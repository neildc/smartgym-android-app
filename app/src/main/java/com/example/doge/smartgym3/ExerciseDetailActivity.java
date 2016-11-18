package com.example.doge.smartgym3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView madText;
    int exercise_id;
    ExerciseService exerciseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        exercise_id = getIntent().getIntExtra("exercise_id", 0);

        madText = (TextView) findViewById(R.id.detailText);
        this.exerciseService = ServiceGenerator.createService(
                ExerciseService.class,
                TokenUtil.getAccessTokenFromSharedPreferences( this )
        );

        getData(exercise_id);
    }

    private void getData(int exerciseId) {


        Call<JsonObject> call = exerciseService.getExercise(exerciseId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    madText.setText(response.body().toString());
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
