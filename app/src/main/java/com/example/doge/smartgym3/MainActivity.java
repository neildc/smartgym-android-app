package com.example.doge.smartgym3;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.server.ExerciseService;
import com.example.doge.smartgym3.util.MyPagerAdapter;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonObject;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.io.IOException;
import java.util.List;

import io.socket.client.Socket;
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
    private WorkoutApplication app;
    private FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vpPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        inkPageIndicator.setViewPager(vpPager);

        setResources();
    }

    private void setResources() {
//        intent = new Intent(this, RankUpActivity.class);
        intent = new Intent(this, WorkoutGuideActivity.class);

//        startButton = (Button) findViewById(R.id.go_button);
        RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer);
        spinner_exercise_type = (Spinner)  findViewById(R.id.spinner_exercise_type);
        weight_input = (EditText)  findViewById(R.id.new_exercise_weight);
        rest_input = (EditText) findViewById(R.id.new_exercise_rest);
        sets_input = (EditText) findViewById(R.id.new_exercise_sets);
        reps_input = (EditText) findViewById(R.id.new_exercise_reps);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        app = (WorkoutApplication) getApplication();
        this.setSupportActionBar(myToolbar);
        this.getSupportActionBar().setTitle("Workout Setup");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!allFieldsNotEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in every field", Toast.LENGTH_LONG).show();
                    // TODO: better error messaging
                } else {
                    String exercise_type = spinner_exercise_type.getSelectedItem().toString();
                    int weight = Integer.valueOf(weight_input.getText().toString());

                    int reps = Integer.valueOf(reps_input.getText().toString());
                    int resttime = Integer.valueOf(rest_input.getText().toString());
                    int sets = Integer.valueOf(sets_input.getText().toString());
                    int exerciseID = createExerciseOnServer(
                            exercise_type,
                            weight,
                            Integer.valueOf(rest_input.getText().toString()),
                            Integer.valueOf(sets_input.getText().toString()),
                            reps
                    );
                    intent.putExtra("repcount", reps);
                    intent.putExtra("exercise_id", exerciseID);
                    intent.putExtra("exercise_type", exercise_type);
                    intent.putExtra("resttime", resttime);
                    intent.putExtra("weight", weight);
                    intent.putExtra("sets", sets);

                    startActivity(intent);
                }
            }
        });

        spinner_exercise_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // madhack but it'll force the graph to be recreated with the new
                // selected exercise

                // Graph will use the value that the spinner text is set to
                resetFragment();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    private void resetFragment() {

        /**
         * yeaaahh...
         */
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment f : fragments) {
            if (f != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(f)
                        .attach(f)
                        .commit();
            }
        }

    }

    private boolean emptyEditField(EditText ef) {
        if (ef.getText().toString().isEmpty()) {
            ef.setError("Can't be empty");
            return true;
        } else {
            return false;
        }
    }

    private boolean allFieldsNotEmpty() {

        if (emptyEditField(weight_input) ||
            emptyEditField(rest_input) ||
            emptyEditField(sets_input)||
            emptyEditField(reps_input))

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
