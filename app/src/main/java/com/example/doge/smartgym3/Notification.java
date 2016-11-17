package com.example.doge.smartgym3;

import android.graphics.Bitmap;

/**
 * Created by DOGE on 13/11/16.
 */


public class Notification {

    public final static String BRAG = "brag";
    public final static String FRIEND_JOIN = "friend_join";

    int remote_id;
    public String type;

    public Notification() {

    }

    public Notification(String type, int remote_id){
        this.remote_id = remote_id;
        this.type = type;
    }
}
