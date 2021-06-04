package com.example.singbike.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.singbike.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnMarkerClickListener{

    public HomeFragment () {
        super (R.layout.fragment_home);
    }

    private static final String DEBUG_MAP = "GOOGLE_MAP_DEBUG";
    private final static String DEBUG_CAMERA_PERMISSION = "DEBUG_CAM_PERMISSION";

    private static final int CAMERA_ACCESS = 0;
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
                .add (R.id.map, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        final Button unlockButton = view.findViewById(R.id.unlockButton);
        unlockButton.setOnClickListener (
                new View.OnClickListener () {
                    @Override
                    public void onClick (View v)
                    {
                        // open camera
                        viewCamera();
                    }
                }
        );
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

        // onClick Event on Marker Icons
        map.setOnMarkerClickListener(this);

        // set initial point on the map at the start
        LatLng rafflesPlace = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        // new CameraPosition(target, zoom, tilt, bearing)
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(rafflesPlace, 20, 45, 45)));

        // add markers
        addMarkers(map);
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

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // when the user click on the marker

        // open bike unlock options
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
            requireActivity(),
            R.style.BottomSheetDialogTheme
        );

        final View bottomSheet = LayoutInflater.from(requireActivity())
                .inflate (R.layout.bike_bottomsheet, (LinearLayout) requireActivity().findViewById(R.id.bikeBottomSheet));

        final TextView bikeIDTitle = bottomSheet.findViewById (R.id.bikeID_bikeBottomSheet);
        final Button unlockNowButton = bottomSheet.findViewById (R.id.unlockNowButton_BikeBtmSheet);
        final Button reserveNowButton = bottomSheet.findViewById (R.id.reserveNowButton_BikeBtmSheet);
        final Button cancelButton = bottomSheet.findViewById (R.id.cancelButton_BikeBtmSheet);

        /* set title of the bike bottom sheet */
        bikeIDTitle.setText (marker.getTitle());

        /* cancel button click -> dismiss the bottom sheet */
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                }
        );

        /* unlock now button -> open camera to scan bike's QR code */
        unlockNowButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // open camera
                        viewCamera();
                    }
                }
        );

        /* reserve now button -> go to reservation (booking) tab */
        reserveNowButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // reserve now
                    }
                }
        );

        bottomSheetDialog.setContentView (bottomSheet);
        bottomSheetDialog.show();

        return false;
    }


    private void addMarkers (GoogleMap map) {
        Marker bike1 = map.addMarker(
            new MarkerOptions ()
                .position (new LatLng(1.2830, 103.8513))
                .title ("Bike1")
                .snippet ("Bike-1")
                .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike1.setTag (0);

        Marker bike2 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2831, 103.8514))
                        .title ("Bike2")
                        .snippet ("Bike-2")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike2.setTag (1);

        Marker bike3 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2831, 103.8513))
                        .title ("Bike3")
                        .snippet ("Bike-3")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike3.setTag (2);

        Marker bike4 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2833, 103.8513))
                        .title ("Bike4")
                        .snippet ("Bike-4")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike4.setTag (3);
    }


    private void viewCamera () {

        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // permission allowed
            Log.d (DEBUG_CAMERA_PERMISSION, "Camera Permission in Home Fragment. GRANTED!!!");
            openCamera();
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // tell the use why we need them to allow camera permission
            Log.d (DEBUG_CAMERA_PERMISSION, "Camera Permission in Home Fragment. EXPLAIN WHY U NEED THIS!");
        }
        else {
            requireActivity().requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_ACCESS);
            Log.d (DEBUG_CAMERA_PERMISSION, "Camera Permission in Home Fragment. REQUEST NEW!!!");
        }
    }

    private Boolean checkCameraOK () {
        // check whether the device's camera is available
        return requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private void openCamera () {
        if (!checkCameraOK())
            return; // camera is not available

        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(cameraIntent);
    }

}
