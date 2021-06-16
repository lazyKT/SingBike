package com.example.singbike.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.ActivityRecyclerViewAdapter;
import com.example.singbike.Models.UserActivity;
import com.example.singbike.R;

import java.util.ArrayList;

public class ActivityFragment extends Fragment {

    public ActivityFragment () {
        super (R.layout.fragment_activity);
    }

    @Override
    public void onCreate (Bundle savedInstaceState) {
        super.onCreate (savedInstaceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition (R.transition.slide_right));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<UserActivity> activities = new ArrayList<>();
        activities.add (new UserActivity("Ride", "18 June 2021 5:20 pm"));
        activities.add (new UserActivity("Booking", "18 June 2021 5:15 pm"));
        activities.add (new UserActivity("Ride", "17 June 2021 7:23 pm"));
        activities.add (new UserActivity("Edit-Profile", "17 June 2021 12:15 pm"));
        activities.add (new UserActivity("Ride", "17 June 2021 9:30 am"));
        activities.add (new UserActivity("Top-up", "16 June 2021 9:23 pm"));

        final RecyclerView activityRecyclerView = view.findViewById (R.id.activityRecyclerView);
        /* set layout manager */
        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        activityRecyclerView.setLayoutManager (layoutManager);

        /* set adapter */
        ActivityRecyclerViewAdapter adapter = new ActivityRecyclerViewAdapter(activities);
        activityRecyclerView.setAdapter (adapter);
    }
}
