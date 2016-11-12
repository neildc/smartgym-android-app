package com.example.doge.smartgym3;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

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
    private SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Geomanist.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        {
            try {
                mSocket = IO.socket(Constants.CHAT_SERVER_URL);
                mSocket.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
//        database = databaseSetup();
    }

//    public SQLiteDatabase databaseSetup(){
//        SQLiteDatabase db = openOrCreateDatabase("SMARTGYM",MODE_PRIVATE,null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS User(" +
//                "Name           TEXT     NOT NULL," +
//                "Profile        TEXT     NOT NULL," +
//                "AccessToken    TEXT     NOT NULL);"
//        );
//        db.execSQL("CREATE TABLE IF NOT EXISTS Exercises(" +
//                "ID         INTEGER     NOT NULL," +
//                "Name       TEXT        NOT NULL," +
//                "Sets       INTEGER     NOT NULL," +
//                "Reps       INTEGER     NOT NULL," +
//                "RestTime   INTEGER     NOT NULL);"
//        );
//        db.execSQL("CREATE TABLE IF NOT EXISTS History(" +
//                "Exercise   INTEGER     NOT NULL," +
//                "Fail       BOOLEAN     NOT NULL," +
//                "Timestamp  DATETIME    NOT NULL," +
//                "FOREIGN KEY (Exercise) REFERENCES Exercises(ID));"
//        );
//        db.execSQL("CREATE TABLE ProgressionHistory(" +
//                "Exercise       INTEGER     NOT NULL," +
//                "WeightIncrease INTEGER     NOT NULL," +
//                "RepIncrease    INTEGER     NOT NULL," +
//                "Timestamp      DATETIME    NOT NULL," +
//                "FOREIGN KEY (Exercise) REFERENCES Exercises(ID));"
//        );
//        return db;
//    }
    public void setProfilePic(Bitmap profilePic) {mProfilePic = profilePic;}
    public Bitmap getProfilePic() {return mProfilePic;}
    public void setName(String name) {mName = name;}
    public String getName() {return mName;}
    public Socket getSocket() {
        return mSocket;
    }
}
