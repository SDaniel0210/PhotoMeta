package com.example.photometa;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.entity.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;



public class MainFragment extends Fragment {

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri cameraImageUri;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private ActivityResultLauncher<String[]> imagePickerLauncher;
    private AppDatabase db;
    private NavController navController;

    private ExifInterface exifInterface;

    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        context = getContext();
        navController = NavHostFragment.findNavController(this);

        db = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                AppDatabase.class,
                "photo_db"
        ).build();

        takePictureLauncher = registerForActivityResult( //takepic button behav
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        new Thread(() -> {
                            Photo photo = new Photo();
                            //moved cameraImageUri to a variable for multiple calls
                            String uri = cameraImageUri.toString();
                            photo.setImageUri(uri);

                            photo.setTitle(uri.substring(uri.lastIndexOf(File.separator) + 1));
                            photo.setAiStatus("UNKNOWN");

                            extractExif(cameraImageUri, photo);

                            db.photoDao().insert(photo);

                            int count = db.photoDao().getAll().size();

                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Camera image saved. DB count: " + count, Toast.LENGTH_LONG).show()
                            );
                        }).start();
                    } else {
                        Toast.makeText(getContext(), "Picture was not taken", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult( //import button behav
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        Toast.makeText(getContext(), uris.size() + " image selected", Toast.LENGTH_SHORT).show();

                        new Thread(() -> {
                            for (Uri uri : uris) {
                                try {
                                    requireContext().getContentResolver().takePersistableUriPermission(
                                            uri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }
                                Photo photo = new Photo();
                                //moved uri to a variable for multiple calls
                                String uriString = uri.toString();
                                photo.setImageUri(uriString);
                                //This is hardcoded until the image naming is fixed
                                photo.setTitle(uriString.substring(uriString.lastIndexOf("%2F") + 3));
                                photo.setAiStatus("UNKNOWN");

                                extractExif(uri, photo);

                                db.photoDao().insert(photo);
                            }

                            int count = db.photoDao().getAll().size();

                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "DB count: " + count, Toast.LENGTH_LONG).show()
                            );
                        }).start();

                    } else {
                        Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //rng placeholder image on startup
        ImageView bg = view.findViewById(R.id.bgImage);
        int[] images = {
                R.drawable.placeholderbg1,
                R.drawable.placeholderbg2,
                R.drawable.placeholderbg3
        };

        // take a picture button
        ImageButton takepic_btn = view.findViewById(R.id.takepic_btn);
        takepic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST
                    );
                }
            }
        });

        Button import_btn = view.findViewById(R.id.import_btn);

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch(new String[]{"image/*"});
            }
        });

        Button list_btn = view.findViewById(R.id.list_btn);
        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_listFragment);
            }
        });

        Button stats_btn = view.findViewById(R.id.stats_btn);
        stats_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_statsFragment);
            }
        });


        int randomIndex = (int) (Math.random() * images.length);
        bg.setImageResource(images[randomIndex]);

        return view;
    }

    private Uri createImageUri() { //location folder and image saving
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photometa_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoMeta");
        }

        return getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void launchCamera() {
        cameraImageUri = createImageUri();

        if (cameraImageUri != null) {
            takePictureLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(context, "Could not create image location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //permission window!!!
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Extracts values from picture's exif. If the tag doesn't exist, value is null or 0
    public void extractExif(Uri uri, Photo photo) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                photo.setAiStatus("UNKNOWN");
                return;
            }

            exifInterface = new ExifInterface(inputStream);

            photo.setDateTaken(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
            photo.setCameraModel(exifInterface.getAttribute(ExifInterface.TAG_MODEL));
            photo.setDescription(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION));
            photo.setAiStatus(detectAiStatus(uri, exifInterface));

            float[] latLong = new float[2];
            if (exifInterface.getLatLong(latLong)) {
                photo.setLatitude((double) latLong[0]);
                photo.setLongitude((double) latLong[1]);
            } else {
                photo.setLatitude(null);
                photo.setLongitude(null);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String detectAiStatus(Uri uri, ExifInterface exifInterface) {

        if (hasRealCameraEvidence(exifInterface)) {
            return "REAL";
        }

        StringBuilder metadata = new StringBuilder();

        String[] tags = {
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_SOFTWARE,
                ExifInterface.TAG_IMAGE_DESCRIPTION,
                ExifInterface.TAG_USER_COMMENT,
                ExifInterface.TAG_ARTIST,
                ExifInterface.TAG_COPYRIGHT,
                ExifInterface.TAG_MAKER_NOTE
        };

        for (String tag : tags) {
            String value = exifInterface.getAttribute(tag);
            if (value != null) {
                metadata.append(value.toLowerCase()).append(" ");
            }
        }

        String exifData = metadata.toString();

        if (containsStrongAiKeyword(exifData)) {
            return "AI";
        }

        if (containsWeakAiPattern(exifData)) {
            return "AI";
        }

        String rawFileData = readRawFileText(uri);

        if (containsStrongAiKeyword(rawFileData)) {
            return "AI";
        }

        if (containsWeakAiPattern(rawFileData)) {
            return "AI";
        }

        return "UNKNOWN";
    }
    private boolean containsWeakAiPattern(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        String lowerData = data.toLowerCase();

        String[] weakAiKeywords = {
                "prompt",
                "seed",
                "steps",
                "sampler",
                "scheduler",
                "cfg",
                "denoise",
                "checkpoint",
                "model",
                "lora",
                "vae",
                "clip",
                "workflow",
                "nodes",
                "positive",
                "negative"
        };

        int matches = 0;

        for (String keyword : weakAiKeywords) {
            if (lowerData.contains(keyword)) {
                matches++;
            }
        }

        return matches >= 4;
    }
    private boolean containsStrongAiKeyword(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        String lowerData = data.toLowerCase();

        String[] strongAiKeywords = {
                "stable diffusion",
                "stablediffusion",
                "comfyui",
                "automatic1111",
                "a1111",
                "midjourney",
                "dall-e",
                "dalle",
                "openai",
                "chatgpt",
                "novelai",
                "leonardo ai",
                "adobe firefly",
                "fooocus",
                "invokeai",
                "dreamshaper",
                "realvisxl",
                "realvis",
                "juggernaut xl",
                "sdxl",
                "txt2img",
                "img2img",
                "model hash",
                "cfg scale",
                "negative prompt",
                "positive prompt",
                "negative_prompt",
                "positive_prompt",
                "generated by ai",
                "ai generated"
        };

        for (String keyword : strongAiKeywords) {
            if (lowerData.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
    private boolean hasRealCameraEvidence(ExifInterface exifInterface) {
        String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        String fNumber = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);
        String exposureTime = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);

        boolean hasMake = make != null && !make.trim().isEmpty();
        boolean hasModel = model != null && !model.trim().isEmpty();

        boolean hasCameraTechnicalData =
                (fNumber != null && !fNumber.trim().isEmpty()) ||
                        (exposureTime != null && !exposureTime.trim().isEmpty()) ||
                        (focalLength != null && !focalLength.trim().isEmpty()) ||
                        (iso != null && !iso.trim().isEmpty());

        return (hasMake || hasModel) && hasCameraTechnicalData;
    }

    private String readRawFileText(Uri uri) {
        StringBuilder result = new StringBuilder();

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                return "";
            }

            byte[] buffer = new byte[4096];
            int bytesRead;

            int maxBytesToRead = 10 * 1024 * 1024;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1 && totalBytesRead < maxBytesToRead) {
                String chunk = new String(buffer, 0, bytesRead, StandardCharsets.ISO_8859_1);
                result.append(chunk.toLowerCase());

                totalBytesRead += bytesRead;
            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return result.toString();
    }

}