package com.example.doge.smartgym3;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;

import static android.R.attr.action;


public class ExerciseProgressionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private LineChartView mChart;
    private final String[] mLabels = {"", "", "", "", "", "", "", "", ""};

    private final float[][] mValues = {{0f, 2f, 1.4f, 4.f, 3.5f, 4.3f, 2f, 4f, 6.f},
            {1.5f, 2.5f, 1.5f, 5f, 4f, 5f, 4.3f, 2.1f, 1.4f}};

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
        TextView no_data_text = (TextView) getView().findViewById(R.id.no_data_text);

        // TODO: Needs to respond to data from the server
        boolean no_data = true;

        if (no_data) {
            no_data_text.setText("Get Exercising !");
        } else {
            mChart = (LineChartView) getView().findViewById(R.id.chart1);

            LineSet dataset = new LineSet(mLabels, mValues[0]);
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
}
