package com.example.photometa.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.photometa.data.local.dao.PhotoDAO;
import com.example.photometa.data.local.entity.Photo;

@Database(entities = {Photo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract PhotoDAO photoDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "photo_db"
                    )
                    .build();
        }
        return instance;
    }

    public static final Migration MIGRATION_1_2 =new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE photos ADD COLUMN make TEXT");
        }
    };
}