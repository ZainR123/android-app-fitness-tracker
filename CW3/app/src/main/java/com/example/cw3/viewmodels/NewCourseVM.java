package com.example.cw3.viewmodels;

import android.app.Application;
import android.app.Notification;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.example.cw3.BR;
import com.example.cw3.database.MyRepository;
import com.example.cw3.entities.Course;
import com.example.cw3.entities.CoursePoint;
import com.example.cw3.entities.CourseStats;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewCourseVM extends ObservableVM {

    //Declare Repository object
    private final MyRepository repository;
    //Declare course object
    private final Course course;
    //Declare lists
    private final List<LatLng> locations;
    private final List<Double> distanceStats;
    private final List<Integer> timeStats;

    private String imageURL;

    //Create bindable objects to display data
    @Bindable
    private MutableLiveData<String> weight;
    @Bindable
    private MutableLiveData<List<CoursePoint>> coursePoints;
    @Bindable
    private MutableLiveData<List<CourseStats>> courseStats;
    @Bindable
    private MutableLiveData<String> distance;
    @Bindable
    private MutableLiveData<String> time;
    @Bindable
    private MutableLiveData<String> pace;
    @Bindable
    private MutableLiveData<String> startDateTime;
    @Bindable
    private MutableLiveData<String> endDateTime;
    @Bindable
    private MutableLiveData<String> calories;
    @Bindable
    private MutableLiveData<String> weather;

    //Button stat booleans
    private boolean isPaused = false;
    private boolean isStarted = false;
    private boolean isFinished = false;
    private boolean pausedLocation = false;
    //Format doubles
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor initialises variables and creates repository link
    public NewCourseVM(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
        course = new Course("");
        locations = new ArrayList<>();
        distanceStats = new ArrayList<>();
        timeStats = new ArrayList<>();
    }

    //Setter for image link
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    //Get the user's weight, if null then set to nothing
    public MutableLiveData<String> getWeight() {
        if (weight == null) {
            weight = new MutableLiveData<>();
            weight.setValue("");
        }
        return weight;
    }
    //Set the user's weight, saving it to the course object, update the getter and notify that the value has changed for data binding
    public void setWeight(int weight) {
        course.setWeight(weight);
        getWeight().setValue(String.valueOf(weight));
        notifyPropertyChanged(BR.weight);
    }
    //Setter for course ID, saving it to the course object
    public void setCourseID(int courseID) {
        course.setCourseID(courseID);
    }

    //Getter for course ID
    public int getCourseID() { return course.getCourseID(); }

    //Get the current course points, if null then set to a new array list
    public MutableLiveData<List<CoursePoint>> getCoursePoints() {
        if (coursePoints == null) {
            coursePoints = new MutableLiveData<>();
            coursePoints.setValue(new ArrayList<>());
        }
        return coursePoints;
    }

    //Add to the current course points and update the getter
    public void addCoursePoint(CoursePoint coursePoints) {
        List<CoursePoint> tempList = getCoursePoints().getValue();
        assert tempList != null;
        tempList.add(coursePoints);
        getCoursePoints().postValue(tempList);
    }

    //Get the current course 100m splits, if null then set to a new array list
    public MutableLiveData<List<CourseStats>> getCourseStats() {
        if (courseStats == null) {
            courseStats = new MutableLiveData<>();
            courseStats.setValue(new ArrayList<>());
        }
        return courseStats;
    }

    //Add to the current course 100m splits and update the getter
    public void addCourseStat(CourseStats courseStats) {
        List<CourseStats> tempList = getCourseStats().getValue();
        assert tempList != null;
        tempList.add(courseStats);
        getCourseStats().postValue(tempList);
    }

    //Get the current pace, if null then set to 0
    public MutableLiveData<String> getPace() {
        if (pace == null) {
            pace = new MutableLiveData<>();
            pace.setValue("0.00");
        }
        return pace;
    }

    //Set the current pace, calculate from distance and time, update course object and getter, then notify that the value has changed for data binding
    public void setPace(double distanceBetween) {
        List<CoursePoint> tempList = getCoursePoints().getValue();
        assert tempList != null;
        int previousTime = tempList.get(tempList.size()-2).getTime();
        int currentTime = tempList.get(tempList.size()-1).getTime();
        int difference = currentTime - previousTime;
        if (difference == 0) {
            difference = 1;
        }
        course.setPace(distanceBetween / difference);
        getPace().setValue(df.format(course.getPace()));
        notifyPropertyChanged(BR.pace);
    }

    //Get the current time, if null then set to 0
    public MutableLiveData<String> getTime() {
        if (time == null) {
            time = new MutableLiveData<>();
            time.setValue("00:00:00");
        }
        return time;
    }

    //Getter for location list
    public List<LatLng> getLocations() {
        return locations;
    }

    //Set the current time, format the input, update course object and getter, then notify that the value has changed for data binding
    public void setTime(int seconds) {
        course.setTime(seconds);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        String timeFormatted = String.format(Locale.UK,"%02d:%02d:%02d", hours, minutes, seconds);
        getTime().setValue(timeFormatted);
        notifyPropertyChanged(BR.time);
    }

    //Get the current calories, if null then set to 0
    public MutableLiveData<String> getCalories() {
        if (calories == null) {
            calories = new MutableLiveData<>();
            calories.setValue("0.00");
        }
        return calories;
    }

    //Set the current calories, calculate the calories burnt with formula, update course object and getter, then notify that the value has changed for data binding
    public void addCalories() {

        double addCalories;
        if (course.getPace() != 0d && !Double.isNaN(course.getPace())) {

            List<CoursePoint> tempList = getCoursePoints().getValue();
            assert tempList != null;
            int previousTime = tempList.get(tempList.size()-2).getTime();
            int currentTime = tempList.get(tempList.size()-1).getTime();
            int difference = currentTime - previousTime;
            if (difference == 0) {
                difference = 1;
            }
            double multiplier = 60d / difference;
            //Formula includes user weight and pace to work out how many calories have been burnt
            double temp = course.getWeight();
            double weightDiv = temp/200d;
            double caloriesPerMin = course.getPace() * 3.65 * 3.5 * weightDiv;
            double caloriesPerUpdate = caloriesPerMin / multiplier;
            addCalories = course.getCalories() + caloriesPerUpdate;
        }
        else {
            addCalories = course.getCalories();
        }
        course.setCalories(addCalories);
        getCalories().setValue(df.format(addCalories));
        notifyPropertyChanged(BR.calories);
    }

    //Get the current start datetime, if null then set to nothing
    public MutableLiveData<String> getStartDateTime() {
        if (startDateTime == null) {
            startDateTime = new MutableLiveData<>();
            startDateTime.setValue("");
        }
        return startDateTime;
    }

    //Get the current start datetime, if null then set to nothing
    public MutableLiveData<String> getEndDateTime() {
        if (endDateTime == null) {
            endDateTime = new MutableLiveData<>();
            endDateTime.setValue("");
        }
        return endDateTime;
    }

    //Getter for isFinished boolean
    public boolean getIsFinished() {
        return isFinished;
    }

    //Setter for isFinished boolean
    public void setIsFinished(boolean finished) {
        isFinished = finished;
    }

    //Getter for isPaused boolean
    public boolean getIsPaused() {
        return isPaused;
    }

    //Setter for isPaused boolean
    public void setIsPaused(boolean paused) {
        this.isPaused = paused;
        if (paused) {
            pausedLocation = true;
        }
    }

    //Getter for isStarted boolean
    public boolean getIsStarted() {
        return isStarted;
    }

    //Setter for isStarted boolean
    public void setIsStarted(boolean started) {
        this.isStarted = started;
    }

    //Set the current start datetime, get current datetime, update course object and getter, then notify that the value has changed for data binding
    public void setStartDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
        String current = dateFormat.format(date);
        course.setStartDateTime(current);
        getStartDateTime().setValue(current);
        notifyPropertyChanged(BR.startDateTime);
    }
    //Set the current end datetime, get current datetime, update course object and getter, then notify that the value has changed for data binding
    public void setEndDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
        String current = dateFormat.format(date);
        course.setEndDateTime(current);
        getEndDateTime().setValue(current);
    }

    //Get the current distance, if null then set to 0
    public MutableLiveData<String> getDistance() {
        if (distance == null) {
            distance = new MutableLiveData<>();
            distance.setValue("0.00");
        }
        return distance;
    }

    //Set the current distance, calculate distance between previous location and current, update course object and set pace and calories,
    //then notify that the value has changed for data binding
    public void setDistance() {
        double value;
        if(locations.size() == 1 || pausedLocation) {
            value = course.getDistance();
            pausedLocation = false;
        }
        else {
            double distanceBetween = SphericalUtil.computeDistanceBetween(locations.get(locations.size()-1), locations.get(locations.size()-2));
            value = course.getDistance() + distanceBetween;
            setPace(distanceBetween);
            addCalories();
        }
        course.setDistance(value);
        getDistance().setValue(df.format(course.getDistance()));
        notifyPropertyChanged(BR.distance);
    }

    //Get the current location point, create a new course point object and add it to our course point list
    //Add current location to our location list and set the new distance and add to 100m splits data
    public void savePoints(LatLng latLng) {
        CoursePoint coursePoint = new CoursePoint(course.getCourseID(), latLng.latitude, latLng.longitude, course.getTime());
        addCoursePoint(coursePoint);
        locations.add(latLng);
        setDistance();
        saveStats();
    }

    //Update 100m splits
    public void saveStats() {
        //If courseStats list is not empty, calculate pace and time change for the 100m interval
        if (courseStats != null && !Objects.requireNonNull(courseStats.getValue()).isEmpty()) {
            if (course.getDistance() >= distanceStats.get(distanceStats.size()-1) + 100d){
                double distance = course.getDistance() - distanceStats.get(distanceStats.size()-1);
                int time = course.getTime() - timeStats.get(timeStats.size()-1);
                double pace = distance/ (double) time;
                CourseStats courseStatsTemp = new CourseStats(course.getCourseID(), pace, course.getDistance(), time);
                addCourseStat(courseStatsTemp);
                timeStats.add(course.getTime());
                distanceStats.add(course.getDistance());
            }
        }
        else {
            //If courseStats list is empty, calculate pace and time change for the first 100m interval
            if (course.getDistance() >= 100d) {
                CourseStats courseStatsTemp = new CourseStats(course.getCourseID(), course.getDistance()/(double) course.getTime(), course.getDistance(),
                        course.getTime());
                addCourseStat(courseStatsTemp);
                timeStats.add(course.getTime());
                distanceStats.add(course.getDistance());
            }
        }
    }
    //Set the notification with updated statistics
    public Notification setNotification(NotificationCompat.Builder builder, String courseName) {
        builder.setSubText("Time: " + getTime().getValue());
        if (courseName.isEmpty()) {
            builder.setContentTitle("Course " + course.getCourseID());
        }
        else {
            builder.setContentTitle(courseName);
        }
        builder.setContentText("Distance: " + getDistance().getValue() + " -- " + "Average Pace: " + getPace().getValue()
                + " -- " + "Calories Burnt: " + getCalories().getValue());
        return builder.build();
    }

    //Set the current weather stats, update course object and set weather string,
    //then notify that the value has changed for data binding
    public void setWeather(String weather) {
        if (!weather.isEmpty()) {
            course.setWeather(weather);
            getWeather().setValue(weather);
            notifyPropertyChanged(BR.weather);
        }
    }

    //Get the current weather stat, if null then set to placeholder
    public MutableLiveData<String> getWeather() {
        if (weather == null) {
            weather = new MutableLiveData<>();
            weather.setValue("No Data");
        }
        return weather;
    }

    //Finish the course, saving all user inputted values to the course object
    public void finish(String courseName, int rating) {
        if (courseName.isEmpty()) {
            course.setCourseName("Course " + course.getCourseID());
        }
        else {
            course.setCourseName(courseName);
        }
        course.setRating(rating);
        course.setImage(imageURL);
        double time = course.getTime();
        course.setPace(course.getDistance()/time);
        setEndDateTime();
        //If no distance traversed then don't save course, otherwise insert all data into database
        if (getCoursePoints().getValue() != null && course.getDistance() != 0d) {
            repository.insertCourse(course);
            repository.insertCoursePoints(getCoursePoints().getValue());
            repository.insertCourseStats(getCourseStats().getValue());
            Toast.makeText(getApplication().getApplicationContext(), "Course successfully saved", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplication().getApplicationContext(), "Course not saved, as no distance traversed", Toast.LENGTH_SHORT).show();
        }
    }
}