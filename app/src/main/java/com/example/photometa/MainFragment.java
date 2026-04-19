package com.example.photometa;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.media.Image;
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
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.entity.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
        View view=inflater.inflate(R.layout.fragment_main,container,false);
        context=getContext();
        navController= NavHostFragment.findNavController(this);

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
                            String uri=cameraImageUri.toString();
                            photo.setImageUri(uri);

                            photo.setTitle(uri.substring(uri.lastIndexOf(File.separator)+1));
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
                                Photo photo = new Photo();
                                //moved uri to a variable for multiple calls
                                String uriString=uri.toString();
                                photo.setImageUri(uriString);
                                //This is hardcoded until the image naming is fixed
                                photo.setTitle(uriString.substring(uriString.lastIndexOf("%2F")+3));
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

        Button list_btn=view.findViewById(R.id.list_btn);
        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_listFragment);
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
    public void extractExif(Uri uri, Photo photo){
        try {
            InputStream inputStream= getContext().getContentResolver().openInputStream(uri);
            exifInterface=new ExifInterface(inputStream);
            photo.setDateTaken(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
            photo.setCameraModel(exifInterface.getAttribute(ExifInterface.TAG_MODEL));
            photo.setDescription(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION));
            Double lat=0.0;
            if(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!= null){
                lat=Double.parseDouble(exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            }
            Double lon=0.0;
            if(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)!= null){
                lat=Double.parseDouble(exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            }
            photo.setLatitude(lat);
            photo.setLongitude(lon);
            Log.d("RV_BIND", "Binding: " + photo.getDateTaken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}