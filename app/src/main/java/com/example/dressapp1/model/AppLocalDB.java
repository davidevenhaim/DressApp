package com.example.dressapp1.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.dressapp1.MyApplication;

@Database(entities = {Product.class,User.class}, version = 4)
@TypeConverters(ProductListConverter.class)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract UserDao userDao();
}


public class AppLocalDB {
    static public final AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbDressApp.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDB(){}
}
