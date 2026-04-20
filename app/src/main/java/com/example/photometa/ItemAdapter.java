package com.example.photometa;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photometa.data.local.entity.Photo;

import java.io.File;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    List<Photo> photos;

    private OnItemClickListener listener;

    public ItemAdapter(List<Photo> photos){
        this.photos=photos;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Photo photo=photos.get(position);
        Log.d("RV_BIND", "Binding: " + photo.getTitle());
        holder.photonameLb.setText(photo.getTitle());
        holder.itemView.setOnClickListener(v->{
            if(listener!=null){
                listener.OnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);

    }

    public void setOnClickItemListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
