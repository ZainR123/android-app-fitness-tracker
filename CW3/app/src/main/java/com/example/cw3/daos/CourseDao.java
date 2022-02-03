package com.example.cw3.daos;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cw3.entities.Course;

import java.util.List;

//Declare database queries
@Dao
public interface CourseDao {

    //Insert one course
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCourse(Course course);

    //Insert multiple courses
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(Course[] courses);

    //Update one course
    @Update(entity = Course.class)
    int updateCourse(Course course);

    //Delete one course by it's id
    @Query("DELETE FROM course_table WHERE courseID == :courseID")
    int deleteCourse(int courseID);

    //Delete all courses
    @Query("DELETE FROM course_table")
    int deleteAll();

    //Get all courses ordered by date
    @Query("SELECT * FROM course_table")
    LiveData<List<Course>> getCoursesByDate();

    //Get all courses ordered by time
    @Query("SELECT * FROM course_table ORDER BY time DESC")
    LiveData<List<Course>> getCoursesByTime();

    //Get all courses ordered by distance
    @Query("SELECT * FROM course_table ORDER BY distance DESC")
    LiveData<List<Course>> getCoursesByDistance();

    //Get all courses ordered by pace
    @Query("SELECT * FROM course_table ORDER BY pace DESC")
    LiveData<List<Course>> getCoursesByPace();

    //Get all courses ordered by calories
    @Query("SELECT * FROM course_table ORDER BY calories DESC")
    LiveData<List<Course>> getCoursesByCalories();

    //Get all courses ordered by weight
    @Query("SELECT * FROM course_table ORDER BY weight DESC")
    LiveData<List<Course>> getCoursesByWeight();

    //Get all courses ordered by rating
    @Query("SELECT * FROM course_table ORDER BY rating DESC")
    LiveData<List<Course>> getCoursesByRating();

    //Get one course by course ID
    @Query("SELECT * FROM course_table WHERE courseID == :courseID")
    LiveData<Course> getCourse(int courseID);

    //Get cursor for all courses
    @Query("SELECT * FROM course_table")
    Cursor getAllCoursesCursor();

    //Get cursor for one course
    @Query("SELECT * FROM course_table WHERE courseID == :courseID")
    Cursor getCourseCursor(int courseID);
}
