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

    @Insert
    void insertAll (UserActivity ... activities);

    @Insert
    void insert (UserActivity activity);

    @Delete
    void delete (UserActivity activity);

}
