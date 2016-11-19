package com.example.doge.smartgym3.graph;

import android.app.Activity;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import com.example.doge.smartgym3.Exercise;
import com.example.doge.smartgym3.R;
import com.example.doge.smartgym3.server.GraphService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseGraphActivity extends AppCompatActivity {

    public static String[] exerciseNames;

    public static int mode = R.id.graph_mode_day;

    public static GraphView graph;
    public static String currExercise;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_graph);


        exerciseNames =  getResources().getStringArray(R.array.exercises);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                exerciseNames
        ));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise_graph, menu);
//            http://stackoverflow.com/questions/7042958/android-adding-a-submenu-to-a-menuitem-where-is-addsubmenu
        MenuItem myMenuItem = menu.findItem(R.id.action_mode);
        getMenuInflater().inflate(R.menu.sub_menu_exercise_graph, myMenuItem.getSubMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.action_mode):
                return true;

            case (R.id.graph_mode_day):
                mode = R.id.graph_mode_day;
                item.setChecked(true);
                resetFragment();
                return true;

            case (R.id.graph_mode_hour):
                mode = R.id.graph_mode_hour;
                item.setChecked(true);
                resetFragment();
                return true;

            case (R.id.graph_mode_minute):
                mode = R.id.graph_mode_minute;
                item.setChecked(true);
                resetFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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


    void changeGraphMode() {

        switch (ExerciseGraphActivity.mode) {
            case (R.id.graph_mode_day):
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(31.0);

                break;

            case (R.id.graph_mode_hour):
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(24.0);
                break;

            case (R.id.graph_mode_minute):
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(60.0);
                break;

        }
    }

    public void getGraphData(String exercise, final GraphView graph) {
        GraphService graphService = ServiceGenerator.createService(
                GraphService.class,
                TokenUtil.getAccessTokenFromSharedPreferences(ExerciseGraphActivity.this)
        );

        // Default
        Call<JsonArray> c = graphService.getGraphFriendsVolumeForADay(exercise);

        switch (mode) {
            case (R.id.graph_mode_day):
                c = graphService.getGraphFriendsVolumeForADay(exercise);
                break;

            case (R.id.graph_mode_hour):
                c = graphService.getGraphFriendsVolumeForAHour(exercise);
                break;

            case (R.id.graph_mode_minute):
                c = graphService.getGraphFriendsVolumeForAMinute(exercise);
                break;

        }


        c.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    switch (response.code()) {
                        case (200):
                            parseResponse(response, graph);
                            break;

                        case (204):
                            Toast.makeText(ExerciseGraphActivity.this, "No data to collect", Toast.LENGTH_LONG);
                            break;
                    }
                }


            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }


    public void parseResponse(Response<JsonArray> response, GraphView graph) {
        JsonArray respBody = response.body().getAsJsonArray();
        for (int i =0; i < respBody.size(); i++) {
            JsonObject o = respBody.get(i).getAsJsonObject();

            ArrayList<DataPoint> dataPointsArrayList = new ArrayList<>();

            JsonObject data = o.get("data").getAsJsonObject();
            for (Map.Entry<String,JsonElement> entry : data.entrySet()) {
                int slice = Integer.parseInt(entry.getKey());
                int weight = entry.getValue().getAsInt();

                Log.d("AAAA", slice + " : " + weight);

                dataPointsArrayList.add(new DataPoint(slice, weight));
            }

            Collections.reverse(dataPointsArrayList);

            LineGraphSeries<DataPoint> series =
                    new LineGraphSeries<DataPoint>(
                            dataPointsArrayList.toArray(
                                    new DataPoint[dataPointsArrayList.size()]
                            )
                    );
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setColor(0xFF000000|(int)(Math.random()*0x1000000));
            Log.v("AAAA", "Color set:"+ Integer.toHexString(series.getColor()));
            graph.addSeries(series);
        }
    }



    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exercise_graph, container, false);

            currExercise = exerciseNames[getArguments().getInt(ARG_SECTION_NUMBER)-1];

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Exercise:" + currExercise);
            ExerciseGraphActivity.graph = (GraphView) rootView.findViewById(R.id.graph);

            graph.getViewport().setXAxisBoundsManual(true);

            ExerciseGraphActivity a = (ExerciseGraphActivity)getActivity();
            a.changeGraphMode();
            a.getGraphData(currExercise, graph);


            return rootView;
        }

    }


}
