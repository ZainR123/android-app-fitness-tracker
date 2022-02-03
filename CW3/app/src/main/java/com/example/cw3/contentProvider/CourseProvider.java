package com.example.cw3.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.example.cw3.daos.CourseDao;
import com.example.cw3.daos.CoursePointDao;
import com.example.cw3.daos.CourseStatsDao;
import com.example.cw3.database.MyRoomDatabase;
import com.example.cw3.entities.Course;
import com.example.cw3.entities.CoursePoint;
import com.example.cw3.entities.CourseStats;

public class CourseProvider extends ContentProvider {

    //Declare objects
    private CourseDao courseDao;
    private CoursePointDao coursePointDao;
    private CourseStatsDao courseStatsDao;

    private static final UriMatcher uriMatcher;

    //add possible data options
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_table", 1);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_table/#", 2);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_point_table", 3);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_point_table/#", 4);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_stats_table", 5);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "course_stats_table/#", 6);
        uriMatcher.addURI(CourseProviderContract.AUTHORITY, "*", 7);
    }

    //Initialise dao objects and link database
    @Override
    public boolean onCreate() {
        MyRoomDatabase database = Room.databaseBuilder(this.getContext(), MyRoomDatabase.class, "runningDatabase").build();

        courseDao = database.courseDao();
        coursePointDao = database.coursePointDao();
        courseStatsDao = database.courseStatsDao();
        return true;
    }

    //Get queries based on what URI selected
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch(uriMatcher.match(uri)) {
            case 1:
                return courseDao.getCourseCursor(Integer.parseInt(uri.getLastPathSegment()));
            case 2:
                return courseDao.getAllCoursesCursor();
            case 3:
                return coursePointDao.getCoursePointsCursor(Integer.parseInt(uri.getLastPathSegment()));
            case 4:
                return coursePointDao.getAllCoursePointsCursor();
            case 5:
                return courseStatsDao.getCourseStatsCursor(Integer.parseInt(uri.getLastPathSegment()));
            case 6:
                return courseStatsDao.getAllCourseStatsCursor();
            case 7:
                Cursor[] cursors = new Cursor[3];
                cursors[0] = courseDao.getAllCoursesCursor();
                cursors[1] = coursePointDao.getAllCoursePointsCursor();
                cursors[2] = courseStatsDao.getAllCourseStatsCursor();
                return new MergeCursor(cursors);
            default:
                return null;
        }
    }

    //See if want to get multiple items or just one
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String contentType;

        if (uri.getLastPathSegment()==null) {
            contentType = CourseProviderContract.CONTENT_TYPE_MULTIPLE;
        } else {
            contentType = CourseProviderContract.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }

    //Insert values in database
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;
        Uri nu;
        switch (uriMatcher.match(uri)) {
            case 1:
                id = courseDao.insertCourse(Course.fromContentValues(values));
                nu = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(nu, null);
                return nu;
            case 3:
                id = coursePointDao.insertCoursePoint(CoursePoint.fromContentValues(values));
                nu = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(nu, null);
                return nu;
            case 5:
                id = courseStatsDao.insertCourseStat(CourseStats.fromContentValues(values));
                nu = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(nu, null);
                return nu;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    //Delete values from database
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        switch(uriMatcher.match(uri)) {
            case 1:
                count = courseDao.deleteCourse(Integer.parseInt(uri.getLastPathSegment()));
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 2:
                count = courseDao.deleteAll();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 3:
                count = coursePointDao.deleteCoursePoints(Integer.parseInt(uri.getLastPathSegment()));
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 4:
                count = coursePointDao.deleteAll();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 5:
                count = courseStatsDao.deleteCourseStats(Integer.parseInt(uri.getLastPathSegment()));
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 6:
                count = courseStatsDao.deleteAll();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case 7:
                count = courseStatsDao.deleteAll() + coursePointDao.deleteAll() + courseDao.deleteAll();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Invalid URI:" + uri);
        }
    }

    //Update values in database
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == 1 || uriMatcher.match(uri) == 2) {
            int count = courseDao.updateCourse(Course.fromContentValues(values));
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        else {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }

    //Bulk insert values into database
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (uriMatcher.match(uri)) {
            case 2:
                Course[] courses = new Course[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    courses[i] = Course.fromContentValues(valuesArray[i]);
                }
                return courseDao.insertAll(courses).length;
            case 4:
                CoursePoint[] coursePoints = new CoursePoint[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    coursePoints[i] = CoursePoint.fromContentValues(valuesArray[i]);
                }
                return coursePointDao.insertAll(coursePoints).length;
            case 6:
                CourseStats[] courseStats = new CourseStats[valuesArray.length];
                for (int i = 0; i < valuesArray.length; i++) {
                    courseStats[i] = CourseStats.fromContentValues(valuesArray[i]);
                }
                return courseStatsDao.insertAll(courseStats).length;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }
}