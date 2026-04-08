package com.example.photometa;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

import android.net.Uri;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.List;

import androidx.room.Room;
import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.entity.Photo;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
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

        imagePickerLauncher = registerForActivityResult(
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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
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
}