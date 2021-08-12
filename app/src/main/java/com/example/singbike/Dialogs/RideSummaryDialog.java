package com.example.singbike.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.MainActivity;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TransactionRequest;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.RideActivity;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RideSummaryDialog extends DialogFragment {

    public interface OnTripSummaryDismissListener {
        void onTripSummaryDismiss (double fare, double promoFare, double totalFare);
    }

    private final Context context;
    private double fare, promoFare, totalFare;
    private final int time;
    private OnTripSummaryDismissListener listener;

    public RideSummaryDialog (Context context, double fare, int time) {
        this.context = context;
        this.fare = fare;
        this.time = time;
        this.promoFare = 0.00;
        this.totalFare = fare;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTripSummaryDismissListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException (context.toString() + " must implement OnTripSummeryDismissListener Interface.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        super.onCreateDialog (savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder (this.context);
        View view = LayoutInflater.from (this.context)
                .inflate (R.layout.ride_summary_dialog, null);

        builder.setView (view);

        final Button applyPromoButton = view.findViewById (R.id.promoApplyButton);
        final Button makePaymentButton = view.findViewById (R.id.makePaymentButton);
        final EditText promoCodeEditText = view.findViewById (R.id.promoCodeET);
        final TextView rideTimeTV = view.findViewById (R.id.rideTime_RideSummary);
        final TextView totalFareTV = view.findViewById (R.id.totalFare_RideSummary);
        final TextView promoFareTV = view.findViewById (R.id.promoFare_RideSummary);
        final TextView fareTV = view.findViewById (R.id.rideFare_RideSummary);

        int rideMinutes = this.time / 60;
        int rideSeconds = this.time % 60;

        fareTV.setText (String.valueOf (this.fare));
        rideTimeTV.setText (String.format (Locale.getDefault(), "%d.%d", rideMinutes, rideSeconds));
        promoFareTV.setText (String.valueOf(promoFare));
        totalFareTV.setText (String.valueOf(totalFare));

        applyPromoButton.setOnClickListener(
                v -> {
                    promoFare = getPromoCodeValue(promoCodeEditText.getText().toString());
                    totalFare = fare - promoFare;
                    promoFareTV.setText (String.valueOf(promoFare));
                    totalFareTV.setText (String.valueOf(totalFare));
                }
        );

        makePaymentButton.setOnClickListener (
                v -> {
                    dismiss();
                    listener.onTripSummaryDismiss (fare, promoFare, totalFare);
                }
        );

        return builder.create();
    }

    private double getPromoCodeValue (String promoCode) {

        switch (promoCode) {
            case "GETWLC":
                return 0.50;
            case "2OFF":
                return 2.00;
            case "GET1D":
                return 1.00;
            case "GET2D":
                return 1.50;
            case "GETSTARTED":
                return 0.500;
            default:
                return 0.00;
        }
    }
}
