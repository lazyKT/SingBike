package com.example.singbike.LocalStorage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao (Data Access Objects)
 * are responsible for defining methods of accessing the database.
 */

@Dao
public interface AchievementDAO {

    @Query("SELECT * FROM achievement")
    List<Achievement> getAll();

    @Insert
    void insertAll (Achievement... achievements);

    @Update
    void updateAchievement (Achievement achievement);

    @Delete
    void delete (Achievement achievement);
}
