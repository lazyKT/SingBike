package com.example.singbike.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.R;

import java.util.List;

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<ActivityRecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView activityTitle, activityTime;
        private final ImageView activityIndicatorIcon;

        public ViewHolder (View v) {
            super(v);

            this.activityIndicatorIcon = v.findViewById (R.id.activityIndicatorIcon);
            this.activityTitle = v.findViewById (R.id.activityItemTitle);
            this.activityTime = v.findViewById (R.id.activityTime);
        }

        public void bind (UserActivity userActivity) {

            final String type = (userActivity.getType()).substring(0, 1).toUpperCase() + (userActivity.getType()).substring(1);
            this.activityTitle.setText (type);
            this.activityTime.setText (userActivity.getTime());

            switch (userActivity.getType()) {
                case "ride":
                    this.activityIndicatorIcon.setImageResource (R.drawable.logo);
                    break;
                case "top-up":
                    this.activityIndicatorIcon.setImageResource (R.drawable.visa);
                    break;
                case "booking":
                    this.activityIndicatorIcon.setImageResource (R.drawable.calendar);
                    break;
                default:
                    this.activityIndicatorIcon.setImageResource (R.drawable.edit_profile);
                    break;
            }

        }

    }

    private List<UserActivity> activities;

    public ActivityRecyclerViewAdapter (List<UserActivity> activities) {
        this.activities = activities;
    }

    public void setActivities (List<UserActivity> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from (viewGroup.getContext())
                .inflate(R.layout.activity_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind(this.activities.get(position));
    }

    @Override
    public int getItemCount () { return this.activities.size();}
}
