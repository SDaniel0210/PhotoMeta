package com.example.photometa.data.local.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.photometa.data.local.entity.Photo;

import java.util.List;
@Dao
public interface PhotoDAO {
    @Insert
    void insert(Photo photo);

    @Query("SELECT * FROM photos")
    List<Photo> getAll();

    @Delete
    void delete(Photo photo);

}
