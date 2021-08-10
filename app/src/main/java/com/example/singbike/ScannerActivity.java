package com.example.singbike;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Fragments.HomeFragment;
import com.example.singbike.Models.Trip;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.google.gson.Gson;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private User user;
    private Location myLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView (R.layout.activity_scanner);
        scannerView = new ZXingScannerView(this);
        setContentView (scannerView);

        if (getIntent() != null) {
            myLocation = getIntent().getParcelableExtra ("MyLocation");
        }

        user = getUserDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler (this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        // get qr code result and send back to home fragment
        Log.d ("QRCODE_RESULT", result.getText());
        startRiding();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }

    private User getUserDetails () {
        Gson gson = new Gson();
        SharedPreferences userPrefs = getSharedPreferences ("User", Context.MODE_PRIVATE);
        String jsonData = userPrefs.getString ("UserDetails", "");
        return gson.fromJson (jsonData, User.class);
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
                            Intent rideIntent = new Intent (ScannerActivity.this, RideActivity.class);
                            rideIntent.putExtra ("trip", trip);
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
                    displayErrorDialog ("NETWORK_ERROR", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", "POST Trip: " + t.getMessage());
            }
        });
    }

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (ScannerActivity.this, title, message);
        dialog.show (getSupportFragmentManager(), dialog.getTag());
    }
}
