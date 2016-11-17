package com.example.doge.smartgym3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        exercise_id = getIntent().getIntExtra("exercise_id", 0);

        madText = (TextView) findViewById(R.id.detailText);

        getData(exercise_id);
    }

    private void getData(int exerciseId) {

        ExerciseService exerciseService = ServiceGenerator.createService(
                ExerciseService.class,
                TokenUtil.getAccessTokenFromSharedPreferences( this )
        );

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


}
