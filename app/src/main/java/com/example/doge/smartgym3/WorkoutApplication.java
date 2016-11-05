package com.example.doge.smartgym3;

import android.app.Application;
import android.graphics.Bitmap;
import android.provider.SyncStateContract;

import com.facebook.login.LoginManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by DOGE on 14/10/2016.
 */

public class WorkoutApplication extends Application {
    private Socket mSocket;
    private String mName;
    private Bitmap mProfilePic;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void setProfilePic(Bitmap profilePic) {mProfilePic = profilePic;}
    public Bitmap getProfilePic() {return mProfilePic;}
    public void setName(String name) {mName = name;}
    public String getName() {return mName;}
    public Socket getSocket() {
        return mSocket;
    }
}
