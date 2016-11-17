package com.example.doge.smartgym3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.gcm.RegistrationIntentService;
import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardActivity extends AppCompatActivity {
    private WorkoutApplication app;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "DashboardActivity";
    int user_id;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private ProgressBar mRegistrationProgressBar;
//    private TextView mInformationTextView;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        app = (WorkoutApplication) getApplication();
        CircleImageView profilePic = (CircleImageView) findViewById(R.id.circleView);
        TextView mWelcomeText = (TextView) findViewById(R.id.welcomeText);
        TextView mStatusText = (TextView) findViewById(R.id.statusText);
        TextView mExercisesHeader = (TextView) findViewById(R.id.exercises_header);

        RelativeLayout mFooter = (RelativeLayout) findViewById(R.id.footer);
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        profilePic.setImageBitmap(app.getProfilePic());
        mWelcomeText.setText("Welcome back , " + app.getName());
        mStatusText.setText("Today is : ARMS");
        mExercisesHeader.setText("Recent Exercises");

        //GCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean("GCM.SENT_TOKEN_TO_SERVER", false);
                if (sentToken) {
                    Log.d("GCM", "sentTOken");
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    Log.d("GCM", "tokenError");
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
//        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();
        Log.d("GCM", "started reciever");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.d("GCM", "Starting RegistrationIntentService");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void updateExercises() {

        String serverAccessToken = TokenUtil.getAccessTokenFromSharedPreferences(this.getApplicationContext());
        final ExerciseService exerciseService = ServiceGenerator.createService(ExerciseService.class, serverAccessToken);
        Call<JsonObject> call = exerciseService.getExercisesForCurrentUser();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    int numNotifications= response.body().get("notifications_count").getAsInt();
                    setUser_id(response.body().get("id").getAsInt());
                    updateNotificationBubble(numNotifications);

                    JsonArray exercisesJSONArray = response.body().get("exercises").getAsJsonArray();
                    final ArrayList<Exercise> exerciseArrayList = new ArrayList<Exercise>();

                    for (int i = 0; i < exercisesJSONArray.size(); i++) {
                        JsonObject exercise = exercisesJSONArray.get(i).getAsJsonObject();
                        exerciseArrayList.add(new Exercise(
                                exercise.get("exercise_type").getAsString(),
                                exercise.get("targetSets").getAsInt(),
                                exercise.get("targetReps").getAsInt(),
                                exercise.get("restTime").getAsInt(),
                                exercise.get("weight").getAsInt(),
                                exercise.get("created").getAsString(),
                                exercise.get("id").getAsInt()

                        ));
                    }

                    ExerciseListAdapter adapter = new ExerciseListAdapter(DashboardActivity.this, exerciseArrayList);
                    ListView exerciseListView = (ListView) findViewById(R.id.exercises_list);
                    exerciseListView.setAdapter(adapter);

                    ExerciseListAdapter.setListViewHeightBasedOnItems(exerciseListView);
                    setItemOnClickListener(exerciseArrayList, exerciseListView);



                } else {
                    // TODO: Error handling
                    Log.wtf("EXERCISE", "Response error");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.wtf("EXERCISE", call.toString());

            }
        });

    }

    private void setItemOnClickListener(final ArrayList<Exercise> exerciseArrayList, ListView exerciseListView) {
        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long item) {
                Exercise clickedOnEx = exerciseArrayList.get(pos);

                Intent intent = new Intent(
                        DashboardActivity.this,
                        ExerciseDetailActivity.class);
                intent.putExtra("exercise_id", clickedOnEx.id);
                startActivity(intent);
            }
        });
    }

//    private ArrayList<Exercise> downloadUsers

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void moveToNotifications(View notificationButton){
        Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateExercises();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter("GCM.REGISTRATION_COMPLETE"));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("GCM", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void setUser_id(int id) {
        this.user_id = id;
    }

    public void goToAllExercisesForUser(View v) {
        Intent intent = new Intent(
                DashboardActivity.this,
                AllExercisesActivity.class);
        intent.putExtra("user_id", this.user_id);
        startActivity(intent);
    }

    public void updateNotificationBubble(int count) {
        TextView bubble = (TextView) findViewById(R.id.notificationBubble);
        bubble.setText(Integer.toString(count));
    }

}

