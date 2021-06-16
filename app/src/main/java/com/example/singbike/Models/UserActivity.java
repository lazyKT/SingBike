package com.example.singbike.Models;

public class UserActivity {

    private final String activityType;
    private final String activityTime;

    public UserActivity (String type, String time) {
        this.activityTime = time;
        this.activityType = type;
    }

    public String getActivityType () { return this.activityType; }
    public String getActivityTime () { return this.activityTime; }

}
