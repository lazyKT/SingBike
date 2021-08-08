package com.example.singbike.Services;

import android.location.Location;

public class SendLocationUpdatesToActivity {

    private Location location;

    public SendLocationUpdatesToActivity (Location location) {
        this.location = location;
    }

    public void setLocation (Location location) {
        this.location = location;
    }

    public Location getLocation () {
        return this.location;
    }

}
