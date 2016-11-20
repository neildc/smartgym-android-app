package com.example.doge.smartgym3;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.example.doge.smartgym3.server.GraphService;
import com.example.doge.smartgym3.util.ServiceGenerator;
import com.example.doge.smartgym3.util.TokenUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExerciseProgressionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private LineChartView mChart;
//    private final String[] mLabels = {"", "", "", "", "", "", "", "", ""};

//    private final float[][] mValues = {{0f, 2f, 1.4f, 4.f, 3.5f, 4.3f, 2f, 4f, 6.f},
//            {1.5f, 2.5f, 1.5f, 5f, 4f, 5f, 4.3f, 2.1f, 1.4f}};

//    private OnFragmentInteractionListener mListener;

    public ExerciseProgressionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ExerciseProgressionFragment newInstance(int param1, String param2) {
        ExerciseProgressionFragment fragment = new ExerciseProgressionFragment();
        Bundle args = new Bundle();
        args.putInt("page", param1);
        args.putString("title", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt("page");
            mParam2 = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_progression, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstancesState) {
        super.onActivityCreated(savedInstancesState);

        Spinner s = (Spinner)getActivity().findViewById(R.id.spinner_exercise_type);
        getGraphData(s.getSelectedItem().toString());

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
    public void getGraphData(String exercise) {
        GraphService graphService = ServiceGenerator.createService(
                GraphService.class,
                TokenUtil.getAccessTokenFromSharedPreferences(getContext())
        );

        // Default
        Call<JsonArray> cSelf = graphService.getGraphSelfVolumeForAHour(exercise);

        cSelf.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    switch (response.code()) {
                        case (200):
                            fillInChartWithData(response);
                            break;

                        case (204):
                            Toast.makeText(getContext(), "No data to collect", Toast.LENGTH_LONG);
                            TextView no_data_text = (TextView) getView().findViewById(R.id.no_data_text);
                            no_data_text.setText("Get Exercising !");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

        public void fillInChartWithData(Response<JsonArray> response) {
            JsonArray respBody = response.body().getAsJsonArray();
            for (int i =0; i < respBody.size(); i++) {

                mChart = (LineChartView) getView().findViewById(R.id.chart1);

                ArrayList<Integer> weights = getDataPointLineGraphSeries(respBody, i);
                float [] values = new float[weights.size()];
                String [] labels = new String[weights.size()];

                for (int w = 0; w < weights.size(); w++) {
                    values[w] = (float)weights.get(w);
                    labels[w] = "";
                }

                LineSet dataset = new LineSet(labels, values);

                        dataset.setColor(Color.parseColor("#28FF74"))
                                .setGradientFill(new int[] {Color.parseColor("#1db954"), Color.parseColor("#1db954")}, null);
                mChart.addData(dataset);

                mChart.setBorderSpacing(1)
                        .setXLabels(AxisRenderer.LabelPosition.NONE)
                        .setYLabels(AxisRenderer.LabelPosition.INSIDE)
                        .setAxisColor(Color.WHITE)
                        .setLabelsColor(Color.WHITE)
                        .setXAxis(false)
                        .setYAxis(true)
                        .setBorderSpacing(Tools.fromDpToPx(5));

                mChart.show();

            }
        }


    private ArrayList<Integer> getDataPointLineGraphSeries(JsonArray respBody, int i) {
        JsonObject o = respBody.get(i).getAsJsonObject();

//        ArrayList<DataPoint> dataPointsArrayList = new ArrayList<>();
        ArrayList<Integer> al = new ArrayList<>();

        JsonObject data = o.get("data").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            int slice = Integer.parseInt(entry.getKey());
            int weight = entry.getValue().getAsInt();

            Log.d("AAAA", slice + " : " + weight);
            al.add(weight);

//            dataPointsArrayList.add(new DataPoint(slice, weight));
//            dataset.addPoint("", weight);
        }

        return (al);

    }




//        Collections.sort(dataPointsArrayList, new Comparator<DataPoint>() {
//            @Override
//            public int compare(DataPoint o1, DataPoint o2) {
//                return ((int) o1.getX() - (int) o2.getX());
//            }
//        });
}
