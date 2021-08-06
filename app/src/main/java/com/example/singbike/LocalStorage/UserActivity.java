package com.example.singbike.LocalStorage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "userActivity")
public class UserActivity {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo (name = "type")
    public String type;

    @ColumnInfo (name = "url")
    public String url;

    @ColumnInfo (name = "userID")
    public int userID;

    @ColumnInfo (name = "time")
    public String time;


    public UserActivity (String type, String url, int userID, String time) {
        this.type = type;
        this.url = url;
        this.userID = userID;
        this.time = time;
    }

    public String getType () { return this.type; }

    public String getUrl () { return this.url; }

    public int getId () { return this.id; }

    public String getTime () { return this.time; }

    public int getUserID () { return this.userID; }
}
