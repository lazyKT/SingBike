package com.example.singbike;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.Ride;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class RideActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener {

    private final String DEBUG_RIDE_TiME = "DEBUG_RIDE_TiME";
    private boolean isLocationGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView timeTextView;
    private long startTime;
    private Handler handler;
    private Runnable runnable;
    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_ride);

        Intent intent = getIntent();
        isLocationGranted = intent.getBooleanExtra("isLocationGranted", false);

        if (!isLocationGranted) requestLocationPermission();

        startTime = System.currentTimeMillis();

        Button finishRideButton = findViewById(R.id.finishRide_Button);
        timeTextView = findViewById(R.id.timeTextView_Ride);

        initMap();

        /* start timer to record the ride time */
        startTimer();

        finishRideButton.setOnClickListener(v -> {
            // cancel the timer runnable
            handler.removeCallbacks(runnable);
            startActivity(new Intent(RideActivity.this, MainActivity.class));
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.map = googleMap;

        updateMapUI();
        getDeviceLocation();

        LatLng rafflesPlace = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        // new CameraPosition(target, zoom, tilt, bearing)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(rafflesPlace, 20, 45, 45)));
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

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMove() {

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
            isGranted -> {
                isLocationGranted = isGranted;
            }
    );


    /* update map UI */
    private void updateMapUI() {
        if (map == null)
            return;

        try {
            if (isLocationGranted) {
                map.setOnCameraMoveStartedListener(this);
                map.setOnCameraMoveListener(this);

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

    /* display error message */
    private void displayErrorDialog (String message) {
        ErrorDialog dialog = new ErrorDialog(RideActivity.this, "SecurityException: Permission Error", message);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }
}
