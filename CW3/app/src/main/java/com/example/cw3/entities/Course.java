package com.example.cw3.entities;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Declare table name
@Entity(tableName = "course_table")
public class Course {

    //Declare table elements
    @PrimaryKey()
    @ColumnInfo(name = "courseID")
    private int courseID;

    @NonNull
    @ColumnInfo(name = "courseName")
    private String courseName;

    @ColumnInfo(name = "pace")
    private double pace;

    @ColumnInfo(name = "distance")
    private double distance;

    @NonNull
    @ColumnInfo(name = "startDateTime")
    private String startDateTime;

    @NonNull
    @ColumnInfo(name = "endDateTime")
    private String endDateTime;

    @ColumnInfo(name = "time")
    private int time;

    @ColumnInfo(name = "calories")
    private double calories;

    @ColumnInfo(name = "weight")
    private int weight;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "weather")
    private String weather;

    //Constructor to set table values
    public Course(String courseName) {
        if (courseName.isEmpty()) {
            this.courseName = "Course " + this.courseID;
        }
        else {
            this.courseName = courseName;
        }
        this.pace = 0;
        this.distance = 0;
        this.startDateTime = "";
        this.time = 0;
        this.calories = 0;
        this.endDateTime = "";
    }

    //Getters and setters for table elements
    @NonNull
    public String getCourseName() {
        return courseName;
    }

    public int getCourseID() {
        return courseID;
    }

    public double getPace() {
        return pace;
    }

    public double getDistance() {
        return distance;
    }

    @NonNull
    public String getStartDateTime() {
        return startDateTime;
    }

    @NonNull
    public String getEndDateTime() { return endDateTime; }

    public int getTime() { return time; }

    public double getCalories() { return calories; }

    public int getWeight() { return weight; }

    public int getRating() {
        return rating;
    }

    public String getImage() {
        return image;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setWeight(int weight) { this.weight = weight; }

    public void setCalories(double calories) { this.calories = calories; }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public void setCourseName(@NonNull String courseName) {
        this.courseName = courseName;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setStartDateTime(@NonNull String startDateTime) { this.startDateTime = startDateTime; }

    public void setEndDateTime(@NonNull String endDateTime) { this.endDateTime = endDateTime; }

    public void setTime(int time) {
        this.time = time;
    }

    //Updates entity values with what has been passed by the content provider user
    public static Course fromContentValues(@Nullable ContentValues values) {
        if (values != null && values.containsKey("courseName")) {
            Course course = new Course(values.getAsString("courseName"));
            if (values.containsKey("courseID")) {
                course.courseID = values.getAsInteger("courseID");
            }
            if (values.containsKey("distance")) {
                course.distance = values.getAsDouble("distance");
            }
            if (values.containsKey("time")) {
                course.time = values.getAsInteger("time");
            }
            if (values.containsKey("pace")) {
                course.pace = values.getAsDouble("pace");
            }
            if (values.containsKey("weather")) {
                course.weather = values.getAsString("weather");
            }
            if (values.containsKey("rating")) {
                course.rating = values.getAsInteger("rating");
            }
            if (values.containsKey("image")) {
                course.image = values.getAsString("image");
            }
            if (values.containsKey("startDateTime")) {
                course.startDateTime = values.getAsString("startDateTime");
            }
            if (values.containsKey("endDateTime")) {
                course.endDateTime = values.getAsString("endDateTime");
            }
            if (values.containsKey("weight")) {
                course.weight = values.getAsInteger("weight");
            }
            if (values.containsKey("calories")) {
                course.calories = values.getAsDouble("calories");
            }
            return course;
        }
        else {
            return null;
        }
    }
}