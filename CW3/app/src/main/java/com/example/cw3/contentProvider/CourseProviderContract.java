package com.example.cw3.contentProvider;

import android.net.Uri;

public class CourseProviderContract {
    //Declare constant variables that describe the nature of the Uris it accepts and declare table column names
    public static final String AUTHORITY = "com.example.cw3.contentProvider.CourseProvider";

    public static final Uri COURSE_URI = Uri.parse("content://"+AUTHORITY+"/course_table");
    public static final Uri COURSE_POINT_URI = Uri.parse("content://"+AUTHORITY+"/course_point_table");
    public static final Uri COURSE_STATS_URI = Uri.parse("content://"+AUTHORITY+"/course_stats_table");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String COURSE_ID = "courseID";
    public static final String COURSE_NAME = "courseName";
    public static final String PACE = "pace";
    public static final String DISTANCE = "distance";
    public static final String START_DATETIME = "startDateTime";
    public static final String END_DATETIME = "endDateTime";
    public static final String TIME = "time";
    public static final String CALORIES = "calories";
    public static final String WEIGHT = "weight";
    public static final String RATING = "rating";
    public static final String IMAGE = "image";
    public static final String WEATHER = "weather";

    public static final String COURSEPOINT_ID = "courseID";
    public static final String POINT_ID = "pointID";
    public static final String POINT_LATITUDE = "latitude";
    public static final String POINT_LONGITUDE = "longitude";
    public static final String POINT_TIME = "time";

    public static final String COURSESTAT_ID = "courseID";
    public static final String STATS_ID = "statsID";
    public static final String STATS_PACE = "pace";
    public static final String STATS_DISTANCE = "distance";
    public static final String STATS_TIME = "time";

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/CourseProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/CourseProvider.data.text";
}
