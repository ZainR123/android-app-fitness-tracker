package com.example.cw3.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cw3.R;
import com.example.cw3.databinding.ActivityCourseStatisticsBinding;
import com.example.cw3.viewmodels.CourseStatisticsVM;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

//Shows overall statistics for all tracks and progression breakdown in the form of a graph
public class CourseStatistics extends AppCompatActivity {

    //Declare viewmodel and data binding object
    private CourseStatisticsVM model;
    private ActivityCourseStatisticsBinding activityCourseStatisticsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("g53mdp", "CourseStatistics onCreate");

        //Links the CourseStatistics activity with it's viewmodel and creates binding object
        model = new ViewModelProvider(this).get(CourseStatisticsVM.class);
        activityCourseStatisticsBinding = ActivityCourseStatisticsBinding.inflate(LayoutInflater.from(this));
        setContentView(activityCourseStatisticsBinding.getRoot());
        activityCourseStatisticsBinding.setViewmodel(model);

        //Observe LiveData object to get all course objects
        model.getCoursesByDate().observe(this, courses -> model.setByDate(courses));

        //Create list of strings for graph options
        String[] items = new String[]{"Pace", "Distance", "Time", "Calories", "Weight"};

        //Create and set adapter for graph options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        activityCourseStatisticsBinding.progressList.setAdapter(adapter);
        //When a graph option is selected, set graph with the selected statistic
        activityCourseStatisticsBinding.progressList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setGraph(activityCourseStatisticsBinding.progressList.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //When return button pressed, finish activity
        activityCourseStatisticsBinding.returnButton.setOnClickListener(v -> finish());
    }

    //Set graph with the current statistic choice
    public void setGraph(String choice) {

        //Create datapoint array and x axis label array with the size of courses
        DataPoint[] dataPoints = new DataPoint[model.getTempDate().size()];
        String[] xLabels = new String[model.getTempDate().size()];

        //Based on what choice is selected set the Y axis header and add the corresponding data points
        switch (choice) {
            case "Time":
                activityCourseStatisticsBinding.graphAxisYheader.setText(R.string.timeGraph);
                for (int i = 0; i < model.getTempDate().size(); i++) {
                    xLabels[i] = String.valueOf(model.getTempDate().get(i).getCourseID());
                    dataPoints[i] = new DataPoint(i + 1, model.getTempDate().get(i).getTime());
                }
                break;
            case "Distance":
                activityCourseStatisticsBinding.graphAxisYheader.setText(R.string.distanceGraph);
                for (int i = 0; i < model.getTempDate().size(); i++) {
                    xLabels[i] = String.valueOf(model.getTempDate().get(i).getCourseID());
                    dataPoints[i] = new DataPoint(i + 1, model.getTempDate().get(i).getDistance());
                }
                break;
            case "Pace":
                activityCourseStatisticsBinding.graphAxisYheader.setText(R.string.average_pace_m_s);
                for (int i = 0; i < model.getTempDate().size(); i++) {
                    xLabels[i] = String.valueOf(model.getTempDate().get(i).getCourseID());
                    dataPoints[i] = new DataPoint(i + 1, model.getTempDate().get(i).getPace());
                }
                break;
            case "Calories":
                activityCourseStatisticsBinding.graphAxisYheader.setText(R.string.caloriesGraph);
                for (int i = 0; i < model.getTempDate().size(); i++) {
                    xLabels[i] = String.valueOf(model.getTempDate().get(i).getCourseID());
                    dataPoints[i] = new DataPoint(i + 1, model.getTempDate().get(i).getCalories());
                }
                break;
            case "Weight":
                activityCourseStatisticsBinding.graphAxisYheader.setText(R.string.weight_kg);
                for (int i = 0; i < model.getTempDate().size(); i++) {
                    xLabels[i] = String.valueOf(model.getTempDate().get(i).getCourseID());
                    dataPoints[i] = new DataPoint(i + 1, model.getTempDate().get(i).getWeight());
                }
                break;
        }
        //Create custom x axis labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(activityCourseStatisticsBinding.idGraphView);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        //If there are more than 2 labels set the label to each course
        if (xLabels.length > 1) {
            staticLabelsFormatter.setHorizontalLabels(xLabels);

        }
        else {
            String[] temp = new String[] {"1", " No Courses"};
            staticLabelsFormatter.setHorizontalLabels(temp);
        }
        //Set labels
        activityCourseStatisticsBinding.idGraphView.getViewport().setScrollable(true);
        activityCourseStatisticsBinding.idGraphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        activityCourseStatisticsBinding.idGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        //Show data points on graph and set size of them
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(15);
        //Remove previous graphs and add the new one
        activityCourseStatisticsBinding.idGraphView.removeAllSeries();
        activityCourseStatisticsBinding.idGraphView.addSeries(series);
    }

    //Remove LiveData observers
    @Override
    protected void onDestroy() {
        Log.d("g53mdp", "CourseStatistics onDestroy");
        model.getCoursesByDate().removeObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("g53mdp", "CourseStatistics onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("g53mdp", "CourseStatistics onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("g53mdp", "CourseStatistics onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("g53mdp", "CourseStatistics onStop");
        super.onStop();
    }
}