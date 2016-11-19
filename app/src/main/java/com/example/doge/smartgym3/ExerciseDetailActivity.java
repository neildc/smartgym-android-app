package com.example.doge.smartgym3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView madText;
    int exercise_id;
    ExerciseService exerciseService;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_exercise_detail);
        this.setSupportActionBar(myToolbar);
        this.getSupportActionBar().setTitle("Exercise Details");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        exercise_id = getIntent().getIntExtra("exercise_id", 0);

//        madText = (TextView) findViewById(R.id.detailText);
        this.exerciseService = ServiceGenerator.createService(
                ExerciseService.class,
                TokenUtil.getAccessTokenFromSharedPreferences( this )
        );

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.bringToFront();

        getData(exercise_id);
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

    public void setTextForAllTextViews(String exercise,
                          String date,
                          String time,
                          int weight,
                          int resttime,
                          int sets,
                          int reps ) {

        setTextOfTextView(R.id.exercise_detail_exercise_type, exercise);
        setTextOfTextView(R.id.exercise_detail_date, date);
        setTextOfTextView(R.id.exercise_detail_time, time);
        setTextOfTextView(R.id.exercise_detail_weight, Integer.toString(weight));
        setTextOfTextView(R.id.exercise_detail_rest, Integer.toString(resttime));
        setTextOfTextView(R.id.exercise_detail_sets, Integer.toString(sets));
        setTextOfTextView(R.id.exercise_detail_reps, Integer.toString(reps));

    }

    private void setTextOfTextView(int id, String text) {
        TextView tv = (TextView)findViewById(id);
        tv.setText(text);
    }

    private void getData(int exerciseId) {


        Call<JsonObject> call = exerciseService.getExercise(exerciseId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
//                    madText.setText(response.body().toString());

                    /*
                    {
                        "user": 2,
                        "id": 63,
                        "created": "2016-11-19T10:54:30.456878Z",
                        "exercise_type": "Bench Press",
                        "targetReps": 5,
                        "targetSets": 5,
                        "restTime": 5,
                        "station": 1,
                        "weight": 33,
                        "sets": []
                    }
                     */

                    JsonObject data = response.body().getAsJsonObject();
                    String datetimeString = data.get("created").getAsString();

                    DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date date = new Date();

                    try {
                        date = iso8601Format.parse(datetimeString);
                    } catch (ParseException e) {
                        Log.e("EXERCISE_DETAIL", e.toString());
                    }

                    SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, d/M/y");
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

                    String dateString = dateFormatter.format(date);
                    String timeString = timeFormatter.format(date);

                    setTextForAllTextViews(
                            data.get("exercise_type").getAsString()+" Exercise",
                            dateString,
                            timeString,
                            data.get("weight").getAsInt(),
                            data.get("restTime").getAsInt(),
                            data.get("targetSets").getAsInt(),
                            data.get("targetReps").getAsInt()
                    );
                    ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    public void deleteExercise(View v) {
        Call<Integer> call = exerciseService.deleteExercise(this.exercise_id);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            ExerciseDetailActivity.this,
                            "Deleted exercise",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

    }


}
