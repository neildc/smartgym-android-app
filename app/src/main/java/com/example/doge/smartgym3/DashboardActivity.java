package com.example.doge.smartgym3;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        app = (WorkoutApplication) getApplication();
        CircleImageView profilePic = (CircleImageView) findViewById(R.id.circleView);
        TextView mWelcomeText = (TextView) findViewById(R.id.welcomeText);
        TextView mStatusText = (TextView) findViewById(R.id.statusText);
        TextView mRecentHeader = (TextView) findViewById(R.id.recentHeader);
        TextView mExercisesHeader = (TextView) findViewById(R.id.exercises_header);
        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBarExerciseList);

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
        mRecentHeader.setText("Recent");
        mExercisesHeader.setText("Exercises");
        mProgressBar.setIndeterminate(true);


//        ArrayList<Exercise> arrayOfRecentExercises = new ArrayList<>();
//
//        Exercise newExercise;
//        newExercise = new Exercise("Bicep Curl", 3, 10, 30);
//        arrayOfRecentExercises.add(newExercise);
//        newExercise = new Exercise("Bench Press", 3, 10, 30);
//        arrayOfRecentExercises.add(newExercise);
//        newExercise = new Exercise("Bent Over Rows", 3, 10, 30);
//        arrayOfRecentExercises.add(newExercise);
//
//        ExerciseListAdapter recentAdapter = new ExerciseListAdapter(this, arrayOfRecentExercises);
//        ListView recentListView = (ListView) findViewById(R.id.recent_list);
//        recentListView.setAdapter(recentAdapter);
//        setListViewHeightBasedOnItems(recentListView);
//
//        ArrayList<Exercise> arrayOfExercises = new ArrayList<>();
//
//        newExercise = new Exercise("Bicep Curl", 3, 10, 30);
//        arrayOfExercises.add(newExercise);
//        newExercise = new Exercise("Bench Press", 3, 10, 30);
//        arrayOfExercises.add(newExercise);
//        newExercise = new Exercise("Bent Over Rows", 3, 10, 30);
//        arrayOfExercises.add(newExercise);




    }

    public void updateExercises() {

        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBarExerciseList);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        String serverAccessToken = TokenUtil.getAccessTokenFromSharedPreferences(this.getApplicationContext());
        final ExerciseService exerciseService = ServiceGenerator.createService(ExerciseService.class, serverAccessToken);
        Call<JsonObject> call = exerciseService.getExercisesForCurrentUser();

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

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {


                if (response.isSuccessful()) {
                    JsonArray exercisesJSONArray = response.body().get("exercises").getAsJsonArray();
                    ArrayList<Exercise> exerciseArrayList = new ArrayList<Exercise>();

                    for (int i = 0; i < exercisesJSONArray.size(); i++) {
                        JsonObject exercise = exercisesJSONArray.get(i).getAsJsonObject();
                        exerciseArrayList.add(new Exercise(
                                exercise.get("exercise_type").getAsString(),
                                exercise.get("targetSets").getAsInt(),
                                exercise.get("targetReps").getAsInt(),
                                exercise.get("restTime").getAsInt(),
                                exercise.get("weight").getAsInt(),
                                exercise.get("created").getAsString()
                        ));
                    }

                    ExerciseListAdapter adapter = new ExerciseListAdapter(DashboardActivity.this, exerciseArrayList);
                    ListView exerciseListView = (ListView) findViewById(R.id.exercises_list);
                    exerciseListView.setAdapter(adapter);
                    setListViewHeightBasedOnItems(exerciseListView);

                    ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBarExerciseList);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);


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

//    private ArrayList<Exercise> downloadUsers

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

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
}


