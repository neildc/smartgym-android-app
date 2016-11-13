package com.example.doge.smartgym3;

import android.graphics.Bitmap;

/**
 * Created by DOGE on 13/11/16.
 */

public class Notification {
    public String type;
    public String message; // ie. brag, friend_join
    public int weight;
    public String exercise;
    public String who_from;
    public Bitmap who_from_profile_pic;

    public Notification(String message, String type, int weight, String exercise, String who_from, Bitmap who_from_profile_pic){
        this.message = message;
        this.type = type;
        this.weight = weight;
        this.exercise = exercise;
        this.who_from = who_from;
        this.who_from_profile_pic = who_from_profile_pic;
    }
}
