package com.example.singbike.LocalStorage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserActivityDAO {

    @Query ("SELECT * FROM userActivity")
    List <UserActivity> getAll();

    @Query ("SELECT * FROM userActivity WHERE userID=:userID")
    List <UserActivity> getActivityByUserID (int userID);

    @Insert
    void insertAll (UserActivity ... activities);

    @Insert
    void insert (UserActivity activity);

    @Delete
    void delete (UserActivity activity);

}
