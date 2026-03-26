package com.example.photometa.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.photometa.data.local.dao.PhotoDAO;
import com.example.photometa.data.local.entity.Photo;

@Database(entities = {Photo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PhotoDAO photoDao();

}