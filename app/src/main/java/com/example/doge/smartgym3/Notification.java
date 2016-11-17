package com.example.doge.smartgym3;

import android.graphics.Bitmap;

/**
 * Created by DOGE on 13/11/16.
 */


public class Notification {

    public final static String BRAG = "brag";
    public final static String FRIEND_JOIN = "friend_join";

    public String type;
    public String message; // ie. brag, friend_join
    public int weight;
    public String exercise;
    public int who_from;
    public String who_from_name;
    public Bitmap who_from_profile_pic;

    public Notification(String message, String type, int weight, String exercise, int who_from, String who_from_name, Bitmap who_from_profile_pic){
        this.message = message;
        this.type = type;
        this.weight = weight;
        this.exercise = exercise;
        this.who_from = who_from;
        this.who_from_name = who_from_name;
        this.who_from_profile_pic = who_from_profile_pic;
    }
}
