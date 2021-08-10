package com.example.singbike.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.BookingHistoryAdapter;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.Booking;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;
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

    private static final String DEBUG_BOOKINGHISTROY = "DEBUG_BOOKINGHISTORY";

    private ArrayList<Booking> bookingList;
    private Booking currentBooking;
    private User user;
    private RetrofitServices services;
    private BookingHistoryAdapter bookingHistoryAdapter;
    private RecyclerView bookingHistoryRecyclerView;
    private LinearLayout loadingLayout;
    private TextView currentBookingStatusTV;
    private TextView currentBookingDateTimeTV;
    private TextView currentBookingBikeID;

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

        /* Current Booking */
        loadingLayout = view.findViewById (R.id.loadingBookingLayout);
        currentBookingStatusTV = view.findViewById (R.id.currentBookingStatusTV);
        CardView currentBookingCardView = view.findViewById(R.id.currentBookingCardView);
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

        fetchBookings();
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
                                currentBooking = bookingList.get (bookingList.size() - 1);
                                currentBookingBikeID.setText (currentBooking.getBike_id());
                                currentBookingDateTimeTV.setText (currentBooking.getBookingDate());
                                currentBookingStatusTV.setText (currentBooking.getBookingStatus());
                                Collections.reverse (bookingList);
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


    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (requireActivity(), title, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }
}
