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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardActivity extends AppCompatActivity {
    private WorkoutApplication app;
    private TextView mWelcomeText;
    private TextView mStatusText;

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

        ArrayList<Exercise> arrayOfRecentExercises = new ArrayList<>();

        Exercise newExercise;
        newExercise = new Exercise("Bicep Curl", 3, 10, 30);
        arrayOfRecentExercises.add(newExercise);
        newExercise = new Exercise("Bench Press", 3, 10, 30);
        arrayOfRecentExercises.add(newExercise);
        newExercise = new Exercise("Bent Over Rows", 3, 10, 30);
        arrayOfRecentExercises.add(newExercise);

        ExerciseListAdapter recentAdapter = new ExerciseListAdapter(this, arrayOfRecentExercises);
        ListView recentListView = (ListView) findViewById(R.id.recent_list);
        recentListView.setAdapter(recentAdapter);
        setListViewHeightBasedOnItems(recentListView);

        ArrayList<Exercise> arrayOfExercises = new ArrayList<>();

        newExercise = new Exercise("Bicep Curl", 3, 10, 30);
        arrayOfExercises.add(newExercise);
        newExercise = new Exercise("Bench Press", 3, 10, 30);
        arrayOfExercises.add(newExercise);
        newExercise = new Exercise("Bent Over Rows", 3, 10, 30);
        arrayOfExercises.add(newExercise);

        ExerciseListAdapter adapter = new ExerciseListAdapter(this, arrayOfExercises);
        ListView exerciseListView = (ListView) findViewById(R.id.exercises_list);
        exerciseListView.setAdapter(adapter);
        setListViewHeightBasedOnItems(exerciseListView);

    }

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

}


