package com.example.singbike.LocalStorage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/* UserActivity Local Database Instance */
@Database (entities = UserActivity.class, exportSchema = false, version = 1)
public abstract class UserActivityDatabase extends RoomDatabase {

    public abstract UserActivityDAO userActivityDAO ();

    private static final String DB_NAME = "sinbike_db";
    private static UserActivityDatabase instance;

    public static synchronized UserActivityDatabase getInstance (Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder (context.getApplicationContext(), UserActivityDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

}
