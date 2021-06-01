package com.example.singbike.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.singbike.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveCanceledListener {

    public HomeFragment () {
        super (R.layout.fragment_home);
    }

    private static final String DEBUG_MAP = "GOOGLE_MAP_DEBUG";
    private GoogleMap map;

    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // map configuration
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType(GoogleMap.MAP_TYPE_TERRAIN)
                .compassEnabled(false)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);

        // create new map fragment instance
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add (R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d (DEBUG_MAP, "onMapReady Called!");
        map = googleMap;

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);

        // initial zoom setting
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // initial map setting
        map.setBuildingsEnabled(true);

        // set initial point on the map at the start
        LatLng rafflesPlace = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        // new CameraPosition(target, zoom, tilt, bearing)
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(rafflesPlace, 20, 45, 45)));


        map.addMarker (new MarkerOptions()
                .position (rafflesPlace)
                .title ("Marker")
        );

    }

    @Override
    public void onCameraIdle() {
        // when the map camera is in IDLE position
    }

    @Override
    public void onCameraMove() {
        // when user moves the map
    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMoveCanceled() {

    }
}
