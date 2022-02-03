package com.example.cw3.entities;

import android.content.ContentValues;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Declare table name
@Entity(tableName = "course_point_table")
public class CoursePoint {

    //Declare table elements
    @ColumnInfo(name = "courseID")
    private int courseID;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pointID")
    private int pointID;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "time")
    private int time;

    //Constructor to set table values
    public CoursePoint(int courseID, double latitude, double longitude, int time) {
        this.courseID = courseID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    //Getters and setters for table elements
    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getPointID() {
        return pointID;
    }

    public void setPointID(int pointID) {
        this.pointID = pointID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    //Updates entity values with what has been passed by the content provider user
    public static CoursePoint fromContentValues(@Nullable ContentValues values) {
        if (values != null && values.containsKey("courseID") && values.containsKey("latitude") && values.containsKey("longitude")  && values.containsKey("time")) {
            CoursePoint coursePoint = new CoursePoint(values.getAsInteger("courseID"), values.getAsDouble("latitude"), values.getAsDouble("longitude"),
                values.getAsInteger("time"));

            if (values.containsKey("pointID")) {
                coursePoint.pointID = values.getAsInteger("pointID");
            }
            return coursePoint;
        }
        else {
            return null;
        }
    }
}
