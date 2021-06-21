package com.example.singbike.Fragments.AccountTab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.singbike.Adapters.RideHistoryRecyclerViewAdapter;
import com.example.singbike.Models.Ride;
import com.example.singbike.R;

import java.util.ArrayList;

public class RideHistoryFragment extends Fragment {

    public RideHistoryFragment () {
        super(R.layout.fragment_ride_history);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Ride> rides = new ArrayList<>();
        rides.add (new Ride("18 June Thurs 7:30 pm", "1.4 km", "2.51", "0"));
        rides.add (new Ride("15 June Tues 9:30 pm", "2.2 km", "3.54", "0"));
        rides.add (new Ride("15 June Tues 7:30 pm", "1.1 km", "1.8", "1.3"));
        rides.add (new Ride("14 June Mon 2:30 pm", "1.24 km", "1.9", "0"));
        rides.add (new Ride("11 June Fri 7:30 pm", "0.9 km", "1.6", "0"));

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

        RideHistoryRecyclerViewAdapter adapter = new RideHistoryRecyclerViewAdapter(rides,
            new RideHistoryRecyclerViewAdapter.RideHistoryItemOnClickListener() {
                @Override
                public void rideItemOnClickListener(Ride ride) {

                    Bundle result = new Bundle();
                    result.putParcelable ("ride_detail", ride);
                    getParentFragmentManager().setFragmentResult ("ride_history_key", result);

                    (requireActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace (R.id.fragmentContainerView, RideDetailsFragment.class, null)
                            .setReorderingAllowed (true)
                            .addToBackStack ("ride-details")
                            .commit();
                }
        });
        rideHistoryRecyclerView.setAdapter (adapter);
    }

}
