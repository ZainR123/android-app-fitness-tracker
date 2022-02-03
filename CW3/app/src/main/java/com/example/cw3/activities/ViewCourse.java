package com.example.cw3.activities;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cw3.R;
import com.example.cw3.adapters.CourseStatsAdapter;
import com.example.cw3.databinding.ActivityViewCourseBinding;
import com.example.cw3.viewmodels.ViewCourseVM;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

//Activity which shows all the statistics for the currently selected course
//Able to update the course name and rating
//Able to delete the course
public class ViewCourse extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //Declare viewmodel and data binding object
    private ViewCourseVM model;
    private ActivityViewCourseBinding activityViewCourseBinding;
    //Declare adapter for the recycler view to show course statistics
    private CourseStatsAdapter courseStatsAdapter;
    //Format doubles
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("g53mdp", "ViewCourse onCreate");
        //Links the ViewCourse activity with it's viewmodel and creates binding object
        model = new ViewModelProvider(this).get(ViewCourseVM.class);
        activityViewCourseBinding = ActivityViewCourseBinding.inflate(LayoutInflater.from(this));
        setContentView(activityViewCourseBinding.getRoot());
        activityViewCourseBinding.setViewmodel(model);
        //Set the current course ID to the one selected from the main activity
        model.setCourseID(getIntent().getExtras().getInt("CourseID"));

        //Create new adapter for the recycler view to show course statistics
        courseStatsAdapter = new CourseStatsAdapter(this);

        //Get course information and set the view with the data
        model.getCourse().observe(this, course -> {
            if (course != null) {

                activityViewCourseBinding.distanceViewValue.setText(df.format(course.getDistance()));
                activityViewCourseBinding.averagePaceViewValue.setText(df.format(course.getPace()));
                int hours = course.getTime() / 3600;
                int minutes = (course.getTime() % 3600) / 60;
                int seconds = course.getTime() % 60;
                activityViewCourseBinding.timeViewValue.setText(String.format(Locale.UK, "%02d:%02d:%02d", hours, minutes, seconds));
                activityViewCourseBinding.startDateViewValue.setText(course.getStartDateTime());
                activityViewCourseBinding.endDateViewValue.setText(course.getEndDateTime());
                activityViewCourseBinding.weightValueView.setText(String.valueOf(course.getWeight()));
                activityViewCourseBinding.weatherViewValue.setText(course.getWeather());
                activityViewCourseBinding.caloriesViewValue.setText(df.format(course.getCalories()));

                if (course.getImage() != null) {
                    activityViewCourseBinding.imageView.setImageBitmap(BitmapFactory.decodeFile(course.getImage()));
                }
                else {
                    activityViewCourseBinding.imageView.setImageResource(android.R.color.transparent);
                    activityViewCourseBinding.imageViewHeader.setText(R.string.no_image_given);
                }
                if (model.isRotated()) {
                    activityViewCourseBinding.courseNameView.setText(model.getName());
                    activityViewCourseBinding.ratingBar.setRating(model.getRating());
                }
                else {
                    activityViewCourseBinding.ratingBar.setRating(course.getRating());
                    activityViewCourseBinding.courseNameView.setText(course.getCourseName());
                }
            }
        });

        //Get course points to set the map route
        model.getCoursePoints().observe(this, coursePoints -> {
            model.setLocations(coursePoints);
            setMap(model.getLocations());
        });

        //Get course statistics to set the 100m split showcase
        model.getCourseStats().observe(this, courseStats -> courseStatsAdapter.setData(courseStats));

        //Set adapter to recycler viw
        activityViewCourseBinding.recyclerViewPoints.setAdapter(courseStatsAdapter);
        activityViewCourseBinding.recyclerViewPoints.setLayoutManager(new LinearLayoutManager(this));
        //Link google map and wait for it to be ready to use
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //If delete course button is pressed then delete the course and finish activity
        activityViewCourseBinding.deleteCourse.setOnClickListener(v -> {
            model.deleteCourse();
            Toast.makeText(this, "Course successfully deleted", Toast.LENGTH_SHORT).show();
            finish();
        });

        //If finish button is pressed then update the course with any changes and finish activity
        activityViewCourseBinding.finishView.setOnClickListener(v -> {
            model.finish(activityViewCourseBinding.courseNameView.getText().toString(), (int) activityViewCourseBinding.ratingBar.getRating());
            finish();
        });
    }

    //When google map is ready to use, set the style
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    //Set map with the user's full course with a drawn line, shows start point and end point
    private void setMap(List<LatLng> latLngs) {

        if (mMap != null) {
            mMap.clear();

            if (!latLngs.isEmpty()) {
                PolylineOptions options = new PolylineOptions().color(Color.RED).width(10).addAll(latLngs);
                mMap.addPolyline(options);
                mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size()-1)).title("Finish")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get((latLngs.size() - 1)/2), 14));
            }
        }
    }

    //Remove LiveData observers
    @Override
    protected void onDestroy() {
        Log.d("g53mdp", "ViewCourse onDestroy");
        model.getCourseStats().removeObservers(this);
        model.getCourse().removeObservers(this);
        model.getCoursePoints().removeObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("g53mdp", "ViewCourse onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("g53mdp", "ViewCourse onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("g53mdp", "ViewCourse onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("g53mdp", "ViewCourse onStop");
        super.onStop();
    }

    //Save the instance state in case an instance is destroyed when rotating the app etc
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Store set values to model
        Log.d("g53mdp", "ViewCourse onSaveInstanceState");
        model.storeValues((int) activityViewCourseBinding.ratingBar.getRating(),activityViewCourseBinding.courseNameView.getText().toString());
    }
}