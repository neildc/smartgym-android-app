package com.example.doge.smartgym3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.server.NotificationService;
import com.example.doge.smartgym3.server.UserService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.facebook.AccessToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class RankUpActivity extends AppCompatActivity {

    String TAG = this.getClass().getName();

    final int NUM_RESULTS = 3;
    int weight;
    String exercise;

    TextView[] textViews = new TextView[NUM_RESULTS];
    Button[] bragButtons = new Button[NUM_RESULTS];

    int[] userIds = new int[NUM_RESULTS];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_up);

        textViews[0] = (TextView) findViewById(R.id.bragText0);
        textViews[1] = (TextView) findViewById(R.id.bragText1);
        textViews[2] = (TextView) findViewById(R.id.bragText2);

        bragButtons[0] = (Button) findViewById(R.id.bragBtn0);
        bragButtons[1] = (Button) findViewById(R.id.bragBtn1);
        bragButtons[2] = (Button) findViewById(R.id.bragBtn2);

        // Get data from the original exercise params entered by the user
        Intent intent = getIntent();
        this.exercise = intent.getStringExtra("exercise_type");
        this.weight = intent.getIntExtra("weight", 0);
        getFriendsToBragTo(this.exercise, this.weight);
    }

    private void getFriendsToBragTo(String exercise, int weight) {
        UserService userService = ServiceGenerator.createService(
                UserService.class,
                TokenUtil.getAccessTokenFromSharedPreferences(this.getApplicationContext())
        );

        Call<JsonArray> call = userService.getFriendsToBragTo( exercise, weight );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

               if (response.isSuccessful()) {

                   switch (response.code()) {
                       case (200):
                           JsonArray respBody = response.body().getAsJsonArray();

                           for (int i = 0; i < respBody.size(); i ++) {
                               JsonObject data = respBody.get(i).getAsJsonObject();
                               userIds[i] = data.get("user_id").getAsInt();
                               fillInBragElement(i,
                                       data.get("firstname").getAsString(),
                                       data.get("weight").getAsInt()
                               );
                           }
                           break;

                       case(204):
                           Toast.makeText(RankUpActivity.this, "No results", Toast.LENGTH_LONG).show();
                           break;


                       default:
                           Toast.makeText(RankUpActivity.this, "AAAAAA", Toast.LENGTH_LONG).show();

                   }


               } else {
                   Log.wtf(TAG, "Response not successful");
                   Log.wtf(TAG, response.errorBody().toString());
               }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.wtf(TAG, "Call failed");
                Log.wtf(TAG, t.getMessage());
            }
        });

    }

    private void fillInBragElement(int i, String name, int weight) {
            textViews[i].setText(name + " (" + weight + " kg)");
            textViews[i].setVisibility(View.VISIBLE);
            bragButtons[i].setVisibility(View.VISIBLE);
    }

    public void sendBrag(View v) {
        Log.d(TAG,"BRAG");

        // Ehh theres 3 items to go through
        for (int i = 0; i<3; i++) {

            if (v.getId() == bragButtons[i].getId()) {
                Log.v(TAG,"MATCH= " + userIds[i]);

                // Disable the button otherwise users can
                // spam brag
                bragButtons[i].setEnabled(false);


                NotificationService notificationService = ServiceGenerator.createService(
                        NotificationService.class,
                        TokenUtil.getAccessTokenFromSharedPreferences(RankUpActivity.this)
                );

                Call<JsonObject> call = notificationService.brag(
                        userIds[i],
                        weight,
                        exercise
                );

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

            }
        }
    }

    /**
     * Used by the footer "DONE" button
     */
    public void closeRankUpActivity(View v) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }



}
