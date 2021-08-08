package com.example.singbike.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.ActivityRecyclerViewAdapter;
import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.LocalStorage.UserActivityDatabase;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;


import java.util.Collections;
import java.util.List;

public class ActivityFragment extends Fragment {

    private List<UserActivity> activities;
    private LinearLayout userActivityLayout;
    private ProgressBar loadingProgressBar;
    private ActivityRecyclerViewAdapter adapter;

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

        userActivityLayout = view.findViewById (R.id.userActivities_layout);
        loadingProgressBar = view.findViewById (R.id.loadingUserActivityProgBar);
        final RecyclerView activityRecyclerView = view.findViewById (R.id.activityRecyclerView);
        /* set layout manager */
        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        activityRecyclerView.setLayoutManager (layoutManager);

        /* set adapter */
        adapter = new ActivityRecyclerViewAdapter(activities);
        activityRecyclerView.setAdapter (adapter);

        fetchAllActivities();
    }

    /* get all user activities from local storage (RoomDB) */
    private void fetchAllActivities () {
        AppExecutor.getInstance().getDiskIO().execute(() -> {
            UserActivityDatabase userActivityDatabase = UserActivityDatabase.getInstance(requireActivity());
            activities = userActivityDatabase.userActivityDAO().getAll();
            Collections.reverse (activities);

            requireActivity().runOnUiThread(() -> {
                loadingProgressBar.setVisibility (View.GONE);
                userActivityLayout.setVisibility(View.VISIBLE);
                adapter.setActivities (activities);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
