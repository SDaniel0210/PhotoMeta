package com.example.photometa;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.view.View;
import android.content.ContentValues;
import android.os.Build;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.room.Room;
import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.entity.Photo;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri cameraImageUri;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private ActivityResultLauncher<String[]> imagePickerLauncher;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "photo_db"
        ).build();

        takePictureLauncher = registerForActivityResult( //takepic button behav
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        new Thread(() -> {
                            Photo photo = new Photo();
                            photo.setImageUri(cameraImageUri.toString());
                            photo.setAiStatus("UNKNOWN");

                            db.photoDao().insert(photo);

                            int count = db.photoDao().getAll().size();

                            runOnUiThread(() ->
                                    Toast.makeText(this, "Camera image saved. DB count: " + count, Toast.LENGTH_LONG).show()
                            );
                        }).start();
                    } else {
                        Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult( //import button behav
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        Toast.makeText(this, uris.size() + " image selected", Toast.LENGTH_SHORT).show();

                        new Thread(() -> {
                            for (Uri uri : uris) {
                                Photo photo = new Photo();
                                photo.setImageUri(uri.toString());
                                photo.setAiStatus("UNKNOWN");

                                db.photoDao().insert(photo);
                            }

                            int count = db.photoDao().getAll().size();

                            runOnUiThread(() ->
                                    Toast.makeText(this, "DB count: " + count, Toast.LENGTH_LONG).show()
                            );
                        }).start();

                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        //rng placeholder image on startup
        ImageView bg = findViewById(R.id.bgImage);
        int[] images = {
                R.drawable.placeholderbg1,
                R.drawable.placeholderbg2,
                R.drawable.placeholderbg3
        };

        // take a picture button
        ImageButton takepic_btn = findViewById(R.id.takepic_btn);
        takepic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST
                    );
                }
            }
        });

        Button import_btn = findViewById(R.id.import_btn);

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch(new String[]{"image/*"});
            }
        });

        Button list_btn=findViewById(R.id.list_btn);
        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        int randomIndex = (int) (Math.random() * images.length);
        bg.setImageResource(images[randomIndex]);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private Uri createImageUri() { //location folder and image saving
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photometa_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoMeta");
        }

        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    private void launchCamera() {
        cameraImageUri = createImageUri();

        if (cameraImageUri != null) {
            takePictureLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(MainActivity.this, "Could not create image location", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { //permission window!!!
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}