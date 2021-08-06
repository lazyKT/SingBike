package com.example.singbike;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class RideActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener {

    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_ride);

        Button finishRideButton = findViewById (R.id.finishRide_Button);

        initMap();

        finishRideButton.setOnClickListener (v -> {
            startActivity (new Intent(RideActivity.this, MainActivity.class));
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setBuildingsEnabled (true);

        LatLng rafflesPlace = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        // new CameraPosition(target, zoom, tilt, bearing)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(rafflesPlace, 20, 45, 45)));
    }

    /* initiate map */
    private void initMap () {
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType (GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled (false)
                .rotateGesturesEnabled (true)
                .tiltGesturesEnabled (true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);

        getSupportFragmentManager().beginTransaction()
                .add (R.id.mapView_Ride, mapFragment)
                .commit();

        mapFragment.getMapAsync (this);
    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMove() {

    }
}
