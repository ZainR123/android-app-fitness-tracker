package com.example.cw3.daos;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cw3.entities.CoursePoint;

import java.util.List;

//Declare database queries
@Dao
public interface CoursePointDao {

    //Insert a list of course points
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCoursePoints(List<CoursePoint> coursePoints);

    //Insert one course point
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCoursePoint(CoursePoint coursePoint);

    //Insert course points in bulk
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(CoursePoint[] coursePoints);

    //Delete course points by id
    @Query("DELETE FROM course_point_table WHERE courseID == :courseID")
    int deleteCoursePoints(int courseID);

    //Delete all course points
    @Query("DELETE FROM course_point_table")
    int deleteAll();

    //Get course points by id
    @Query("SELECT * FROM course_point_table WHERE courseID == :courseID")
    LiveData<List<CoursePoint>> getCoursePoints(int courseID);

    //Get cursor of course points by id
    @Query("SELECT * FROM course_point_table WHERE courseID == :courseID")
    Cursor getCoursePointsCursor(int courseID);

    //Get cursor of all course points
    @Query("SELECT * FROM course_point_table")
    Cursor getAllCoursePointsCursor();
}
