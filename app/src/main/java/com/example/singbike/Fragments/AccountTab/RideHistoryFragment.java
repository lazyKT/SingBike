package com.example.singbike.Fragments.AccountTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.singbike.Adapters.RideHistoryRecyclerViewAdapter;
import com.example.singbike.AuthActivity;
import com.example.singbike.BottomSheets.TripDetailsBottomSheet;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Dialogs.LoadingDialog;
import com.example.singbike.Models.Ride;
import com.example.singbike.Models.Trip;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;
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

public class RideHistoryFragment extends Fragment {

    public RideHistoryFragment () {
        super(R.layout.fragment_ride_history);
    }

    private ArrayList<Trip> tripList;
    private LoadingDialog loadingDialog;
    private User user;
    private RideHistoryRecyclerViewAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog (requireActivity(), "loading trips ...");
        loadingDialog.show (requireActivity().getSupportFragmentManager(), loadingDialog.getTag());
        tripList = new ArrayList<>();
        user = getUserDetails();

        if (user == null) {
            // log out
            logOut();
        }


        final RecyclerView rideHistoryRecyclerView = view.findViewById (R.id.rideHistoryRecyclerView);
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById (R.id.swipeRefreshLayout_RideHistory);

        swipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // refresh the ride history
                }
            }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        rideHistoryRecyclerView.setLayoutManager (layoutManager);

        adapter = new RideHistoryRecyclerViewAdapter(tripList,
                trip -> {

                    TripDetailsBottomSheet bottomSheet = new TripDetailsBottomSheet(trip);
                    bottomSheet.show (requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
                });
        rideHistoryRecyclerView.setAdapter (adapter);

        fetchUserTrips();
    }

    private User getUserDetails () {
        Gson gson = new Gson();
        SharedPreferences userPrefs = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        String jsonData = userPrefs.getString ("UserDetails", "");
        return gson.fromJson (jsonData, User.class);
    }

    private void logOut () {
        SharedPreferences userPrefs = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        SharedPreferences preferences = requireActivity().getSharedPreferences(getString(R.string.authState), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences.Editor editor1 = userPrefs.edit();
        editor1.clear();
        editor1.apply();
        editor.clear();
        editor.apply();
        // logout user
        Intent intent = new Intent (requireActivity(), AuthActivity.class);
        startActivity(intent);
    }

    /* fetch user trips */
    private void fetchUserTrips () {
        if (user == null)
            return;

        final String url = String.format (Locale.getDefault(), "customers/customer_trips/%d", user.getID());

        /* run in network thread */
        AppExecutor.getInstance().getNetworkOps().execute(() -> {
            Retrofit retrofit = RetrofitClient.getRetrofit();
            RetrofitServices services = retrofit.create (RetrofitServices.class);

            Call<ResponseBody> call = services.fetchUserTrips (url);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject (response.body().string());
                                String tripsStr = jsonObject.getString ("trips");
                                Log.d ("DEBUG_RIDE_HISTORY", tripsStr);
                                Gson gson = new Gson();
                                Trip[] trips = gson.fromJson (tripsStr, Trip[].class);

                                requireActivity().runOnUiThread (() -> {
                                    tripList = new ArrayList<>(Arrays.asList (trips));
                                    Collections.reverse (tripList);
                                    adapter.setTrips (tripList);
                                    loadingDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                });
                            }
                            catch (JSONException | IOException e) {
                                displayErrorDialog ("RUNTIME ERROR", e.getMessage());
                            }
                        }
                        else {
                            displayErrorDialog("NETWORK_ERROR", "GET Trips: Response is empty!!!");
                        }
                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                }
            });
        });

    }

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (requireActivity(), title, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

}
