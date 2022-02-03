package com.example.cw3.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.cw3.daos.CourseDao;
import com.example.cw3.daos.CoursePointDao;
import com.example.cw3.daos.CourseStatsDao;
import com.example.cw3.entities.Course;
import com.example.cw3.entities.CoursePoint;
import com.example.cw3.entities.CourseStats;

import java.util.List;

public class MyRepository {

    //Declare dao objects
    private final CourseDao courseDao;
    private final CoursePointDao coursePointDao;
    private final CourseStatsDao courseStatsDao;

    //Initialise dao objects and link database
    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        courseDao = db.courseDao();
        coursePointDao = db.coursePointDao();
        courseStatsDao = db.courseStatsDao();
    }

    //Create functions which link dao queries to the viewmodels
    public LiveData<List<Course>> getCoursesByDate() { return courseDao.getCoursesByDate(); }

    public LiveData<List<Course>> getCoursesByTime() { return courseDao.getCoursesByTime(); }

    public LiveData<List<Course>> getCoursesByDistance() { return courseDao.getCoursesByDistance(); }

    public LiveData<List<Course>> getCoursesByPace() { return courseDao.getCoursesByPace(); }

    public LiveData<List<Course>> getCoursesByCalories() { return courseDao.getCoursesByCalories(); }

    public LiveData<List<Course>> getCoursesByWeight() { return courseDao.getCoursesByWeight(); }

    public LiveData<List<Course>> getCoursesByRating() { return courseDao.getCoursesByRating(); }

    public LiveData<Course> getCourse(int courseID) { return courseDao.getCourse(courseID); }

    public LiveData<List<CourseStats>> getCourseStats(int courseID) { return courseStatsDao.getCourseStats(courseID); }

    public LiveData<List<CoursePoint>> getCoursePoints(int courseID) { return coursePointDao.getCoursePoints(courseID); }

    public void insertCourse(Course course) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> courseDao.insertCourse(course));
    }

    public void insertCoursePoints(List<CoursePoint> coursePoint) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> coursePointDao.insertCoursePoints(coursePoint));
    }

    public void insertCourseStats(List<CourseStats> courseStats) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> courseStatsDao.insertCourseStats(courseStats));
    }

    public void update(Course course) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> courseDao.updateCourse(course));
    }

    public void deleteCourse(Course course) {
        MyRoomDatabase.databaseWriteExecutor.execute(() -> courseDao.deleteCourse(course.getCourseID()));
        MyRoomDatabase.databaseWriteExecutor.execute(() -> coursePointDao.deleteCoursePoints(course.getCourseID()));
        MyRoomDatabase.databaseWriteExecutor.execute(() -> courseStatsDao.deleteCourseStats(course.getCourseID()));
    }
}