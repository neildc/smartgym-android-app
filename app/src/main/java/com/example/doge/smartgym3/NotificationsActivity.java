package com.example.doge.smartgym3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.doge.smartgym3.server.NotificationService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private WorkoutApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        app = (WorkoutApplication) getApplication();
        this.setSupportActionBar(myToolbar);
        this.getSupportActionBar().setTitle("Notifications");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<Notification> arrayOfRecentNotifications = new ArrayList<>();

//        Notification newNotification;
////        newNotification = new Notification(null, "brag", 300, "Bicep Curls", "Jason", null);
////        arrayOfRecentNotifications.add(newNotification);
////        newNotification = new Notification(null, "friend_join", 0, null, "Bill", app.getProfilePic());
////        arrayOfRecentNotifications.add(newNotification);
////        newNotification = new Notification(null, "friend_join", 0, null, "Henry", app.getProfilePic());
//        arrayOfRecentNotifications.add(newNotification);


    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNotifications();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        // other menu select events may be present here

        return super.onOptionsItemSelected(item);
    }


    public void updateNotifications() {

        String serverAccessToken = TokenUtil.getAccessTokenFromSharedPreferences(this.getApplicationContext());
        NotificationService notificationService = ServiceGenerator.createService(NotificationService.class, serverAccessToken);
        Call<JsonArray> call = notificationService.getNotificationsForCurrentUser();

        //        {
        //            "id": 2,
        //                "exercises": [
        //                    {
        //                        "user": 2,
        //                            "id": 1,
        //                            "exercise_type": "",
        //                            "targetReps": 5,
        //                            "targetSets": 5,
        //                            "restTime": 5,
        //                            "station": 1,
        //                            "weight": 30,
        //                            "sets": [
        //                                {
        //                                    "id": 1,
        //                                        "successfulReps": 4,
        //                                        "time": 30,
        //                                        "exercise": 1
        //                                },
        //                            ]
        //                    },

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("NOTIFICATION", "Got response");

                if (response.isSuccessful()) {
                    JsonArray notificationsJSONArray = response.body().getAsJsonArray();
                    ArrayList<Notification> notificationArrayList = new ArrayList<>();

                    for (int i = 0; i < notificationsJSONArray.size(); i++) {
                        JsonObject notification = notificationsJSONArray.get(i).getAsJsonObject();
                        switch (notification.get("type").getAsString()) {
                            case (Notification.BRAG):
                                parseAsBrag(notificationArrayList, notification);
                                break;

                            default:
                        }
                    }


                    NotificationListAdapter recentAdapter = new NotificationListAdapter(NotificationsActivity.this, notificationArrayList);
                    ListView notificationListView = (ListView) findViewById(R.id.notification_list);
                    notificationListView.setAdapter(recentAdapter);

//                    setListViewHeightBasedOnItems(notificationListView);

//                    ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBarExerciseList);
//                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                } else {
                    // TODO: Error handling
                    Log.wtf("NOTIFICATION", "Response error");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.wtf("NOTIFICATION", t.getMessage());
            }
        });

    }

    private void parseAsBrag(ArrayList<Notification> notificationArrayList, JsonObject notification) {
        String data = notification.get("data").getAsString();
        String exercise;
        int weight;
        int from;
        String name;

        try {
            JSONObject object = (JSONObject) new JSONTokener(data).nextValue();
            exercise = object.getString("exercise");
//                            JSONArray locations = object.getJSONArray("locations");
            weight = object.getInt("weight");
            from = object.getInt("from_id");
            name = object.getString("from_first");

            notificationArrayList.add(new Notification(
                    notification.get("message").getAsString(),
                    Notification.BRAG,
                    weight,
                    exercise,
                    from,
                    name,
                    null
            ));
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
        }
    }
}
