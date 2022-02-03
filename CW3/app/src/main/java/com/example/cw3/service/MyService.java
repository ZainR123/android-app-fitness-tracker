package com.example.cw3.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.cw3.R;
import com.example.cw3.activities.NewCourse;

import java.util.Timer;
import java.util.TimerTask;

//Service to track location, time and notifications
public class MyService extends Service {

    //Declare objects and tick count
    private LocationManager locationManager;
    private MyLocationListener listener;
    private NotificationManager notifications;
    private NotificationCompat.Builder buildNotification;
    private Timer timer = null;
    private int tickCount = 0;

    //Getter for notification manager
    public NotificationManager getNotifications() {
        return notifications;
    }

    //Getter for notification builder
    public NotificationCompat.Builder getBuildNotification() {
        return buildNotification;
    }

    //Binder class allowing other applications to bind and create connections
    public class MyBinder extends Binder implements IInterface {

        //Defines interface that specifies how a client can communicate with the service
        @Override
        public IBinder asBinder() {
            return this;
        }

        //Pause and start functions for timer and gps
        public void pauseGPS() {
            MyService.this.pauseGPS();
        }
        public void pauseTimer() {
            MyService.this.pauseTimer();
        }
        public void startGPS() {
            MyService.this.startGPS();
        }
        public void startTimer() {
            MyService.this.startTimer();
        }
        //Getters for notification objects
        public NotificationManager getNotifications() {
            return MyService.this.getNotifications();
        }
        public NotificationCompat.Builder getBuildNotification() { return MyService.this.getBuildNotification(); }
    }

    //Initialise notifications
    private void createNotifications() {

        //Create new notification manager and cancel any notifications in the same ID slot
        notifications = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifications.cancel(1);
        String CHANNEL_ID = "100";
        CharSequence name = "Running App";
        //Create new notification channel with high importance so notification stays at the top of the list
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        //add channel to the notification manager
        notifications.createNotificationChannel(channel);
        //Create new intent between new course activity and this service
        Intent intent = new Intent(this, NewCourse.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //Create pending intent with selected intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //Build notification, setting the notification to silent and making it top priority
        //Also adding so whenever the notification is pressed it will return to the activity
        buildNotification = new NotificationCompat.Builder(this, CHANNEL_ID).setNotificationSilent().setWhen(0)
                .setPriority(NotificationCompat.PRIORITY_MAX).setContentIntent(pendingIntent);
    }

    //Creates pending button intents for the notification bar
    public PendingIntent buttonIntents(String name) {
        Intent intent = new Intent(name);
        return PendingIntent.getBroadcast(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    //Update notification buttons for pausing, resuming and finishing
    //Add custom notification logo then notify the notification manager of the changes
    public void startNotifications() {
        buildNotification.setContentTitle("Running App")
                .setSmallIcon(R.drawable.outline_run_circle_black_24)
                .addAction(R.drawable.ic_launcher_background, "Start", buttonIntents("Start"))
                .addAction(R.drawable.ic_launcher_background, "Finish", buttonIntents("Finish"))
                .addAction(R.drawable.ic_launcher_background, "Pause", buttonIntents("Pause"));
        notifications.notify(1, buildNotification.build());
    }

    //Timer thread which updates timer every second, sending a broadcast to be received
    public void startTimer() {

        timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                ++tickCount;
                Intent intentTime = new Intent("Time");
                intentTime.putExtra("Timer", tickCount);
                sendBroadcast(intentTime);
            }
        };
        timer.schedule(myTask, 1000, 1000);
    }

    //Location tracker which requests user location at every update, only if permissions enabled
    public void startGPS() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, listener);
    }

    //Pause timer by cancelling thread
    public void pauseTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    //Pause GPS by removing location updates
    public void pauseGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

    //Location tracker subclass, on location change send a broadcast with the most recent latitude and longitude
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Intent intentLocation = new Intent("Location");
            intentLocation.putExtra("Latitude", loc.getLatitude());
            intentLocation.putExtra("Longitude", loc.getLongitude());
            sendBroadcast(intentLocation);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }
    }

    //On creation of the service instantiate the MyService class and create the notification manager, and start the timer/GPS
    @Override
    public void onCreate() {
        Log.d("g53mdp", "service onCreate");
        super.onCreate();
        createNotifications();
        startGPS();
        startTimer();
        startNotifications();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d("g53mdp", "service onBind");
        return new MyBinder();
    }

    //When the foreground service is started then declare the notification as foregrounded
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("g53mdp", "service onStartCommand");
        startForeground(1, buildNotification.build());
        return Service.START_STICKY;
    }

    //When service ends then reset the threads and cancel all notifications open, stop the foreground service
    @Override
    public void onDestroy() {
        Log.d("g53mdp", "service onDestroy");
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
            locationManager = null;
            listener = null;
        }
        if(timer != null) {
            timer.cancel();
            timer = null;
            tickCount = 0;
        }
        stopForeground(true);
        notifications.cancelAll();
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("g53mdp", "service onRebind");
        super.onRebind(intent);
    }

    //When the task is prematurely ended stop the service
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("g53mdp", "service onTaskRemoved");
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("g53mdp", "service onUnbind");
        return true;
    }
}