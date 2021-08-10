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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.Trip;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TransactionRequest;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.Services.MyLocationService;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String DEBUG_RIDE_STATE = "DEBUG_RIDE_STATE";
    private static final String DEBUG_RIDE_LOCATION = "DEBUG_RIDE_LOCATION";
    private static final String DEBUG_RIDE_TiME = "DEBUG_RIDE_TiME";
    private static final String DEBUG_RIDE_ACTIVITY = "DEBUG_RIDE_ACTIVITY";
    private boolean isLocationGranted, initMapDone = false;
    private TextView timeTextView;
    private long startTime;
    private Handler handler;
    private Runnable runnable;
    private GoogleMap map;
    private List<Location> visitedLocations = new ArrayList<>();

    private MyLocationService myLocationService;
    private boolean mBound = false;

    private Trip trip;
    private RetrofitServices services;
    private Retrofit retrofit;
    private ServiceConnection serviceConnection;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceStates) {
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_ride);

        Button finishRideButton = findViewById(R.id.finishRide_Button);
        timeTextView = findViewById(R.id.timeTextView_Ride);

        Log.d (DEBUG_RIDE_STATE, "Inside onCreate Method!");

        Intent intent = getIntent();
        isLocationGranted = intent.getBooleanExtra("isLocationGranted", false);

        // request location permission
        if (!isLocationGranted) requestLocationPermission();

        user = getUserDetails();
        if (user == null)
            startActivity (new Intent (RideActivity.this, MainActivity.class));

        // get trip created from Home Fragment
        trip = getIntent().getParcelableExtra ("trip");

        if (trip == null) {
            displayErrorDialog ("Application Error!", "Trip is NULL!!");
        }

        // create service connection for bound service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d (DEBUG_RIDE_ACTIVITY, "onServiceConnected!");
                MyLocationService.LocalBinder localBinder = (MyLocationService.LocalBinder) service;
                myLocationService = localBinder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d (DEBUG_RIDE_ACTIVITY, "onServiceDisconnected!");
                mBound = false;
            }
        };

        retrofit = RetrofitClient.getRetrofit();
        services = retrofit.create (RetrofitServices.class);

        startTime = System.currentTimeMillis();
        initMap();

        /* start timer to record the ride time */
        startTimer();

        // show notification to user that location tracking is being started
        Log.d (DEBUG_RIDE_ACTIVITY, "Foreground Service staring inside onResume()");
        startForeGroundService();

        finishRideButton.setOnClickListener(v -> {
            endTrip();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d (DEBUG_RIDE_ACTIVITY, "onStart()");
        /* bind MyLocationService */
        Intent intent = new Intent (RideActivity.this, MyLocationService.class);
        bindService (intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        Runnable runnable = () -> {
            Log.d (DEBUG_RIDE_ACTIVITY, "mBound : " + mBound + ", Bound Service Starting inside onResume()");
            if (mBound) {
                visitedLocations = myLocationService.getVisitedLocations();
                updateDeviceLocationOnMap();
                handler.postDelayed (this.runnable, 5000);
            }
        };
        handler.postDelayed (runnable, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d (DEBUG_RIDE_ACTIVITY, "onStop()");
        /* unbind MyLocationService */
        Log.d (DEBUG_RIDE_ACTIVITY, "Bound Service unbind inside onStop()");
        unbindService (serviceConnection);
        mBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setRequestLocationUpdates (RideActivity.this, false);
        stopForeGroundService();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d (DEBUG_RIDE_STATE, "Inside onMapReady Method!");
        this.map = googleMap;

        updateMapUI();

        setLastKnownLocationOnMap();
    }

    private User getUserDetails () {
        Gson gson = new Gson();
        SharedPreferences userPrefs = getSharedPreferences ("User", Context.MODE_PRIVATE);
        String jsonData = userPrefs.getString ("UserDetails", "");
        return gson.fromJson (jsonData, User.class);
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

    /* set current location and move the map position on activity start */
    private void setLastKnownLocationOnMap () {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (RideActivity.this);
        try {
            if (isLocationGranted) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(RideActivity.this, location -> {
                            if (location != null) {
                                map.moveCamera (CameraUpdateFactory.newLatLngZoom(
                                        new LatLng (location.getLatitude(), location.getLongitude()),
                                        20
                                ));
                                initMapDone = true;
                            }
                        });
            }
        }
        catch (SecurityException e) {
            displayErrorDialog ("SecurityException: Permission Error", e.getMessage());
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
                requestLocationPermission();
            }
        } catch (SecurityException se) {
            displayErrorDialog ("SecurityException: Permission Error", se.getMessage());
        }
    }

    private void updateDeviceLocationOnMap () {
        Log.d (DEBUG_RIDE_LOCATION, "updateDeviceLocationOnMap()");
        if (!initMapDone)
            return;

        for (Location location : visitedLocations) {
            Log.d (DEBUG_RIDE_LOCATION, "updateDeviceLocationOnMap(): move camera");
            Log.d (DEBUG_RIDE_LOCATION, location.toString());
            map.moveCamera (CameraUpdateFactory.newLatLngZoom(
                    new LatLng (location.getLatitude(), location.getLongitude()),
                    18
            ));
        }
    }

    /* display error message */
    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog(RideActivity.this, title, message);
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void stopForeGroundService () {
        Intent stopIntent = new Intent (RideActivity.this, MyLocationService.class);
        stopIntent.setAction("stop");
        startForegroundService (stopIntent);
    }

    private void startForeGroundService () {
        Intent startIntent = new Intent (this, MyLocationService.class);
        startIntent.setAction ("start");
        startForegroundService (startIntent);
    }

    private void endTrip () {
        if (trip == null)
            return;

        final String url = String.format (Locale.getDefault(), "/customers/trips/%d", trip.getTripID());

        TripRequest tripRequest = new TripRequest(
                trip.getTripID(),
                Utils.locationToStringFormat (visitedLocations.get (visitedLocations.size())),
                "",
                0.00,
                3.12,
                5.21
        );

        Call<ResponseBody> call = services.endTrip (url, new TripRequest());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        try {
                            JSONObject object = new JSONObject (response.body().string());
                            JSONObject tripObject = object.getJSONObject ("trip");

                            // record user activity
                            AppExecutor.getInstance().getDiskIO().execute(() -> {
                                Utils.insertUserActivity (RideActivity.this, "ride", user.getID());
                            });
                            // perform transaction
                            performTransaction (tripObject.getDouble("total_fare"));

                        }
                        catch (JSONException | IOException e) {
                            displayErrorDialog ("APPLICATION ERROR!", e.getMessage());
                        }
                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", "END Trip: Response Body is Empty!!");
                    }
                }
                else {
                    displayErrorDialog ("NETWORK_ERROR", "END Trip: Response Body is Empty!!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }

    private void performTransaction (double amount) {
        if (user == null)
            return;

        Call<ResponseBody> call = services.createTransaction (new TransactionRequest(user.getID(), amount, "ride"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // cancel the timer runnable
                        handler.removeCallbacks(runnable);
                        Utils.setRequestLocationUpdates (RideActivity.this, false);
                        stopForeGroundService();
                        startActivity(new Intent(RideActivity.this, MainActivity.class));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }
}
