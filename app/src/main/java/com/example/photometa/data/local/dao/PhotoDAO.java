package com.example.photometa.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.DeleteTable;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.photometa.data.local.entity.Photo;

import java.util.List;
@Dao
public interface PhotoDAO {
    @Insert
    void insert(Photo photo);

    @Query("SELECT * FROM photos where id=:id")
    LiveData<Photo> getPhoto(int id);

    @Query("SELECT * FROM photos")
    LiveData<List<Photo>> getAll();

    @Query("SELECT * FROM photos WHERE title LIKE :searchSpec ORDER BY title")
    LiveData<List<Photo>> getAllByTitle(String searchSpec);

    @Query("SELECT * FROM photos WHERE description LIKE :searchSpec ORDER BY description")
    LiveData<List<Photo>> getAllByDescription(String searchSpec);

    @Query("SELECT * FROM photos WHERE dateTaken LIKE :searchSpec ORDER BY dateTaken")
    LiveData<List<Photo>> getAllByDate(String searchSpec);

    @Query("SELECT * FROM photos WHERE cameraModel LIKE :searchSpec ORDER BY cameraModel")
    LiveData<List<Photo>> getAllByCamera(String searchSpec);

    @Query("SELECT * FROM photos WHERE latitude LIKE :searchLat OR longitude LIKE :searchLon ORDER BY latitude")
    LiveData<List<Photo>> getAllByCoords(String searchLat, String searchLon);

    @Query("SELECT * FROM photos WHERE aiStatus LIKE :searchSpec ORDER BY aiStatus")
    LiveData<List<Photo>> getAllByAI(String searchSpec);

    @Query("SELECT title FROM photos where id=0")
    String getFirstTitle();

    @Query("SELECT COUNT(*) FROM photos")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM photos WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    int getGpsCount();

    @Query("SELECT COUNT(*) FROM photos WHERE aiStatus = 'AI'")
    int getAiVerifiedCount();

    @Query("SELECT cameraModel FROM photos")
    LiveData<List<String>> getTopCameras();

    @Update
    void update(Photo photo);

    @Delete
    void delete(Photo photo);



}
