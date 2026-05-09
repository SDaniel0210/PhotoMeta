package com.example.photometa;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.entity.Photo;

import com.bumptech.glide.Glide;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleitemFragment extends Fragment {

    Photo photo;
    int id;
    private AppDatabase db;
    private Button back_btn, delete_btn, save_btn;
    private NavController navController;
    private TextView nameTxt, descTxt, dateTxt, cameraTxt, makeTxt, coordsTxt, aiTxt;
    private ImageView imageView;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_singleitem,container,false);
        navController= NavHostFragment.findNavController(this);

        nameTxt=view.findViewById(R.id.name_txt);
        descTxt=view.findViewById(R.id.description_txt);
        dateTxt=view.findViewById(R.id.date_txt);
        cameraTxt=view.findViewById(R.id.camera_txt);
        makeTxt = view.findViewById(R.id.make_txt);
        coordsTxt=view.findViewById(R.id.coordinates_txt);
        aiTxt=view.findViewById(R.id.ai_txt);
        imageView=view.findViewById(R.id.imageView);

        db = AppDatabase.getInstance(getContext());

        id=getArguments().getInt("photoId");

        db.photoDao().getPhoto(id).observe(getViewLifecycleOwner(), updatedPhoto -> {
            if (updatedPhoto == null) return;

            photo = updatedPhoto;

            nameTxt.setText(photo.getTitle());
            descTxt.setText(photo.getDescription());
            dateTxt.setText(photo.getDateTaken());
            cameraTxt.setText(photo.getCameraModel());
            aiTxt.setText(photo.getAiStatus());

            if ("REAL".equals(photo.getAiStatus()) && photo.getMake() != null && !photo.getMake().trim().isEmpty()) {
                makeTxt.setText(photo.getMake());
            } else {
                makeTxt.setText("");
            }

            if(photo.getLatitude()!=null && photo.getLongitude()!=null){
                coordsTxt.setText(photo.getLatitude().toString()+"-"+photo.getLongitude().toString());
            }else coordsTxt.setText("0-0");

            if (photo.getImageUri() != null && !photo.getImageUri().isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(photo.getImageUri()))
                        .override(1200, 1200)
                        .fitCenter()
                        .into(imageView);
            } else {
                imageView.setImageDrawable(null);
            }
        });

        back_btn=view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_singleitemFragment_to_listFragment);
            }
        });

        save_btn=view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setTitle(nameTxt.getText().toString());
                if(descTxt.getText().length()>=1){
                    photo.setDescription(descTxt.getText().toString());
                }
                photo.setDateTaken(dateTxt.getText().toString());
                photo.setCameraModel(cameraTxt.getText().toString());
                String s =coordsTxt.getText().toString();
                if(s.contains("-")){
                    try{
                        String[] coords=s.split("-");
                        photo.setLatitude(Double.parseDouble(coords[0]));
                        photo.setLongitude(Double.parseDouble(coords[1]));
                    }catch (NumberFormatException e){
                        Toast.makeText(getContext(), "Invalid coordinates format", Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(getContext(), "Coordinates must be in format: lat-lon", Toast.LENGTH_SHORT).show();
                photo.setAiStatus(aiTxt.getText().toString());

                executor.execute(() -> {
                    db.photoDao().update(photo);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Photo updated", Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });

        delete_btn=view.findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.execute(() -> {
                    db.photoDao().delete(photo);

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Photo deleted ("+photo.getTitle()+")", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_singleitemFragment_to_listFragment);
                    });
                });
            }
        });

        return view;
    }
}