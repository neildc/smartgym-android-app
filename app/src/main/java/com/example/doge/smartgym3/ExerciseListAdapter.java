package com.example.doge.smartgym3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
        TextView exerciseWeight = (TextView) convertView.findViewById(R.id.exercise_weight);
        TextView exerciseDate = (TextView) convertView.findViewById(R.id.exercise_date);
        // Populate the data into the template view using the data object
        exerciseName.setText(exercise.exerciseName);
        exerciseSet.setText(String.valueOf(exercise.reps) + " x " + String.valueOf(exercise.sets));
        exerciseWeight.setText(String.valueOf(exercise.weight) + " kg");
        exerciseDate.setText(exercise.date);
        // Return the completed view to render on screen
        return convertView;
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
