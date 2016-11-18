package com.example.doge.smartgym3;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.doge.smartgym3.DashboardActivity;
import com.example.doge.smartgym3.Exercise;
import com.example.doge.smartgym3.ExerciseListAdapter;
import com.example.doge.smartgym3.R;
import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllExercisesActivity extends AppCompatActivity {

    String TAG = AllExercisesActivity.class.getName();

    int user_id;
    private WorkoutApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exercises);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_all_exercise);
        app = (WorkoutApplication) getApplication();
        this.setSupportActionBar(myToolbar);
        this.getSupportActionBar().setTitle("All Exercises");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        this.user_id = intent.getIntExtra("user_id", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateExercises();
    }

    public void updateExercises() {

        String serverAccessToken = TokenUtil.getAccessTokenFromSharedPreferences(this.getApplicationContext());
        final ExerciseService exerciseService = ServiceGenerator.createService(ExerciseService.class, serverAccessToken);
        Call<JsonArray> call = exerciseService.getExercisesForUser(this.user_id);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.isSuccessful()) {

                    JsonArray exercisesJSONArray = response.body().getAsJsonArray();
                    final ArrayList<Exercise> exerciseArrayList = new ArrayList<>();

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

                    ExerciseListAdapter adapter = new ExerciseListAdapter(AllExercisesActivity.this, exerciseArrayList);
                    ListView exerciseListView = (ListView) findViewById(R.id.exercises_list_all);
                    exerciseListView.setAdapter(adapter);
                    ExerciseListAdapter.setListViewHeightBasedOnItems(exerciseListView);

                    setItemOnClickListener(exerciseArrayList, exerciseListView);



                } else {
                    // TODO: Error handling
                    Log.wtf(TAG, "Response error");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.wtf(TAG, call.toString());

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
                        AllExercisesActivity.this,
                        ExerciseDetailActivity.class);
                intent.putExtra("exercise_id", clickedOnEx.id);
                startActivity(intent);
            }
        });
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
}
