package com.example.photometa.data.local.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
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

    @Query("SELECT title FROM photos where id=0")
    String getFirstTitle();

    @Query("SELECT COUNT(*) FROM photos")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM photos WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    int getGpsCount();

    @Query("SELECT COUNT(*) FROM photos WHERE aiStatus = 'AI'")
    int getAiVerifiedCount();

    @Query("SELECT cameraModel FROM photos")
    List<String> getTopCameras();

    @Delete
    void delete(Photo photo);



}
