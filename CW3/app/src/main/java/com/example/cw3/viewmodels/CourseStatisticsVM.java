package com.example.cw3.viewmodels;

import android.app.Application;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;

import com.example.cw3.BR;
import com.example.cw3.database.MyRepository;
import com.example.cw3.entities.Course;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseStatisticsVM extends ObservableVM {

    //Declare lists
    private final LiveData<List<Course>> byDate;
    private List<Course> tempDate;
    //Declare bindable strings
    @Bindable
    private String totalTimeString;
    @Bindable
    private String totalDistanceString;
    @Bindable
    private String averagePaceString;
    @Bindable
    private String totalCaloriesString;
    //Format for doubles
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //Constructor to initialise repository and set lists
    public CourseStatisticsVM(Application application) {
        super(application);
        MyRepository repository = new MyRepository(application);

        byDate = repository.getCoursesByDate();
        tempDate = new ArrayList<>();
    }

    //Getter for all the courses
    public LiveData<List<Course>> getCoursesByDate() { return byDate; }

    //Getters and setters for the overall statistics
    public String getTotalTimeString() {
        return totalTimeString;
    }

    public void setTotalTimeString(String totalTimeString) {
        this.totalTimeString = totalTimeString;
    }

    public String getTotalDistanceString() {
        return totalDistanceString;
    }

    public void setTotalDistanceString(String totalDistanceString) {
        this.totalDistanceString = totalDistanceString;
    }

    public String getAveragePaceString() {
        return averagePaceString;
    }

    public void setAveragePaceString(String averagePaceString) {
        this.averagePaceString = averagePaceString;
    }

    public String getTotalCaloriesString() {
        return totalCaloriesString;
    }

    public void setTotalCaloriesString(String totalCaloriesString) {
        this.totalCaloriesString = totalCaloriesString;
    }

    //Set the course list with the observer value and calculate averages for all courses
    public void setByDate(List<Course> course) {
        tempDate = course;
        calculateAverages();
    }

    //Getter for course list
    public List<Course> getTempDate() {
        return tempDate;
    }

    //Calculate averages over all courses
    public void calculateAverages() {
        if (tempDate.size() > 0) {
            int totalTime = 0;
            double totalDistance = 0;
            double totalCalories = 0;
            double averagePace = 0;
            //Add distances, times and calories and pace of all courses
            for (int i = 0; i < tempDate.size(); i++) {
                totalDistance += tempDate.get(i).getDistance();
                totalTime += tempDate.get(i).getTime();
                averagePace += tempDate.get(i).getPace();
                totalCalories += tempDate.get(i).getCalories();
            }
            //For pace get average
            averagePace = averagePace / tempDate.size();
            //Set bindable strings to the calculated values
            totalDistanceString = df.format(totalDistance);
            averagePaceString = df.format(averagePace);
            int hours = totalTime / 3600;
            int minutes = (totalTime % 3600) / 60;
            int seconds = totalTime % 60;
            totalTimeString = String.format(Locale.UK,"%02d:%02d:%02d", hours, minutes, seconds);
            totalCaloriesString = df.format(totalCalories);
        }
        else {
            //Set bindable strings to zero
            totalDistanceString = "0.00";
            totalTimeString = "00:00";
            averagePaceString = "0.00";
            totalCaloriesString = "0.00";
        }
        //Notify binding of update in value
        notifyPropertyChanged(BR.totalDistanceString);
        notifyPropertyChanged(BR.totalTimeString);
        notifyPropertyChanged(BR.totalCaloriesString);
        notifyPropertyChanged(BR.averagePaceString);
    }
}
