package com.example.singbike.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.singbike.R;
import com.example.singbike.RideActivity;
import com.example.singbike.Utilities.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyLocationService extends Service {

    private static final String DEBUG_RIDE_SERVICE = "DEBUG_RIDE_SERVICE";
    private static final int NOTIFICATION_ID = 1234;
    private static final String CHANNEL_ID = "SinBike_Channel";

    private final IBinder binder = new LocalBinder();

    private Handler handler;
    private Runnable runnable;

    private Notification.Builder builder; // for update notification
    private Notification notification;
    private NotificationChannel notificationChannel;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private List<Location> visitedLocations = new ArrayList<>();


    public class LocalBinder extends Binder {
        public MyLocationService getService() {
            return MyLocationService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d (DEBUG_RIDE_SERVICE, "onCreate()");
        createNotification();
        setFusedLocationProviderClient();
        createLocationRequest();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

//        if (locationCallback != null)
//            fusedLocationProviderClient.removeLocationUpdates (locationCallback);

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().contains("start")) {
                Log.d (DEBUG_RIDE_SERVICE, "onStartCommand : start");
                this.handler = new Handler();
                this.runnable = () -> {
                    startForeground (NOTIFICATION_ID, notification);
                    storeVisitedLocations();
                    boolean isActivityInBackground = intent.getBooleanExtra ("run_in_bg", false);
//                    if (isActivityInBackground) {
//                        // activity in background, continue requesting location updates
//                        handler.postDelayed (this.runnable, 3000);
//                    }
                };
                handler.post (runnable);
            }
            else {
                Log.d (DEBUG_RIDE_SERVICE, "onStartCommand : STOP");

                this.handler.removeCallbacks (runnable);

                if (locationCallback != null)
                    fusedLocationProviderClient.removeLocationUpdates (locationCallback);
                stopForeground (true);
                stopSelf();
            }
        }

        return START_STICKY;
    }

    private void createLocationRequest () {
        if (locationRequest != null)
            return;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(200000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void storeVisitedLocations () {
        Log.d (DEBUG_RIDE_SERVICE, "storeVisitedLocations()");
        try {

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
//                    Log.d (DEBUG_RIDE_SERVICE, "This is started by Foreground Service!!");
                    //                        Log.d(DEBUG_RIDE_SERVICE, location.toString());
                    visitedLocations.addAll(locationResult.getLocations());
                }
            };

            fusedLocationProviderClient.requestLocationUpdates (locationRequest, locationCallback, Looper.myLooper());
        }
        catch (SecurityException e) {
            Log.d (DEBUG_RIDE_SERVICE, e.getMessage());
        }
    }


    /*
    This will be run when the activity is in running state
    and will be unbind when the activity goes into onStop() state.
    Action: Update the Main Thread (UI Thread) from here.
     */
    public List<Location> getVisitedLocations () {
//        Log.d (DEBUG_RIDE_SERVICE, "getVisitedLocations()");

        try {

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    visitedLocations = locationResult.getLocations();
                }
            };

            createLocationRequest();

            if (locationRequest == null) {
                Log.d (DEBUG_RIDE_SERVICE, "locationRequest is NULL!!");
            }

            if (fusedLocationProviderClient == null) {
                Log.d (DEBUG_RIDE_SERVICE, "fusedLocationProviderClient is NULL!!");
            }

            if (locationCallback == null) {
                Log.d (DEBUG_RIDE_SERVICE, "locationCallBack is NULL!!");
            }

            fusedLocationProviderClient.requestLocationUpdates (locationRequest, locationCallback, Looper.myLooper());
        }
        catch (SecurityException e) {
            if (e.getMessage() != null)
                Log.d (DEBUG_RIDE_SERVICE, e.getMessage());
        }

        return this.visitedLocations;
    }

    private void setFusedLocationProviderClient () {
        if (this.fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    private void createNotification () {
        if (notification == null) {
            if (notificationChannel == null) {
                CharSequence name = getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent notificationIntent = new Intent(this, RideActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Location Tracking in Progrss ...")
                    .setContentIntent(pendingIntent);

            notification = builder.build();
        }
    }
}
