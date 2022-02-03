package com.example.cw3.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.cw3.database.MyRepository;
import com.example.cw3.entities.Course;
import com.example.cw3.entities.CoursePoint;
import com.example.cw3.entities.CourseStats;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewCourseVM extends ObservableVM {

    //Declare repository object and lists
    private final MyRepository repository;
    private LiveData<Course> course;
    private List<LatLng> locations;
    private int courseID;
    public boolean isRotated = false;
    private int rating;
    private String name;

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    //Constructor initialises repository
    public ViewCourseVM(@NonNull Application application) {
        super(application);
        repository = new MyRepository(application);
    }

    //Getter for boolean
    public boolean isRotated() {
        return isRotated;
    }

    //Store rating and course name values when phone is rotated
    public void storeValues(int rating, String name) {
        this.rating = rating;
        this.name = name;
        isRotated = true;
    }

    //Setter for course ID
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    //Get course points for specific course
    public LiveData<List<CoursePoint>> getCoursePoints() {
        return repository.getCoursePoints(courseID);
    }

    //Get course stats for specific course
    public LiveData<List<CourseStats>> getCourseStats() {
        return repository.getCourseStats(courseID);
    }

    //Get course details for specific course
    public LiveData<Course> getCourse() {
        course = repository.getCourse(courseID);
        return course;
    }

    //Getter for list of locations
    public List<LatLng> getLocations() {
        return locations;
    }

    //Set locations list for use in map route
    public void setLocations(List<CoursePoint> coursePoints) {
        locations = new ArrayList<>();
        for (int i = 0; i < coursePoints.size(); i++) {
            LatLng latLng = new LatLng(coursePoints.get(i).getLatitude(), coursePoints.get(i).getLongitude());
            locations.add(latLng);
        }
    }

    //When user finished save name/ rating change and update repository
    public void finish(String courseName, int rating) {
        if (courseName.isEmpty()) {
            Objects.requireNonNull(course.getValue()).setCourseName("Course " + courseID);
        }
        else {
            Objects.requireNonNull(course.getValue()).setCourseName(courseName);
        }
        course.getValue().setRating(rating);
        repository.update(course.getValue());
    }

    //Delete the course from repository from all tables
    public void deleteCourse() {
        repository.deleteCourse(course.getValue());
    }
}