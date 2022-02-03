package com.example.cw3.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cw3.database.MyRepository;
import com.example.cw3.entities.Course;

import java.util.ArrayList;
import java.util.List;

public class MainVM extends ObservableVM {

    //Declare LiveData lists
    private final MutableLiveData<List<Course>> allCourses;
    private final LiveData<List<Course>> byDate;
    private final LiveData<List<Course>> byTime;
    private final LiveData<List<Course>> byDistance;
    private final LiveData<List<Course>> byPace;
    private final LiveData<List<Course>> byCalories;
    private final LiveData<List<Course>> byWeight;
    private final LiveData<List<Course>> byRating;

    //Declare list of course objects
    private List<Course> tempDate;
    private List<Course> tempTime;
    private List<Course> tempDistance;
    private List<Course> tempPace;
    private List<Course> tempCalories;
    private List<Course> tempWeight;
    private List<Course> tempRating;

    private int listPosition;

    //Constructor links repository and initialises lists, filling them with their corresponding database queries
    public MainVM(Application application) {
        super(application);
        MyRepository repository = new MyRepository(application);

        tempDate = new ArrayList<>();
        tempTime = new ArrayList<>();
        tempDistance = new ArrayList<>();
        tempPace = new ArrayList<>();
        tempCalories = new ArrayList<>();
        tempWeight = new ArrayList<>();
        tempRating = new ArrayList<>();

        allCourses = new MutableLiveData<>();
        allCourses.setValue(new ArrayList<>());

        byDate = repository.getCoursesByDate();
        byTime = repository.getCoursesByTime();
        byDistance = repository.getCoursesByDistance();
        byPace = repository.getCoursesByPace();
        byCalories = repository.getCoursesByCalories();
        byWeight = repository.getCoursesByWeight();
        byRating = repository.getCoursesByRating();
    }

    //Set the temp lists to the observed LiveData course objects
    //If the dropdown item selected equals the list being updated, then set the recycler view with the updated values
    public void setByDate(List<Course> course, String choice) {
        tempDate = course;
        if (choice.equals("Date")) {
            setAllCourses(choice);
        }
    }
    public void setByTime(List<Course> course, String choice) {
        tempTime = course;
        if (choice.equals("Time")) {
            setAllCourses(choice);
        }
    }
    public void setByDistance(List<Course> course, String choice) {
        tempDistance = course;
        if (choice.equals("Distance")) {
            setAllCourses(choice);
        }
    }
    public void setByPace(List<Course> course, String choice) {
        tempPace = course;
        if (choice.equals("Pace")) {
            setAllCourses(choice);
        }
    }
    public void setByCalories(List<Course> course, String choice) {
        tempCalories = course;
        if (choice.equals("Calories")) {
            setAllCourses(choice);
        }
    }
    public void setByWeight(List<Course> course, String choice) {
        tempWeight = course;
        if (choice.equals("Weight")) {
            setAllCourses(choice);
        }
    }
    public void setByRating(List<Course> course, String choice) {
        tempRating = course;
        if (choice.equals("Rating")) {
            setAllCourses(choice);
        }
    }

    //Getter for the LiveData ordered lists
    public LiveData<List<Course>> getCoursesByDate() { return byDate; }

    public LiveData<List<Course>> getCoursesByTime() { return byTime; }

    public LiveData<List<Course>> getCoursesByDistance() { return byDistance; }

    public LiveData<List<Course>> getCoursesByPace() { return byPace; }

    public LiveData<List<Course>> getCoursesByCalories() { return byCalories; }

    public LiveData<List<Course>> getCoursesByWeight() { return byWeight; }

    public LiveData<List<Course>> getCoursesByRating() { return byRating; }

    public MutableLiveData<List<Course>> getAllCourses() { return allCourses; }

    //Set the MutableLiveData variable used to set the recycler view with the user selected ordered list
    public void setAllCourses(String choice) {

        switch(choice) {
            case "Date":
                getAllCourses().setValue(tempDate);
                break;
            case "Time":
                getAllCourses().setValue(tempTime);
                break;
            case "Distance":
                getAllCourses().setValue(tempDistance);
                break;
            case "Pace":
                getAllCourses().setValue(tempPace);
                break;
            case "Calories":
                getAllCourses().setValue(tempCalories);
                break;
            case "Weight":
                getAllCourses().setValue(tempWeight);
                break;
            case "Rating":
                getAllCourses().setValue(tempRating);
                break;
        }
    }

    //Get position in list where user selected
    public int getListPosition() {
        return listPosition;
    }
    //Set position in list where user selected
    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }
}
