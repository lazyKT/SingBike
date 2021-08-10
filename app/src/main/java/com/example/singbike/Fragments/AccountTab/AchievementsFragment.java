package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.singbike.Adapters.AchievementRvAdapter;
import com.example.singbike.LocalStorage.Achievement;
import com.example.singbike.LocalStorage.AchievementDatabase;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;

import java.util.ArrayList;
import java.util.List;

public class AchievementsFragment extends Fragment {

    public AchievementsFragment () {
        super (R.layout.fragment_achievement);
    }

    private static final String DEBUG_ACHIEVEMENTS = "DEBUG_ACHIEVEMENTS";
    private List<Achievement> achievementList;

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final LinearLayout loadingLayout = view.findViewById (R.id.loadingLayout_Achievements);
        final RecyclerView achievementRecyclerView = view.findViewById(R.id.achievementRecyclerView);
        /* swipe down to refresh (reload) the achievements' state */
        final SwipeRefreshLayout refreshLayout = view.findViewById (R.id.swipeRefresheLayout);
        final AchievementRvAdapter adapter = new AchievementRvAdapter(requireActivity(), achievementList);
        LinearLayoutManager layoutManager = new LinearLayoutManager (getActivity());
        achievementRecyclerView.setLayoutManager (layoutManager);

        achievementRecyclerView.setAdapter(adapter);
        Log.d (DEBUG_ACHIEVEMENTS, "onViewCreated!");

        /* fetching achievements and progress from Local Database (Rooms) */
        AppExecutor.getInstance().getDiskIO().execute(() -> {
            // Run on different thread (not UI Thread)
            Log.d (DEBUG_ACHIEVEMENTS, "Retrieving Achievements");
            /* retrieve achievements from local database  */
            AchievementDatabase achievementDatabase = AchievementDatabase.getInstance (requireContext());
            achievementList = achievementDatabase.achievementDAO().getAll();

            /* update UI Thread */
            requireActivity().runOnUiThread (() -> {
                Log.d (DEBUG_ACHIEVEMENTS, "Updated UI Thread");
                loadingLayout.setVisibility (View.GONE);
                refreshLayout.setVisibility (View.VISIBLE);
                adapter.setAchievements (achievementList);
                adapter.notifyDataSetChanged();
            });
        });

        /* refresh event */
        refreshLayout.setOnRefreshListener (
                () -> Toast.makeText(requireActivity(), "Refreshing", Toast.LENGTH_LONG).show()
        );

    }

}
