package com.example.doge.smartgym3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DOGE on 6/11/2016.
 */

public class ExerciseListAdapter extends ArrayAdapter<Exercise>{
    public ExerciseListAdapter(Context context, ArrayList<Exercise> exercises) {
        super(context, 0, exercises);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Exercise exercise = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_exercise, parent, false);
        }
        // Lookup view for data population
        TextView exerciseName = (TextView) convertView.findViewById(R.id.exercise_name);
        TextView exerciseSet = (TextView) convertView.findViewById(R.id.exercise_set);
        // Populate the data into the template view using the data object
        exerciseName.setText(exercise.exerciseName);
        exerciseSet.setText(String.valueOf(exercise.reps) + " x " + String.valueOf(exercise.sets));
        // Return the completed view to render on screen
        return convertView;
    }
}
