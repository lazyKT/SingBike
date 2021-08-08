package com.example.singbike.LocalStorage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * The following class defines App Local Database Configurations and
 * serves as access point to the Database
 */

@Database (entities = Achievement.class, exportSchema = false, version = 2)
public abstract class AchievementDatabase extends RoomDatabase {

    public abstract AchievementDAO achievementDAO();

    private static final String DB_NAME = "sinbike_db";
    private static AchievementDatabase instance;

    public static synchronized AchievementDatabase getInstance (Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder (context.getApplicationContext(), AchievementDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
