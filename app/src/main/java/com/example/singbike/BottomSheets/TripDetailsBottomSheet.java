package com.example.singbike.BottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.singbike.Models.Trip;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TripDetailsBottomSheet extends BottomSheetDialogFragment {

    private final Trip trip;

    public TripDetailsBottomSheet (Trip trip) {
        this.trip = trip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView (inflater, container, savedInstanceState);

        View v = inflater.inflate (R.layout.ride_details, container, false);

        final TextView tripIDTV = v.findViewById (R.id.rideID_RideDetails);
        final TextView tripFareTV = v.findViewById (R.id.rideFare_RideDetails);
        final TextView promoFareTV = v.findViewById (R.id.promoFare_RideDetails);
        final TextView totalFareTV = v.findViewById (R.id.totalFare_RideDetails);

        tripIDTV.setText (String.valueOf(trip.getTripID()));
        tripFareTV.setText (String.valueOf(trip.getFare()));
        promoFareTV.setText (String.valueOf(trip.getPromo()));
        totalFareTV.setText (String.valueOf(trip.getTotal_fare()));

        return v;
    }
}
