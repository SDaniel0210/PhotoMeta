package com.example.photometa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private EditText search_txt;

    private AppDatabase db;

    private Button backButton;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list,container,false);
        navController= NavHostFragment.findNavController(this);
        recyclerView=view.findViewById(R.id.photos_rv);
        search_txt=view.findViewById(R.id.search_txt);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter=new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        db = Room.databaseBuilder(
                getActivity().getApplicationContext(),
                AppDatabase.class,
                "photo_db"
        ).build();

        //Database handling
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Photo> photos = db.photoDao().getAll();

            requireActivity().runOnUiThread(() -> {
                adapter.photos=photos;
                adapter.notifyDataSetChanged();
            });
        });

        adapter.setOnClickItemListener(((position) -> {
            //todo: Give position to ItemViewFragment and fill from that
            navController.navigate(R.id.action_listFragment_to_singleitemFragment);
        }));

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
}