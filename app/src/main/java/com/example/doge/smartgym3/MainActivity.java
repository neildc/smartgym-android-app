package com.example.doge.smartgym3;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonObject;

import java.io.IOException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Intent intent;
    private Socket mSocket;
    private Spinner spinner_exercise_type;
    private EditText weight_input;
    private EditText rest_input;
    private EditText sets_input;
    private EditText reps_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setResources();
    }

    private void setResources() {
        intent = new Intent(this, WorkoutGuideActivity.class);
        startButton = (Button) findViewById(R.id.go_button);
        spinner_exercise_type = (Spinner)  findViewById(R.id.spinner_exercise_type);
        weight_input = (EditText)  findViewById(R.id.new_exercise_weight);
        rest_input = (EditText) findViewById(R.id.new_exercise_rest);
        sets_input = (EditText) findViewById(R.id.new_exercise_sets);
        reps_input = (EditText) findViewById(R.id.new_exercise_reps);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!allFieldsNotEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in every field", Toast.LENGTH_LONG).show();
                    // TODO: better error messaging
                } else {

                    int reps = Integer.valueOf(reps_input.getText().toString());
                    int exerciseID = createExerciseOnServer(
                            spinner_exercise_type.getSelectedItem().toString(),
                            Integer.valueOf(weight_input.getText().toString()),
                            Integer.valueOf(rest_input.getText().toString()),
                            Integer.valueOf(sets_input.getText().toString()),
                            reps
                    );
                    intent.putExtra("repcount", reps);
                    intent.putExtra("exercise_id", exerciseID);

                    startActivity(intent);
                }
            }
        });
    }

    private boolean allFieldsNotEmpty() {

        spinner_exercise_type = (Spinner)  findViewById(R.id.spinner_exercise_type);
        weight_input = (EditText)  findViewById(R.id.new_exercise_weight);
        rest_input = (EditText) findViewById(R.id.new_exercise_rest);
        sets_input = (EditText) findViewById(R.id.new_exercise_sets);
        reps_input = (EditText) findViewById(R.id.new_exercise_reps);

        if (
            weight_input.getText().toString().isEmpty() ||
            rest_input.getText().toString().isEmpty() ||
            sets_input.getText().toString().isEmpty() ||
            reps_input.getText().toString().isEmpty())
            return false;
        else {
            return true;
        }

    }

    private int createExerciseOnServer(String exercise, int weight, int restTime, int sets, int reps) {
        String token = TokenUtil.getAccessTokenFromSharedPreferences(MainActivity.this);
        ExerciseService exerciseService = ServiceGenerator.createService(ExerciseService.class, token);
        Call<JsonObject> call = exerciseService.startNewExercise(reps, sets, 1, restTime, weight, exercise);
        try {
            Response<JsonObject> resp = call.execute();
            if (resp.isSuccessful()) {
                JsonObject respBody = resp.body().getAsJsonObject();
                return respBody.get("id").getAsInt();
            } else {
               return -1;
            }
        } catch (IOException e){
            Log.wtf("EXERCISE", "Failed to create new exercise");
            Log.wtf("EXERCISE", e.toString());
            return -1;
        }
    }




}
