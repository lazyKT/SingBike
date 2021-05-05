package com.example.singbike.Fragments.AccountTab;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.singbike.Adapters.AchievementRvAdapter;
import com.example.singbike.R;

public class AchievementsFragment extends Fragment {

    public AchievementsFragment () {
        super (R.layout.fragment_achievement);
    }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        final RecyclerView achievementRecyclerView = view.findViewById(R.id.achievementRecyclerView);
        String[] achievements = getResources().getStringArray(R.array.achievements);
        AchievementRvAdapter adapter = new AchievementRvAdapter(achievements);

        LinearLayoutManager layoutManager = new LinearLayoutManager (getActivity());
        achievementRecyclerView.setLayoutManager (layoutManager);

        achievementRecyclerView.setAdapter(adapter);

        /* swipe down to refresh (reload) the achievements' state */
        final SwipeRefreshLayout refreshLayout = view.findViewById (R.id.swipeRefresheLayout);

        /* refresh event */
        refreshLayout.setOnRefreshListener (
          new SwipeRefreshLayout.OnRefreshListener() {
              @Override
              public void onRefresh () {
                  Toast.makeText(requireActivity(), "Refreshing", Toast.LENGTH_LONG).show();
              }
          }
        );

    }

}
