package com.example.codeappsvs01;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;

@Database(entities = {PlayerResult.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract PlayerResultDao playerResultDao();

    public static class Db{
        AppDatabase adb;
        private static Db db;
        private Db(Context context){
            adb = Room.databaseBuilder(context,
                    AppDatabase.class, "codeapps")
                    .allowMainThreadQueries()
                    .build();
        }
        public static Db getInstance(Context context){
            if(db == null){
                db = new Db(context);
            }
            return db;
        }
        public PlayerResultDao getDAO(){
            return adb.playerResultDao();
        }
    }

    /*public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            Log.d("AppDatabase", "Creando nueva instancia de base de datos");
            INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "CodeAPP.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }*/
}
