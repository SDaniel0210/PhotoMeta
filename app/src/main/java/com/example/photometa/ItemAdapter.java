package com.example.photometa;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photometa.data.local.entity.Photo;

import com.example.photometa.utils.ImageLoader;
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
        holder.photonameLb.setText(photo.getTitle());
        if (photo.getDescription()!=null){
            holder.descOrPathLb.setText(photo.getDescription());
        }else holder.descOrPathLb.setText(photo.getImageUri());

        if (photo.getImageUri() != null && !photo.getImageUri().isEmpty()) {
            ImageLoader.loadScaledImage(holder.photoImg, Uri.parse(photo.getImageUri()), 400, 400);
        } else {
            holder.photoImg.setImageResource(R.drawable.placeholderbg3); // optional fallback
        }

        holder.itemView.setOnClickListener(v->{
            if(listener!=null){
                listener.OnItemClick(photo.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(int photoId);

    }

    public void setOnClickItemListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
