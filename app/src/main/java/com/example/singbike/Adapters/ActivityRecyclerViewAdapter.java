package com.example.singbike.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Models.UserActivity;
import com.example.singbike.R;

import java.util.ArrayList;

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

            this.activityTitle.setText (userActivity.getActivityType());
            this.activityTime.setText (userActivity.getActivityTime());

            switch (userActivity.getActivityType()) {
                case "Ride":
                    this.activityIndicatorIcon.setImageResource (R.drawable.logo);
                    break;
                case "Top-up":
                    this.activityIndicatorIcon.setImageResource (R.drawable.visa);
                    break;
                case "Booking":
                    this.activityIndicatorIcon.setImageResource (R.drawable.calendar);
                    break;
                default:
                    this.activityIndicatorIcon.setImageResource (R.drawable.edit_profile);
                    break;
            }

        }

    }

    private final ArrayList<UserActivity> activities;

    public ActivityRecyclerViewAdapter (ArrayList<UserActivity> activities) {
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
