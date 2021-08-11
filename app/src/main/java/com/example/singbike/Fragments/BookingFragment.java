package com.example.singbike.Fragments;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.BookingHistoryAdapter;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Dialogs.LoadingDialog;
import com.example.singbike.MainActivity;
import com.example.singbike.Models.Booking;
import com.example.singbike.Models.Trip;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.RideActivity;
import com.example.singbike.ScannerActivity;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookingFragment extends Fragment {

    private static final String DEBUG_BOOKING = "DEBUG_BOOKING";
    private static final String CHANNEL_ID = "RESERVATION_CANCELLED";
    private static final int NOTIFICATION_ID = 6864740;

    private ArrayList<Booking> bookingList;
    private User user;
    private Booking currentBooking;
    private RetrofitServices services;
    private BookingHistoryAdapter bookingHistoryAdapter;
    private RecyclerView bookingHistoryRecyclerView;
    private LinearLayout loadingLayout;
    private TextView currentBookingStatusTV;
    private TextView currentBookingDateTimeTV;
    private TextView currentBookingBikeID;
    private CardView currentBookingCardView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLocation;
    private boolean locationPermissionGranted;
    private LoadingDialog loadingDialog;

    private final ActivityResultLauncher<String> requestLocationLauncher = registerForActivityResult (
            new ActivityResultContracts.RequestPermission(), isGranted -> locationPermissionGranted = isGranted
    );

    public BookingFragment () {
        super(R.layout.fragment_booking);
    }

    @Override
    public void onCreate (Bundle savedInstaceState) {
        super.onCreate (savedInstaceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition (R.transition.slide_right));
    }


    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        bookingList = new ArrayList<>();
        user = getUserDetails();
        Retrofit retrofit = RetrofitClient.getRetrofit();
        services = retrofit.create (RetrofitServices.class);

        requestLocationPermission ();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        /* Current Booking */
        loadingLayout = view.findViewById (R.id.loadingBookingLayout);
        currentBookingStatusTV = view.findViewById (R.id.currentBookingStatusTV);
        currentBookingCardView = view.findViewById(R.id.currentBookingCardView);
        currentBookingDateTimeTV = view.findViewById (R.id.currentBookingDateTime);
        currentBookingBikeID = view.findViewById (R.id.bikeID_currentBooking);

        /* on longPress (hold) Event on currentBooking CardView */
        currentBookingCardView.setOnLongClickListener(
                v -> true
        );

        /* Booking History */
        bookingHistoryRecyclerView = view.findViewById (R.id.bookingHistoryRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        bookingHistoryRecyclerView.setLayoutManager(layoutManager);

        bookingHistoryAdapter = new BookingHistoryAdapter(requireActivity(), bookingList);
        bookingHistoryRecyclerView.setAdapter (bookingHistoryAdapter);

        fetchLastReservation();

        currentBookingCardView.setOnClickListener(v1 -> {

            getDeviceLocation();

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog (
                    requireActivity(),
                    R.style.BottomSheetDialogTheme
            );
            final View bottomSheet = LayoutInflater.from (requireActivity())
                    .inflate (R.layout.current_reservation_actions, (requireActivity()).findViewById (R.id.currentBookingBottomSheet));
            final Button cancelReservationButton = bottomSheet.findViewById (R.id.cancelButton_CurrentReservation);
            final Button scanQRCodeButton = bottomSheet.findViewById (R.id.scanQRCodeButton_CurrentReservation);
            final Button manualKeyInButton = bottomSheet.findViewById (R.id.manualKeyInButton_CurrentReservation);
            final Button unlockBikeButton = bottomSheet.findViewById (R.id.manualUnlockButton_CurrentReservation);
            final Button cancelManualKeyInButton = bottomSheet.findViewById (R.id.cancelManualKeyInButton_CurrentReservation);
            final EditText bikeIDEditText = bottomSheet.findViewById (R.id.manualBikeID_CurrentReservation);

            cancelReservationButton.setOnClickListener (view1 -> {
                cancelReservation (currentBooking.getReservation_id());
                bottomSheetDialog.dismiss();
            });

            scanQRCodeButton.setOnClickListener (view1 -> {
                Intent scanIntent = new Intent (requireActivity(), ScannerActivity.class);
                scanIntent.putExtra ("MyLocation", myLocation);
                startActivity (scanIntent);
            });

            manualKeyInButton.setOnClickListener (view1 -> {
                scanQRCodeButton.setVisibility (View.GONE);
                cancelReservationButton.setVisibility (View.GONE);
                unlockBikeButton.setVisibility (View.VISIBLE);
                cancelManualKeyInButton.setVisibility (View.VISIBLE);
                bikeIDEditText.setVisibility (View.VISIBLE);
                manualKeyInButton.setVisibility(View.GONE);
            });

            cancelManualKeyInButton.setOnClickListener (view1 -> {
                scanQRCodeButton.setVisibility (View.VISIBLE);
                cancelReservationButton.setVisibility (View.VISIBLE);
                unlockBikeButton.setVisibility (View.GONE);
                cancelManualKeyInButton.setVisibility (View.GONE);
                bikeIDEditText.setVisibility (View.GONE);
                manualKeyInButton.setVisibility(View.VISIBLE);
            });

            manualKeyInButton.setOnClickListener (view1 -> {
                if (!bikeIDEditText.getText().toString().equals("")) {
                    loadingDialog = new LoadingDialog (requireActivity(), "Starting Ride ...");
                    completeReservation ();
                }
            });

            bottomSheetDialog.setContentView (bottomSheet);
            bottomSheetDialog.show();
        });
    }

    /* retrieve user details from SharedPreferences */
    private User getUserDetails () {
        Gson gson = new Gson();
        SharedPreferences userPrefs = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        String jsonData = userPrefs.getString ("UserDetails", "");
        return gson.fromJson (jsonData, User.class);
    }

    /* fetch user reservation (aka bookings) */
    private void fetchBookings () {

        if (user == null)
            return;

        final String url = String.format (Locale.getDefault(), "bikes/cust_reservations/%d", user.getID());

        Call<ResponseBody> call = services.getReservations (url);
        AppExecutor.getInstance().getNetworkOps().execute(() -> call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 204) {
                    requireActivity().runOnUiThread(() -> {
                        loadingLayout.setVisibility (View.GONE);
                        bookingHistoryRecyclerView.setVisibility (View.VISIBLE);
                    });
                }
                else if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            Gson gson = new Gson();
                            String bookingStr = jsonObject.getString ("reservations");
                            Booking[] bookings = gson.fromJson (bookingStr, Booking[].class);

                            requireActivity().runOnUiThread(() -> {
                                bookingList = new ArrayList<>(Arrays.asList(bookings));
                                Collections.reverse (bookingList);
                                setCurrentBooking (bookingList.get(0));
                                bookingHistoryAdapter.setBookings (bookingList);
                                bookingHistoryAdapter.notifyDataSetChanged();
                                loadingLayout.setVisibility (View.GONE);
                                bookingHistoryRecyclerView.setVisibility (View.VISIBLE);
                            });
                        }
                        catch (JSONException | IOException e) {
                            requireActivity().runOnUiThread(() ->
                                    displayErrorDialog ("RUNTIME_ERROR", e.getMessage())
                                );
                        }
                    }
                    else {
                        requireActivity().runOnUiThread(() ->
                                displayErrorDialog ("NETWORK_ERROR", "GET Reservations: Response Empty!")
                        );
                    }
                }
                else {
                    requireActivity().runOnUiThread(() ->
                            displayErrorDialog ("NETWORK_ERROR", "GET Reservations: Response Failed!")
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", "GET Reservations: " + t.getMessage());
            }
        }));
    }

    private void fetchLastReservation () {
        String url = String.format (Locale.getDefault(), "customers/last_reservation/%d", user.getID());

        Call<ResponseBody> call = services.getLastReservation (url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // check the reservation,
                        // if it exceeds time limit (10 minutes), cancel and show notification to user, and create a new
                        // if it doesn't exceed the time and still active, show an information to user that he/she cannot have more than one reservation
                        try {
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            if (jsonObject.getString("status").equals("active")) {
                                // check the time
                                // if reservation created time is more than 10 minutes
                                if (Utils.isReservationExpired (jsonObject.getString("created_at"))) {
                                    // reservation expired
                                    // cancelled it
                                    // show notification
                                    showNotification();
                                    cancelReservation (jsonObject.getInt("reservation_id"));
                                }
                                else {
                                    fetchBookings();
                                }
                            }
                            else {
                                fetchBookings();
                            }
                        }
                        catch (JSONException | IOException e) {
                            displayErrorDialog ("RUNTIME ERROR", e.getMessage());
                        }
                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", "GET LAST Reservation: Response is Empty!");
                    }
                }
                else {
                    displayErrorDialog ("NETWORK_ERROR", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }

    private void cancelReservation (int reservationID) {
        final String url = String.format (Locale.getDefault(), "bikes/cancel_reservation/%d", reservationID);

        Call<ResponseBody> call = services.cancelReservation (url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 204) {
                    // good
                    fetchBookings();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }

    private void setCurrentBooking (Booking booking) {
        if (booking.getBookingStatus().equals("active")) {
            currentBooking = booking;
            currentBookingBikeID.setText (booking.getBikeID());
            currentBookingDateTimeTV.setText (booking.getBookingDate());
            currentBookingStatusTV.setText (booking.getBookingStatus());
        }
        else {
            currentBookingCardView.setVisibility (View.GONE);
        }
    }

    /* show notification to the user that the previous reservation is expired!!! */
    private void showNotification () {
        CharSequence name = getString(R.string.app_name);
        String description = "You have reserved a bike for more than 10 minutes and the bike has been released back to other users.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel (CHANNEL_ID, name, importance);
        channel.setDescription (description);
        NotificationManager manager = requireActivity().getSystemService (NotificationManager.class);
        manager.createNotificationChannel (channel);

        Intent intent = new Intent (requireActivity(), MainActivity.class);
        intent.putExtra ("goto", "reservation");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(requireActivity(), CHANNEL_ID)
                .setSmallIcon (R.drawable.logo)
                .setContentTitle ("Reservation Expired")
                .setStyle (new Notification.BigTextStyle()
                        .bigText (description))
                .setContentIntent(pendingIntent)
                .setAutoCancel (true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from (requireActivity());
        notificationManagerCompat.notify (NOTIFICATION_ID, builder.build());
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

    /* get device location */
    private void getDeviceLocation () {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // set last known location as current location
                        myLocation = task.getResult();
                    }
                });
            }

        }
        catch (SecurityException e) {
            displayErrorDialog ("SecurityException: Location Error!", e.getMessage());
        }
    }


    private void completeReservation () {

    }


    private void startRiding () {

        if (user == null)
            return;

        String startPointStr = String.format (Locale.getDefault(), "(%.5f, %.5f)", myLocation.getLatitude(), myLocation.getLongitude());

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

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (requireActivity(), title, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }
}
