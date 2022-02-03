package com.example.cw3.daos;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cw3.entities.CourseStats;

import java.util.List;

//Declare database queries
@Dao
public interface CourseStatsDao {

    //Insert list of course stats
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCourseStats(List<CourseStats> courseStats);

    //Insert one course stat
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCourseStat(CourseStats courseStat);

    //Bulk insert course stats
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(CourseStats[] courseStats);

    //Delete course stats by id
    @Query("DELETE FROM course_stats_table WHERE courseID == :courseID")
    int deleteCourseStats(int courseID);

    //Delete all course stats
    @Query("DELETE FROM course_stats_table")
    int deleteAll();

    //Get course stats by id
    @Query("SELECT * FROM course_stats_table WHERE courseID == :courseID")
    LiveData<List<CourseStats>> getCourseStats(int courseID);

    //Get cursor of course stats by id
    @Query("SELECT * FROM course_stats_table WHERE courseID == :courseID")
    Cursor getCourseStatsCursor(int courseID);

    //Get cursor of all course stats
    @Query("SELECT * FROM course_stats_table")
    Cursor getAllCourseStatsCursor();
}
