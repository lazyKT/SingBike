package com.example.singbike.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.singbike.Dialogs.ReservationDialog;
import com.example.singbike.Fragments.AccountTab.ReportFragment;
import com.example.singbike.Models.User;
import com.example.singbike.R;
import com.example.singbike.RideActivity;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnMarkerClickListener {

    public HomeFragment () {
        super (R.layout.fragment_home);
    }

    private static final String DEBUG_MAP = "GOOGLE_MAP_DEBUG";
    private static final String DEBUG_CAMERA_PERMISSION = "DEBUG_CAM_PERMISSION";
    private static final String DEBUG_FRAGMENT = "DEBUG_HOME_FRAG";
    private static final String DEBUG_SHARED_PREFS = "DEBUG_SHARED_PREFS";

    private static final int CAMERA_ACCESS = 0;
    private static final int LOCATION_PERMISSION_CODE = 1;
    private boolean locationPermissionGranted = false;

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d (DEBUG_FRAGMENT, "OnCreate Home Fragment!");
        getParentFragmentManager().setFragmentResultListener("reservation", this,
                (requestKey, bundle) -> {
                    // receive reservation result from dialog fragment
                    int confirmResult = bundle.getInt ("reserve_confirm");
                    if (confirmResult == 0)
                        Toast.makeText (requireActivity(), "You have cancel the bike reservation!!", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final TextView creditScoreTextView = view.findViewById (R.id.creditScoreTextView_Home);
        final TextView balanceTextView = view.findViewById (R.id.balanceTextView_Home);

        /* get user details from local storage (SharedPreferences) */
        User user = getUserDetails();
        if (user != null) {
            Log.d (DEBUG_SHARED_PREFS, user.toString());
            creditScoreTextView.setText (String.valueOf(user.getCredits()));
            balanceTextView.setText (String.valueOf(user.getBalance()));
        }

        // request location permission
        requestLocationPermission();

        // map configuration
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
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
                v -> {

                    if (!locationPermissionGranted) {
                        requestLocationPermission();
                        return;
                    }

                    // check bluetooth feature is supported in user's device
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter == null) {
                        Toast.makeText (requireActivity(), "Your device does not support Bluetooth!", Toast.LENGTH_LONG).show();
//                        return;
                    }

                    if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                        // ask user to enable bluetooth
                        Intent enableBluetooth = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        requestBluetoothLauncher.launch (enableBluetooth);
                    }

                    // ask users whether open camera to scan qr code or key in manually
                    openUnlockOptions();
                }
        );

        final FloatingActionButton reportButton = view.findViewById (R.id.reportButton_Home);
        reportButton.setOnClickListener(
                v -> (requireActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace (R.id.fragmentContainerView, ReportFragment.class, null)
                        .setReorderingAllowed (true)
                        .addToBackStack ("report")
                        .commit()
        );
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d (DEBUG_MAP, "onMapReady Called!");

        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);

        // initial zoom setting
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // initial map setting
        googleMap.setBuildingsEnabled(true);

        // onClick Event on Marker Icons
        googleMap.setOnMarkerClickListener(this);

        // set initial point on the map at the start
        LatLng rafflesPlace = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        // new CameraPosition(target, zoom, tilt, bearing)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(rafflesPlace, 20, 45, 45)));

        // add markers
        addMarkers(googleMap);
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

    private void addMarkers (GoogleMap map) {
        Marker bike1 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2830, 103.8513))
                        .title ("30ad3214")
                        .snippet ("Bike-1")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike1.setTag (0);

        Marker bike2 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2831, 103.8514))
                        .title ("0032f413c")
                        .snippet ("Bike-2")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike2.setTag (1);

        Marker bike3 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2831, 103.8513))
                        .title ("003214ca32")
                        .snippet ("Bike-3")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike3.setTag (2);

        Marker bike4 = map.addMarker(
                new MarkerOptions ()
                        .position (new LatLng(1.2833, 103.8513))
                        .title ("003214a345")
                        .snippet ("Bike-4")
                        .icon (BitmapDescriptorFactory.fromResource(R.drawable.cycle32))
        );
        bike4.setTag (3);
    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
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
                v -> bottomSheetDialog.dismiss()
        );

        /* unlock now button -> open camera to scan bike's QR code */
        unlockNowButton.setOnClickListener(
                v -> {
                    // open camera
                    viewCamera();
                }
        );

        /* reserve now button -> go to reservation (booking) tab */
        reserveNowButton.setOnClickListener(
                v -> {
                    // reserve now
                    bottomSheetDialog.dismiss();
                    // send bikeID to the dialogFragment
                    ReservationDialog dialog = ReservationDialog.newInstance(marker.getTitle());
                    dialog.show (getChildFragmentManager(), dialog.getTag());
                }
        );

        bottomSheetDialog.setContentView (bottomSheet);
        bottomSheetDialog.show();

        return false;
    }

    /**
     * Open the BottomSheet which offers users to unlock a bike.
     * Option-1: Open the camera and scan the qr code
     * Option-2: Manually key in (enter) the bike id.
     */
    private void openUnlockOptions () {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                requireActivity(),
                R.style.BottomSheetDialogTheme
        );
        final View bottomSheet = LayoutInflater.from (requireActivity())
                .inflate (R.layout.unlock_options_bottomsheet, (LinearLayout) requireActivity().findViewById(R.id.unlcok_options_bottomsheet));

        final Button scanQRCodeButton = bottomSheet.findViewById (R.id.scanQRCodeButton);
        final Button manualKeyInButton = bottomSheet.findViewById (R.id.manualKeyInButton);
        final Button cancelManualKeyInButton = bottomSheet.findViewById (R.id.cancelButton_UnlockOptionsBtmSheet);
        final EditText manualKeyInBikeIDET = bottomSheet.findViewById (R.id.bikeIDEditText);

        cancelManualKeyInButton.setVisibility (View.GONE);
        manualKeyInBikeIDET.setVisibility (View.GONE);

        scanQRCodeButton.setOnClickListener (
                v -> {
                    // open camera to scan qr code
                    viewCamera();
                }
        );

        manualKeyInButton.setOnClickListener (
                v -> {
                    // show text box to key in manually
                    scanQRCodeButton.setVisibility (View.GONE);
                    cancelManualKeyInButton.setVisibility (View.VISIBLE);
                    manualKeyInBikeIDET.setVisibility (View.VISIBLE);

                    if (!manualKeyInBikeIDET.getText().toString().equals("")) {
                        Intent intent = new Intent (requireActivity(), RideActivity.class);
                        startActivity (intent);
                    }
                }
        );

        cancelManualKeyInButton.setOnClickListener(
                v -> {
                    scanQRCodeButton.setVisibility (View.VISIBLE);
                    cancelManualKeyInButton.setVisibility (View.GONE);
                    manualKeyInBikeIDET.setVisibility (View.GONE);
                }
        );

        bottomSheetDialog.setContentView (bottomSheet);
        bottomSheetDialog.show();
    }

    /* request permission to get the device's location */
    private void requestLocationPermission () {
        if (ContextCompat.checkSelfPermission (
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
        else {
            // request to access device's location
//            ActivityCompat.requestPermissions (requireActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            requestLocationLauncher.launch (Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    /* retrieve user details from SharedPreferences */
    private User getUserDetails () {
        Gson gson = new Gson();
        SharedPreferences userPrefs = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        String jsonData = userPrefs.getString ("UserDetails", "");
        return gson.fromJson (jsonData, User.class);
    }

    /* check whether the camera permission has been already granted, otherwise, request one */
    private void viewCamera () {

        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // permission allowed
            Log.d (DEBUG_CAMERA_PERMISSION, "Camera Permission in Home Fragment. GRANTED!!!");
            openCamera();
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // tell the user why we need them to allow camera permission
            Log.d (DEBUG_CAMERA_PERMISSION, "Camera Permission in Home Fragment. EXPLAIN WHY U NEED THIS!");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.camera_request)
                .setPositiveButton(R.string.allow, (dialog, which) -> {
                    dialog.dismiss();
                    requireActivity().requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_ACCESS);
                })
                .setNegativeButton(R.string.never, (dialog, which) -> dialog.dismiss());
            builder.create();
            builder.show();
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

    /* Request for Bluetooth Permission */
    private final ActivityResultLauncher<Intent> requestBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // user has allowed and enabled bluetooth
                    openUnlockOptions();
                }
            });

    private final ActivityResultLauncher<String> requestLocationLauncher = registerForActivityResult (new ActivityResultContracts.RequestPermission(),
            isGranted -> {
        locationPermissionGranted = isGranted;
    });

}
