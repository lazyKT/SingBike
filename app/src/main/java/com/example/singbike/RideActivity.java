package com.example.singbike;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Services.MyLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String DEBUG_RIDE_STATE = "DEBUG_RIDE_STATE";
    private static final String DEBUG_RIDE_LOCATION = "DEBUG_RIDE_LOCATION";
    private static final String DEBUG_RIDE_TiME = "DEBUG_RIDE_TiME";
    private static final String DEBUG_RIDE_SERVICE = "DEBUG_RIDE_ACTIVITY";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "LOCATION_UPDATE_KEYS";
    private boolean isLocationGranted, requestingLocationUpdates, bound, isForeGround;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView timeTextView;
    private long startTime;
    private Handler handler;
    private Runnable runnable;
    private GoogleMap map;

    private MyLocationService myLocationService;
    private boolean mBound = false;


    private ServiceConnection serviceConnection;

    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
//    private MyLocationService locationService;


    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_ride);

        Button finishRideButton = findViewById(R.id.finishRide_Button);
        timeTextView = findViewById(R.id.timeTextView_Ride);

        Log.d (DEBUG_RIDE_STATE, "Inside onCreate Method!");

        Intent intent = getIntent();
        isLocationGranted = intent.getBooleanExtra("isLocationGranted", false);

        updateValuesFromBundles (savedInstanceStates);
        Log.d (DEBUG_RIDE_STATE, "From savedInstanceStates (Requesting Location) : " + requestingLocationUpdates);

        requestingLocationUpdates = true;

        if (!isLocationGranted) requestLocationPermission();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d (DEBUG_RIDE_SERVICE, "onServiceConnected!");
                MyLocationService.LocalBinder localBinder = (MyLocationService.LocalBinder) service;
                myLocationService = localBinder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d (DEBUG_RIDE_SERVICE, "onServiceDisconnected!");
                mBound = false;
            }
        };

        Intent startIntent = new Intent (this, MyLocationService.class);
        startIntent.setAction ("start");
        startService (startIntent);

        startTime = System.currentTimeMillis();
        initMap();

        /* start timer to record the ride time */
        startTimer();
//
//        /* create and configure location request to update the user routes during the ride  */
//        createLocationRequest ();
//
//        /* instantiate locationCallBack */
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult (@NonNull LocationResult result) {
////                Log.d (DEBUG_RIDE_LOCATION, "onLocationResult: Before 'for' loop");
//                for (Location location : result.getLocations()) {
//                    Log.d (DEBUG_RIDE_LOCATION, "onLocationResult: inside 'for' loop" + location.toString());
//                    updateLocationOnUI (location);
//                }
//            }
//        };



        finishRideButton.setOnClickListener(v -> {
            // cancel the timer runnable
            handler.removeCallbacks(runnable);
            Intent stopIntent = new Intent(this, MyLocationService.class);
            stopIntent.setAction("stop");
            startService(stopIntent);
            startActivity(new Intent(RideActivity.this, MainActivity.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d (DEBUG_RIDE_SERVICE, "onStart()");
        /* bind MyLocationService */
        Intent intent = new Intent (RideActivity.this, MyLocationService.class);
        bindService (intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d (DEBUG_RIDE_SERVICE, "mBound : " + mBound);
                if (mBound) {
                    int num = myLocationService.getRandomInts();
                    Log.d (DEBUG_RIDE_SERVICE, "" + num);

                    handler.postDelayed (this, 5000);
                }
            }
        };
        handler.postDelayed (runnable, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d (DEBUG_RIDE_SERVICE, "onStop()");
        /* unbind MyLocationService */
        unbindService (serviceConnection);
        mBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopIntent = new Intent(this, MyLocationService.class);
        stopIntent.setAction("stop");
        startService(stopIntent);
    }

    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        Log.d (DEBUG_RIDE_STATE, "Inside onSaveInstanceState Method!");
        outState.putBoolean (REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates);
        super.onSaveInstanceState (outState);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d (DEBUG_RIDE_STATE, "Inside onMapReady Method!");
        this.map = googleMap;

        updateMapUI();

        /* get Device Location and show the position on map */
//        getDeviceLocation();
    }

    /* initiate map */
    private void initMap() {
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.mapView_Ride, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
    }

    /* start timer */
    private void startTimer() {

        Log.d(DEBUG_RIDE_TiME, "Timer Start Processing ...");

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long milliSeconds = System.currentTimeMillis() - startTime;
                int seconds = (int) (milliSeconds / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timeTextView.setText(String.format(Locale.getDefault(), "%s:%s", padZero(minutes), padZero(seconds)));
                handler.postDelayed(this, 500);
            }
        };

        handler.postDelayed(runnable, 0);
        Log.d(DEBUG_RIDE_TiME, "Timer has been triggered!");

    }

    /* pad leading zeros to get two digit number */
    private String padZero(long n) {
        if (String.valueOf(n).length() > 1)
            return String.valueOf(n);

        return String.format(Locale.getDefault(), "0%d", n);
    }

    /* request device location permission */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                RideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationGranted = true;
        } else {
            requestLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /* launch location permission request */
    private final ActivityResultLauncher<String> requestLocationLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted -> isLocationGranted = isGranted
    );


    /* update map UI */
    private void updateMapUI() {
        if (map == null)
            return;

        try {
            if (isLocationGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.setBuildingsEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                requestLocationPermission();
            }
        } catch (SecurityException se) {
            displayErrorDialog (se.getMessage());
        }
    }

    /* get device location */
    private void getDeviceLocation() {

        try {
            if (isLocationGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(RideActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // set current position as last known location of device
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera (CameraUpdateFactory.newLatLngZoom (
                                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                                    20
                            ));
                        }
                        else {
                            Log.d (DEBUG_RIDE_TiME, "LastKnownLocation is NULL!!!");
                            if (task.getException() != null)
                                displayErrorDialog (task.getException().getMessage());
                            map.getUiSettings().setMyLocationButtonEnabled (false);
                        }
                    }
                });
            }
        }
        catch (SecurityException se) {
            displayErrorDialog (se.getMessage());
        }
    }

    private void createLocationRequest () {
        Log.d (DEBUG_RIDE_LOCATION, "Creating Location Request!");
        locationRequest = LocationRequest.create();
        locationRequest.setInterval (10000); // make location request every 10 seconds
        locationRequest.setFastestInterval (5000); // make nearly accurate location updates every 5 seconds
        locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationRequest () {
        try {
            Log.d (DEBUG_RIDE_LOCATION, "Starting Location Request!");
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        catch (SecurityException e) {
            displayErrorDialog (e.getMessage());
        }
    }

    private void stopLocationUpdates () {
        Log.d (DEBUG_RIDE_LOCATION, "Stopping Location Request!");
//        fusedLocationProviderClient.removeLocationUpdates (locationCallback);
    }

    private void updateValuesFromBundles (Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;
        requestingLocationUpdates = savedInstanceState.getBoolean (REQUESTING_LOCATION_UPDATES_KEY);
    }

    private void updateLocationOnUI (Location location) {
        Log.d (DEBUG_RIDE_LOCATION, "Updating Location on UI " + location.toString());
    }

    /* display error message */
    private void displayErrorDialog (String message) {
        ErrorDialog dialog = new ErrorDialog(RideActivity.this, "SecurityException: Permission Error", message);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
