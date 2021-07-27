package com.example.singbike.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.LocalStorage.Achievement;
import com.example.singbike.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AchievementRvAdapter extends RecyclerView.Adapter<AchievementRvAdapter.ViewHolder> {

    private List<Achievement> achievements;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView achievementTV;
        private final LinearLayout achievementLayout;
        private final ProgressBar progBar;

        public ViewHolder (View view) {
            super(view);
            this.achievementTV = view.findViewById (R.id.achievementTextView);
            this.progBar = view.findViewById (R.id.achievementProgBar);
            this.achievementLayout = view.findViewById (R.id.achievementLayout);
        }

        public void bind (Context context, Achievement achievement) {
            this.achievementTV.setText (achievement.getTitle());
            this.progBar.setMax (achievement.getGoal());
            this.progBar.setProgress (achievement.getCurrentScore());
            if (achievement.getCompleted()) {
                achievementLayout.setBackgroundColor (ContextCompat.getColor(context, R.color.white));
            }
            else {
                achievementLayout.setBackgroundColor (ContextCompat.getColor(context, R.color.background));
            }
        }
    }

    public AchievementRvAdapter (Context context, List<Achievement> achievements) {
        this.achievements = achievements;
        this.context = context;
    }

    public void setAchievements (List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int position) {

        View v = LayoutInflater.from (viewGroup.getContext())
                .inflate(R.layout.achievement_items, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind(this.context, this.achievements.get(position));
    }

    @Override
    public int getItemCount () {
        return achievements.size();
    }
}
