package com.example.doge.smartgym3;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by DOGE on 14/10/2016.
 */

public class WorkoutApplication extends Application {
    private Socket mSocket;
    private String mName;
    private Bitmap mProfilePic;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AvenirBook.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        {
            try {
                mSocket = IO.socket(Constants.BENCH_SERVER_URL);
                mSocket.connect();
                if (mSocket.connected() == false) {
                    Log.e("WS", "Failed to connect to socket");
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
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
