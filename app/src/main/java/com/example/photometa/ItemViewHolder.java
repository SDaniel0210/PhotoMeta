package com.example.photometa;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//Serves the use of a fragment for a single item in the list
public class ItemViewHolder extends RecyclerView.ViewHolder {

    ImageView photoImg;
    TextView photonameLb;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        photoImg=itemView.findViewById(R.id.photo_img);
        photonameLb=itemView.findViewById(R.id.photoname_lb);
    }
}
