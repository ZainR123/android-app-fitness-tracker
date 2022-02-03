package com.example.cw3.entities;

import android.content.ContentValues;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Declare table name
@Entity(tableName = "course_stats_table")
public class CourseStats {

    //Declare table elements
    @ColumnInfo(name = "courseID")
    private int courseID;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "statsID")
    private int statsID;

    @ColumnInfo(name = "distance")
    private double distance;

    @ColumnInfo(name = "pace")
    private double pace;

    @ColumnInfo(name = "time")
    private int time;

    //Constructor to set table values
    public CourseStats(int courseID, double pace, double distance, int time) {
        this.courseID = courseID;
        this.pace = pace;
        this.distance = distance;
        this.time = time;
    }

    //Getters and setters for table elements
    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getStatsID() {
        return statsID;
    }

    public void setStatsID(int statsID) {
        this.statsID = statsID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    //Updates entity values with what has been passed by the content provider user
    public static CourseStats fromContentValues(@Nullable ContentValues values) {
        if (values != null && values.containsKey("courseID") && values.containsKey("pace") && values.containsKey("distance") && values.containsKey("time")) {
            CourseStats courseStats = new CourseStats(values.getAsInteger("courseID"), values.getAsDouble("pace"), values.getAsDouble("distance")
                    , values.getAsInteger("time"));

            if (values.containsKey("statsID")) {
                courseStats.statsID = values.getAsInteger("statsID");
            }
            return courseStats;
        }
        else {
            return null;
        }
    }
}
