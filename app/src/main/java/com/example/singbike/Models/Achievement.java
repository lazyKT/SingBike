package com.example.singbike.Models;

import android.annotation.SuppressLint;

import java.util.Locale;

public class Achievement {

    private String name; // achievement name
    private int max, current; // maximum and current value of achievement
    private double reward; // reward upn completed achievement
    private int drawableResource; // achievement icon

    public Achievement (String name, int max, int current, double reward, int drawableResource) {
        this.name = name;
        this.max = max;
        this.current = current;
        this.reward = reward;
        this.drawableResource = drawableResource;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setMax (int max) {
        this.max = max;
    }

    public void setCurrent (int current) {
        this.current = current;
    }

    public void setReward (double reward) {
        this.reward = reward;
    }

    public void setDrawableResource (int res) {
        this.drawableResource = res;
    }

    public double getReward() {
        return reward;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    public int getDrawableResource () {
        return drawableResource;
    }

    public String getTitle () {
        /* title to show on achievement card */
        return String.format(Locale.ENGLISH, "%s (%d/%d).", this.name, this.current, this.max);
    }
}
