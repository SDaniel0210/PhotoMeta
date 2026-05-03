package com.example.photometa;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.photometa.data.local.AppDatabase;
import com.example.photometa.data.local.dao.PhotoDAO;
import com.example.photometa.data.local.entity.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private EditText search_txt;
    private Spinner search_spn;

    private AppDatabase db;

    private Button backButton;
    private NavController navController;
    ArrayList<String> spinners=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        navController= NavHostFragment.findNavController(this);
        recyclerView=view.findViewById(R.id.photos_rv);
        search_txt=view.findViewById(R.id.search_txt);
        search_spn=view.findViewById(R.id.search_spn);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter=new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        spinners.add("Title");
        spinners.add("Description");
        spinners.add("Date Taken");
        spinners.add("Camera Model");
        spinners.add("Coordinates");
        spinners.add("AI Status");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), R.layout.visible_spinner, spinners);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        search_spn.setAdapter(arrayAdapter);

        db = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                AppDatabase.class,
                "photo_db"
        ).build();

        loadList();

        adapter.setOnClickItemListener(((photoId) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("photoId",photoId);
            navController.navigate(R.id.action_listFragment_to_singleitemFragment, bundle);
        }));

        search_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                loadList();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });



        backButton=view.findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_listFragment_to_mainFragment);
            }
        });

        Button stats_btn = view.findViewById(R.id.stats_btn);
        stats_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_listFragment_to_statsFragment);
            }
        });

        return view;
    }

    public void loadList(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String search="%"+search_txt.getText()+"%";
            List<Photo> photos = null;

            switch (search_spn.getSelectedItem().toString()){
                case "Title":
                    photos = db.photoDao().getAllByTitle(search);
                    break;
                case "Description":
                    photos = db.photoDao().getAllByDescription(search);
                    break;
                case "Date Taken":
                    photos = db.photoDao().getAllByDate(search);
                    break;
                case "Camera Model":
                    photos = db.photoDao().getAllByCamera(search);
                    break;
                case "Coordinates":
                    String[] coords= search_txt.getText().toString().split("-");
                    String lat=coords[0];
                    String lon;
                    if(coords.length<2){
                        lon="0.0";
                    }else lon=coords[1];
                    photos=db.photoDao().getAllByCoords(lat,lon);
                    break;
                case "AI Status":
                    photos = db.photoDao().getAllByAI(search);
                    break;
            }
            //This is to make the lambda accept the non-final list
            List<Photo> finalPhotos= photos;
            requireActivity().runOnUiThread(() -> {
                adapter.photos=finalPhotos;
                adapter.notifyDataSetChanged();
            });
        });
    }
}