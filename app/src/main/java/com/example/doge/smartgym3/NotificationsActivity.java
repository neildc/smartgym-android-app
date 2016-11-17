package com.example.doge.smartgym3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import retrofit2.http.DELETE;

public class NotificationsActivity extends AppCompatActivity {

    String TAG = NotificationsActivity.class.getName();

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

                    setItemOnClickListener(notificationArrayList, notificationListView);


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
        int id = notification.get("id").getAsInt();
        int exercise_id;
        String from_name;

        try {
            JSONObject object = (JSONObject) new JSONTokener(data).nextValue();
            exercise = object.getString("exercise");
            exercise_id = object.getInt("exerciseID");
            weight = object.getInt("weight");
            from_name = object.getString("from_name");

            notificationArrayList.add(new BragNotification(
                    Notification.BRAG,
                    id,
                    exercise,
                    exercise_id,
                    weight,
                    from_name
            ));
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
        }
    }


    private void setItemOnClickListener(final ArrayList<Notification> notificationArrayList, ListView notificationListView) {
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long item) {

                Notification n = notificationArrayList.get(pos);

                deleteNotification(n);

                if (n.getClass() == BragNotification.class) {

                    BragNotification brag = (BragNotification) n;

                    Intent intent = new Intent(
                            NotificationsActivity.this,
                            ExerciseDetailActivity.class);
                    intent.putExtra("exercise_id", brag.exercise_id);
                    startActivity(intent);

                }

            }
        });
    }

    private void deleteNotification(Notification n) {
        NotificationService notificationService = ServiceGenerator.createService(
                NotificationService.class,
                TokenUtil.getAccessTokenFromSharedPreferences(NotificationsActivity.this)
        );

        Call<Integer> call = notificationService.deleteNotification(n.remote_id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.d(TAG, "Delete notification result = " + response.code());
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e(TAG, "Delete notification failed");
                Log.e(TAG, t.getMessage());

            }
        });
    }
}
