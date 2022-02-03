package com.example.cw3.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cw3.R;
import com.example.cw3.adapters.CourseStatsAdapter;
import com.example.cw3.databinding.ActivityNewCourseBinding;
import com.example.cw3.service.MyService;
import com.example.cw3.viewmodels.NewCourseVM;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewCourse extends AppCompatActivity implements OnMapReadyCallback {

    //Declare viewmodel and data binding object
    private NewCourseVM model;
    private ActivityNewCourseBinding activityNewCourseBinding;

    //Declare the google map object
    private GoogleMap mMap;
    //Set service binder object to null
    private MyService.MyBinder myService = null;
    //Declare adapter for 100m splits recycler view
    private CourseStatsAdapter courseStatsAdapter;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("g53mdp", "NewCourse onCreate");

        //Links the newCourse activity with it's viewmodel and creates binding object
        model = new ViewModelProvider(this).get(NewCourseVM.class);
        activityNewCourseBinding = ActivityNewCourseBinding.inflate(LayoutInflater.from(this));
        setContentView(activityNewCourseBinding.getRoot());
        activityNewCourseBinding.setViewmodel(model);

        //Set Course ID and weight with the data from main activity
        model.setCourseID(getIntent().getIntExtra("CourseID", 0));
        model.setWeight(getIntent().getIntExtra("weight", 0));

        //Set the course name to a generic placeholder
        activityNewCourseBinding.courseName.setText(getString(R.string.course_name_default) + model.getCourseID());

        //Get google map and wait for it to be ready for use
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //When the start button is pressed
        activityNewCourseBinding.startButton.setOnClickListener(v -> {

            //If the button hasn't been pressed at all then create the foreground service and start datetime
            if (!model.getIsStarted()) {
                this.startForegroundService(new Intent(NewCourse.this, MyService.class));
                model.setStartDateTime();
                model.setIsStarted(true);
                model.setIsFinished(false);
                Toast.makeText(this, "Course Started!", Toast.LENGTH_SHORT).show();
            }
            //If the course has been paused, start the location and timer tracker
            if (model.getIsPaused()) {
                myService.startGPS();
                myService.startTimer();
                model.setIsPaused(false);
                Toast.makeText(this, "Course Resumed!", Toast.LENGTH_SHORT).show();
            }
        });
        //When the pause button is pressed
        activityNewCourseBinding.pauseButton.setOnClickListener(v -> {
            //If the course has been started and is not paused, pause the location and timer tracker
            if (model.getIsStarted() && !model.getIsPaused()) {
                myService.pauseGPS();
                myService.pauseTimer();
                model.setIsPaused(true);
                Toast.makeText(this, "Course Paused!", Toast.LENGTH_SHORT).show();
            }
        });
        //When image button pressed, open user files and let them pick an image
        activityNewCourseBinding.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(intent, 3);
        });
        //When finish pressed end activity and service
        activityNewCourseBinding.finishButton.setOnClickListener(v -> {
            model.setIsFinished(true);
            courseFinished();
        });

        //When remove image pressed, reset image given
        activityNewCourseBinding.clearImageNew.setOnClickListener(v -> {
            model.setImageURL(null);
            activityNewCourseBinding.imageButton.setImageResource(R.drawable.round_image_black_18);
            activityNewCourseBinding.imageSelectedNew.setText(R.string.select_image);
        });

//        //List of strings for weather dropdown menu
//        String[] items = new String[]{"Clear", "Snowy", "Icy", "Windy", "Rainy"};
//
//        //Create and set adapter for weather dropdown
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        activityNewCourseBinding.weatherList.setAdapter(adapter);

        //Create and set adapter for recycler view to view 100m splits
        courseStatsAdapter = new CourseStatsAdapter(this);
        activityNewCourseBinding.recyclerViewStats.setAdapter(courseStatsAdapter);
        activityNewCourseBinding.recyclerViewStats.setLayoutManager(new LinearLayoutManager(this));

        //Observe LiveData list tol get 100m split data
        model.getCourseStats().observe(this, courseStatistics -> courseStatsAdapter.setData(courseStatistics));

        //Set Broadcast Receiver intent filters and register the receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("Location");
        filter.addAction("Time");
        filter.addAction("Start");
        filter.addAction("Pause");
        filter.addAction("Finish");
        registerReceiver(receiver, filter);
        //Bind the service to the activity
        this.bindService(new Intent(NewCourse.this, MyService.class), serviceConnection, 0);
        if (model.getImageURL() != null) {
            activityNewCourseBinding.imageButton.setImageBitmap(BitmapFactory.decodeFile(model.getImageURL()));
            activityNewCourseBinding.imageSelectedNew.setText(R.string.image_selected);
        }
    }

    //Create new service connection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        //Once connected then create a new service binder object in service
        @Override
        public void onServiceConnected(ComponentName trackName, IBinder service) {
            Log.d("g53mdp", "NewCourse onServiceConnected");
            myService = (MyService.MyBinder) service;
        }

        //If service broken unexpectedly close the service and set the object to null
        @Override
        public void onServiceDisconnected(ComponentName trackName) {
            Log.d("g53mdp", "NewCourse onServiceDisconnected");
            myService = null;
        }
    };

    //Create Broadcast Receiver to get location, time and notification button press updates
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!model.getIsFinished()) {
                switch (action) {
                    case "Location":
                        //If intent is location then save the given points to course object
                        double latitude = intent.getDoubleExtra("Latitude", 0.0);
                        double longitude = intent.getDoubleExtra("Longitude", 0.0);
                        model.savePoints(new LatLng(latitude, longitude));
                        if (model.getLocations().size() == 1) {
                            weatherByLocation(String.valueOf(latitude), String.valueOf(longitude));
                        }
                        break;
                    case "Time":
                        //If intent is time then save the time to course object and update the notification
                        int time = intent.getIntExtra("Timer", 0);
                        model.setTime(time);
                        setMap(model.getLocations());
                        myService.getNotifications().notify(1, model.setNotification(myService.getBuildNotification(),
                                activityNewCourseBinding.courseName.getText().toString()));
                        break;

                    case "Start":
                        //If start notification button pressed and paused, then resume course
                        if (model.getIsPaused()) {
                            myService.startGPS();
                            myService.startTimer();
                        }
                        break;

                    case "Pause":
                        //If pause notification button pressed and not already paused, then pause course
                        if (!model.getIsPaused()) {
                            myService.pauseGPS();
                            myService.pauseTimer();
                            model.setIsPaused(true);
                        }
                        break;

                    case "Finish":
                        //If finish notification button pressed, then save and end course
                        courseFinished();
                        break;
                }
            }
        }
    };

    //If user successfully adds image from files then get the image path, compress it and set the image button to it and update the text
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && resultCode == RESULT_OK && data != null){
            Uri imageSelected = data.getData();
            String[] projectionArray = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageSelected, projectionArray, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projectionArray[0]);
            String imageLink = cursor.getString(columnIndex);
            activityNewCourseBinding.imageButton.setImageBitmap(BitmapFactory.decodeFile(imageLink));

            activityNewCourseBinding.imageSelectedNew.setText(R.string.image_selected);
            model.setImageURL(imageLink);
            cursor.close();
        }
    }

    //When google map is ready to use, set the style and add map route if available
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        setMap(model.getLocations());
    }

    //Update map with a drawn line of the current route, shows start point
    private void setMap(List<LatLng> latLngs) {

        if (mMap != null) {

            if (!latLngs.isEmpty()) {
                mMap.clear();
                PolylineOptions options = new PolylineOptions().color(Color.RED).width(10).addAll(latLngs);
                mMap.addPolyline(options);
                mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title("Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size() - 1), 15));
            }
            else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        if (task.getResult() != null) {
                            LatLng latLng = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }
                });
            }
        }
    }

    //Get weather by lat, long values for the user
    private void weatherByLocation(String lat,String lon){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&appid=0670708d80c58c5fdf2ccd2b6e5d4c8a&units=metric";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json=new JSONObject(response);
                        JSONObject objects = json.getJSONArray("list").getJSONObject(0);
                        JSONObject object = objects.getJSONArray("weather").getJSONObject(0);
                        JSONObject main = objects.getJSONObject("main");
                        model.setWeather(Math.round(main.getDouble("temp")) + "°C" + " - " + object.getString("description"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Log.d("g53mdp", "No API Response"));
        queue.add(stringRequest);
    }

    //When course is finished, save the course name, rating and weather details
    public void courseFinished() {
        if (model.getIsStarted()) {
            model.finish(activityNewCourseBinding.courseName.getText().toString(), (int) activityNewCourseBinding.ratingBarValue.getRating());
        }
        else {
            Toast.makeText(this, "Course not started, so not saved", Toast.LENGTH_SHORT).show();
        }
        //Stop the service and finish activity
        stopService(new Intent(NewCourse.this, MyService.class));
        finish();
    }

    //When activity destroyed remove current observers, unregister the broadcast receiver and unbind the service
    @Override
    protected void onDestroy() {
        Log.d("g53mdp", "NewCourse onDestroy");
        model.getCourseStats().removeObservers(this);
        unregisterReceiver(receiver);
        if(serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        super.onDestroy();
    }

    //When the back button is pressed stop the service and finish course
    @Override
    public void onBackPressed() {
        Log.d("g53mdp", "NewCourse onBackPressed");
        stopService(new Intent(NewCourse.this, MyService.class));
        Toast.makeText(this, "Course not saved", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.d("g53mdp", "NewCourse onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("g53mdp", "NewCourse onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("g53mdp", "NewCourse onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("g53mdp", "NewCourse onStop");
        super.onStop();
    }
}