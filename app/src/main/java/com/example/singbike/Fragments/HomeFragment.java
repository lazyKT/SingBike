package com.example.singbike.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Dialogs.LoadingDialog;
import com.example.singbike.Dialogs.ReservationDialog;
import com.example.singbike.Fragments.AccountTab.ReportFragment;
import com.example.singbike.Models.Trip;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.RideActivity;
import com.example.singbike.ScannerActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    public HomeFragment () {
        super (R.layout.fragment_home);
    }

    private static final String DEBUG_MAP = "DEBUG_MAP";
    private static final String DEBUG_CAMERA_PERMISSION = "DEBUG_CAM_PERMISSION";
    private static final String DEBUG_FRAGMENT = "DEBUG_HOME_FRAG";
    private static final String DEBUG_SHARED_PREFS = "DEBUG_SHARED_PREFS";

    private Location myLocation;
    private LatLng defaultLocation; // move to this location if real location gets some errors
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleMap map;
    private static final int CAMERA_ACCESS = 0;
    private boolean locationPermissionGranted = false;
    private User user;
    private LoadingDialog loadingDialog;

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
        user = getUserDetails();
        if (user != null) {
            Log.d (DEBUG_SHARED_PREFS, user.toString());
            creditScoreTextView.setText (String.valueOf(user.getCredits()));
            balanceTextView.setText (String.valueOf(user.getBalance()));
        }

        // request location permission
        requestLocationPermission();

        // set default Location to somewhere near Raffles Place MRT Station
        defaultLocation = new LatLng(1.2830, 103.8513);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        initMap();

        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    myLocation = location;
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition(
                                    new LatLng (myLocation.getLatitude(), myLocation.getLongitude()),
                                    18,
                                    45,
                                    45
                            )
                    ));
                }
            }
        };


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
                        displayErrorDialog ("System Error! Bluetooth Not Found!", "Your device does not support Bluetooth!");
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

        Log.d (DEBUG_MAP, "onMapReady!");

        this.map = googleMap;

        /* update map interface */
        updateMapUI();

//        showDummyLocation();
        getDeviceLocation();

        // add markers
        addMarkers(googleMap);

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        catch (SecurityException e) {
            displayErrorDialog ("SECURITY EXCEPTION", e.getMessage());
        }
    }

    /* Initiate GoogleMap and Configurations */
    private void initMap () {
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
    }

    /* update map UI */
    private void updateMapUI () {

        if (map == null)
            return;

        // initial zoom setting
        map.getUiSettings().setZoomControlsEnabled(false);
        // initial map setting
        map.setBuildingsEnabled(true);

        // onClick Event on Marker Icons
        map.setOnMarkerClickListener(this);

        try {
            map.setMyLocationEnabled (locationPermissionGranted);
            map.getUiSettings().setMyLocationButtonEnabled (locationPermissionGranted);
        }
        catch (SecurityException e) {
            displayErrorDialog ("SecurityException: Permission Error!", e.getMessage());
        }
    }

    /* get device location */
    private void getDeviceLocation () {
        Log.d (DEBUG_MAP, "Getting Device Location!!");
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Log.d (DEBUG_MAP, "LastKnownLocation request is successful!!!");
                       // set last known location as current location
                       myLocation = task.getResult();
                       if (myLocation != null) {
                           Log.d (DEBUG_MAP, "LastKnownLocation is not NULL!!!");
                           map.moveCamera(CameraUpdateFactory.newCameraPosition(
                                   new CameraPosition(
                                           new LatLng (myLocation.getLatitude(), myLocation.getLongitude()),
                                           18,
                                           45,
                                           45
                                   )
                           ));
                           addMarkers (map);
                       }
                   }
                   else {
                       Log.d (DEBUG_MAP, "LastKnownLocation is NULL!!!");
                       if (task.getException() != null)
                           displayErrorDialog ("SecurityException: Location Error!", task.getException().getMessage());

                       map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(defaultLocation, 20, 45, 45)));
                       map.getUiSettings().setMyLocationButtonEnabled (false);
                   }
                });
            }
        }
        catch (SecurityException e) {
            displayErrorDialog ("SecurityException: Location Error!", e.getMessage());
        }
    }

    private void addMarkers (GoogleMap map) {

        Log.d (DEBUG_MAP, "addMarkers!");

        if (myLocation == null) {
            Log.d (DEBUG_MAP, "myLocation is NULL!");
            return;
        }

        Log.d (DEBUG_MAP, "Current Location : " + myLocation.toString());

        for (int i = 0; i < 4; i++) {
            double d = (double)(i)/(double)(5000 * new Random().nextInt(5) + 1);
            double latitude = myLocation.getLatitude() + d;
            double longitude = myLocation.getLongitude() + d;
            Log.d (DEBUG_MAP, i + ". Latitude : " + latitude + ", Longitude : " + longitude );
            Marker marker = map.addMarker (
                new MarkerOptions ()
                    .position (new LatLng(myLocation.getLatitude() + d, myLocation.getLongitude() + d))
                    .title ("bike_sFWp04qr")
                    .icon (BitmapDescriptorFactory.fromResource (R.drawable.cycle32))
            );
            if (marker != null) {
                marker.setTag (i);
            }
        }
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
                .inflate (R.layout.bike_bottomsheet, requireActivity().findViewById(R.id.bikeBottomSheet));

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
                    ReservationDialog dialog = ReservationDialog.newInstance(marker.getTitle(), user.getID());
                    dialog.show (getChildFragmentManager(), dialog.getTag());
                }
        );

        bottomSheetDialog.setContentView (bottomSheet);
        bottomSheetDialog.show();

        return false;
    }

    private void createLocationRequest () {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval (10000);
        locationRequest.setFastestInterval (5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                .inflate (R.layout.unlock_options_bottomsheet, requireActivity().findViewById(R.id.unlcok_options_bottomsheet));

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
                        loadingDialog = new LoadingDialog (requireActivity(), "Starting ..");
                        loadingDialog.show (requireActivity().getSupportFragmentManager(), loadingDialog.getTag());
                        fetchBalance();
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
            openScanner();
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

    private void openScanner () {
        if (!checkCameraOK())
            return; // camera is not available

        Intent scanIntent = new Intent (requireActivity(), ScannerActivity.class);
        scanIntent.putExtra ("MyLocation", myLocation);
        startActivity (scanIntent);
    }

    private void startRiding () {

        if (user == null)
            return;

        String startPointStr = String.format (Locale.getDefault(), "(%.5f, %.5f)", myLocation.getLatitude(), myLocation.getLongitude());

        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.createTrip (new TripRequest.TripCreateRequest(user.getID(), startPointStr));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            Log.d ("TRIP_CREATE", "OK");
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            Log.d ("TRIP_CREATE", jsonObject.getJSONObject("trip").toString());
//                            Trip trip = new Trip (jsonObject.getJSONObject("trip"));
                            Gson gson = new Gson();
                            Trip trip = gson.fromJson (jsonObject.getJSONObject("trip").toString(), Trip.class);
                            Intent rideIntent = new Intent (requireActivity(), RideActivity.class);
                            rideIntent.putExtra ("trip", trip);
                            loadingDialog.dismiss();
                            startActivity (rideIntent);
                        }
                        catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", "POST Trip: Response Body is NULL!");
                    }
                }
                else {
                    displayErrorDialog ("NETWORK_ERROR", "POST Trip: Failed to get response!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", "POST Trip: " + t.getMessage());
            }
        });
    }

    private void fetchBalance () {
        if (user == null)
            return;

        final String url = String.format (Locale.getDefault(), "customers/%d", user.getID());
        final String NETWORK_ERROR = "NETWORK_ERROR";
        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.fetchProfile (url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            double balance = jsonObject.getJSONObject("customer").getDouble("balance");
                            if (balance < 5.00) {
                                // user must have at least 5$ in the balance to ride
                                displayErrorDialog ("LOW BALANCE", "User must have at least 5$ in the balance to ride");
                                loadingDialog.dismiss();
                                return;
                            }
                            startRiding();
                        }
                        catch (JSONException | IOException e) {
                            displayErrorDialog ("APPLICATION ERROR", e.getMessage());
                        }
                    }
                    else {
                        displayErrorDialog (NETWORK_ERROR, "GET PROFILE: Response Empty!");
                    }
                }
                else
                    displayErrorDialog (NETWORK_ERROR, "GET PROFILE: Response Failed!");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (requireActivity(), title, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }


    /* Request for Bluetooth Permission */
    private final ActivityResultLauncher<Intent> requestBluetoothLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // user has allowed and enabled bluetooth
                    openUnlockOptions();
                }
            });

    private final ActivityResultLauncher<String> requestLocationLauncher = registerForActivityResult (
            new ActivityResultContracts.RequestPermission(), isGranted -> locationPermissionGranted = isGranted
    );

}
