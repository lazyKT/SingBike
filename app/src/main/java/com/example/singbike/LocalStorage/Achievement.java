package com.example.singbike.LocalStorage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "achievement")
public class Achievement {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo (name="title")
    public String title;

    @ColumnInfo (name="goal")
    public int goal;

    @ColumnInfo (name="current_score")
    public int currentScore;

    @ColumnInfo (name="reward")
    public double reward;

    @ColumnInfo (name="icon_res")
    public String iconResource;

    @ColumnInfo (name="completed")
    public boolean completed;

    public Achievement (String title, int goal, int currentScore, double reward, String iconResource, boolean completed) {
        this.title = title;
        this.goal = goal;
        this.currentScore = currentScore;
        this.reward = reward;
        this.iconResource = iconResource;
        this.completed = completed;
    }

    public String getTitle () { return this.title; }

    public int getGoal () { return this.goal; }

    public boolean getCompleted () { return this.completed; }

    public int getCurrentScore () { return this.currentScore; }
}
