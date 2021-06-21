package com.example.singbike.Fragments.AccountTab;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.singbike.Models.Ride;
import com.example.singbike.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RideDetailsFragment extends Fragment implements OnMapReadyCallback {

    private Ride ride = null;
    private TextView rideDetailsTitle, promoFareTextView, totalFareTextView, rideFareTextView;

    public RideDetailsFragment () { super(R.layout.ride_details);}

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        /* receive data from the ride history fragment */
        getParentFragmentManager().setFragmentResultListener("ride_history_key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                ride = result.getParcelable ("ride_detail");
            }
        });

        rideDetailsTitle = view.findViewById (R.id.rideDetailsTitle);
        ImageButton backButton = view.findViewById(R.id.backButton_RideDetails);
        rideFareTextView = view.findViewById (R.id.rideFare_RideDetails);
        promoFareTextView = view.findViewById (R.id.promoFare_RideDetails);
        totalFareTextView = view.findViewById (R.id.totalFare_RideDetails);
        Button reportIssueButton = view.findViewById(R.id.reportProblemButton_RideDetails);

        backButton.setOnClickListener (
            new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    // go back to ride history
                    (requireActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed (true)
                            .replace (R.id.fragmentContainerView, RideHistoryFragment.class, null)
                            .addToBackStack ("ride_detail")
                            .commit();
                }
            }
        );

        reportIssueButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // report any issues regarding this ride
                }
            }
        );

        /* display ride path on map */
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType (GoogleMap.MAP_TYPE_TERRAIN);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance (mapOptions);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add (R.id.mapContainer_RideDetails, mapFragment)
                .commit();
        mapFragment.getMapAsync (this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ride != null) {
            rideDetailsTitle.setText (ride.getRideTime());
            rideFareTextView.setText (ride.getRideFare());
            promoFareTextView.setText (ride.getPromo());
            totalFareTextView.setText (ride.getTotalFare());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng tripStart = new LatLng(1.2830, 103.8513); // somewhere near Raffles Place MRT
        LatLng tripEnd = new LatLng (1.2832, 103.8522);
        // new CameraPosition(target, zoom, tilt, bearing)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(tripStart, 18, 45, 45)));

        // add trip start marker
        googleMap.addMarker (
            new MarkerOptions()
            .position(tripStart)
            .icon (BitmapDescriptorFactory.fromResource (R.drawable.start))
        );

        // add trip end marker
        googleMap.addMarker(
            new MarkerOptions()
            .position (tripEnd)
            .icon (BitmapDescriptorFactory.fromResource (R.drawable.end))
        );

        PolylineOptions polylineOptions = new PolylineOptions()
                .add (tripStart)
                .add (new LatLng(1.2830, 103.8515))
                .add (new LatLng(1.2831, 103.8518))
                .add (new LatLng(1.2831, 103.8521))
                .add (tripEnd)
                .width (10)
                .color(Color.BLUE);
        googleMap.addPolyline (polylineOptions);
    }
}
