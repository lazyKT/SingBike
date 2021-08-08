package com.example.singbike.Utilities;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.singbike.RideActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class GetLocationWorker extends Worker {

    public GetLocationWorker (@NonNull Context context, @NonNull WorkerParameters params) {
        super (context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval (10000);
        locationRequest.setFastestInterval (5000);
        locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d ("DEBUG_RIDE_BG",location.toString());
                }
            }
        };

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (getApplicationContext());

        startLocationRequest (fusedLocationProviderClient, locationRequest, locationCallback);

        return Result.success();
    }

    private void startLocationRequest (FusedLocationProviderClient fusedLocationProviderClient, LocationRequest locationRequest, LocationCallback locationCallback) {
        try {
            Log.d ("DEBUG_RIDE_BG", "Starting Location Request!");
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        catch (SecurityException e) {
            if (e.getMessage() != null)
                Log.d ("DEBUG_RIDE_BG", e.getMessage());
        }
    }

}
