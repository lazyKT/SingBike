package com.example.singbike.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.R;

import java.util.Random;

public class AchievementRvAdapter extends RecyclerView.Adapter<AchievementRvAdapter.ViewHolder> {

    private String[] achievements;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView achievementTV;
        private final ProgressBar progBar;

        public ViewHolder (View view) {
            super(view);
            this.achievementTV = view.findViewById(R.id.achievementTextView);
            this.progBar = view.findViewById(R.id.achievementProgBar);
        }

        public void bind (String achievementStr) {
            this.achievementTV.setText(achievementStr);
            this.progBar.setProgress(new Random().nextInt() * 10);
        }
    }

    public AchievementRvAdapter (String[] achievements) {
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
        viewHolder.bind(this.achievements[position]);
    }

    @Override
    public int getItemCount () {
        return achievements.length;
    }
}
