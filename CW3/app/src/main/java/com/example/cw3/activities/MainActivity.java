package com.example.cw3.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cw3.adapters.CourseAdapter;
import com.example.cw3.databinding.ActivityMainBinding;
import com.example.cw3.viewmodels.MainVM;

import java.util.Objects;

//Activity which shows all the currently created tracks and allows users to inspect a selected track
//Able to create a new track
//Able to sort tracks by different stats
//Able to see overall statistics of all current tracks
public class MainActivity extends AppCompatActivity {
    //Declare viewmodel and data binding object
    private MainVM model;
    private ActivityMainBinding activityMainBinding;
    //Declare adapter for the recycler view to show all courses
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("g53mdp", "MainActivity onCreate");

        //Links the main activity with it's viewmodel and creates binding object
        model = new ViewModelProvider(this).get(MainVM.class);
        activityMainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(activityMainBinding.getRoot());
        activityMainBinding.setViewmodel(model);

        //Declare list of strings for my dropdown list
        String[] items = new String[]{"Date", "Time", "Distance", "Pace", "Calories", "Weight", "Rating"};
        //Declare adapter for dropdown menu
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //Set adapter to dropdown list
        activityMainBinding.sortStats.setAdapter(adapter);
        //When a dropdown item is picked, set the order of courses
        activityMainBinding.sortStats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                model.setAllCourses(activityMainBinding.sortStats.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //Adapter for showing all courses
        courseAdapter = new CourseAdapter(this);

        //Observe for an update on every ordered list
        model.getCoursesByDate().observe(this, courses -> model.setByDate(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByTime().observe(this, courses -> model.setByTime(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByDistance().observe(this, courses -> model.setByDistance(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByPace().observe(this, courses -> model.setByPace(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByCalories().observe(this, courses -> model.setByCalories(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByWeight().observe(this, courses -> model.setByWeight(courses, activityMainBinding.sortStats.getSelectedItem().toString()));
        model.getCoursesByRating().observe(this, courses -> model.setByRating(courses, activityMainBinding.sortStats.getSelectedItem().toString()));

        //Set viewable list to what the user has ordered their list by
        model.getAllCourses().observe(this, courses -> courseAdapter.setData(courses));
        //Set adapter to the recycler view
        activityMainBinding.recyclerView.setAdapter(courseAdapter);
        activityMainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //When a recycler view item is selected ask for storage permissions to view the course
        courseAdapter.setClickListener((view, position) -> {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startViewCourse(position);
            }
            else {
                model.setListPosition(position);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        });

        //When a new course is selected ask for location and storage permissions to start a new course
        activityMainBinding.newTrack.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                startNewCourse();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
            }
        });

        //When the statistics button is pressed go to it's activity
        activityMainBinding.courseStatsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CourseStatistics.class);
            startActivity(intent);
        });
    }

    //Create a new activity for a new course, passing on the user's weight and the ID for the new course
    public void startNewCourse() {
        Intent intent = new Intent(MainActivity.this, NewCourse.class);
        if (activityMainBinding.weightValue.getText().toString().isEmpty() || Integer.parseInt(activityMainBinding.weightValue.getText().toString()) == 0) {
            intent.putExtra("weight", 70);
        }
        else {
            intent.putExtra("weight", Integer.parseInt(activityMainBinding.weightValue.getText().toString()));
        }

        if (Objects.requireNonNull(model.getCoursesByDate().getValue()).size() == 0 || model.getCoursesByDate().getValue() == null) {
            intent.putExtra("CourseID", 1);
        } else {
            int finalCourse = model.getCoursesByDate().getValue().size() - 1;
            int finalCourseID = model.getCoursesByDate().getValue().get(finalCourse).getCourseID();
            intent.putExtra("CourseID", (finalCourseID + 1));
        }
        startActivity(intent);
    }

    //Create a new activity for viewing a course, passing on the ID of the selected course
    public void startViewCourse(int position) {
        Intent intent = new Intent(this, ViewCourse.class);
        intent.putExtra("CourseID", Math.toIntExact(courseAdapter.getItemId(position)));
        startActivity(intent);
    }

    //Check permissions user selected, if not given then prompt them that they can't create/view a course without them
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                startNewCourse();
            }
            else {
                Toast.makeText(this, "Location and storage access must be allowed to create a new course", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startViewCourse(model.getListPosition());
            }
            else {
                Toast.makeText(this, "Storage access must be allowed to view a course", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //When the activity is destroyed, remove all LiveData observers
    @Override
    protected void onDestroy() {
        Log.d("g53mdp", "MainActivity onDestroy");
        model.getAllCourses().removeObservers(this);
        model.getCoursesByWeight().removeObservers(this);
        model.getCoursesByDate().removeObservers(this);
        model.getCoursesByCalories().removeObservers(this);
        model.getCoursesByPace().removeObservers(this);
        model.getCoursesByDistance().removeObservers(this);
        model.getCoursesByTime().removeObservers(this);
        model.getCoursesByRating().removeObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("g53mdp", "MainActivity onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("g53mdp", "MainActivity onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("g53mdp", "MainActivity onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("g53mdp", "MainActivity onStop");
        super.onStop();
    }
}