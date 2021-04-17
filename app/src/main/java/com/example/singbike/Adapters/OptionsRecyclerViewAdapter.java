package com.example.singbike.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.R;

public class OptionsRecyclerViewAdapter extends RecyclerView.Adapter<OptionsRecyclerViewAdapter.ViewHolder> {


    private String[] optionsContents;

    /* Inner class to provide the reference to the custom view */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView optionTV;

        public  ViewHolder (View v) {
            super (v);

            optionTV = (TextView) v.findViewById(R.id.optionTV);
        }

        public TextView getOptionTV () {
            return optionTV;
        }
    }

    /**
     * Initialize the option elements which will be displayed inside recyclerview
     * @param options -> Account Tab Options
     */
    public OptionsRecyclerViewAdapter (String[] options) {
        optionsContents = options;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, int viewType) {
        // load the custom option item view
        View view = LayoutInflater.from (viewGroup.getContext())
                .inflate (R.layout.option_item, viewGroup, false);

        return new ViewHolder (view);
    }

    // bind custom contents (options) into parent viewgroup (recyclerview)
    @Override
    public void onBindViewHolder (ViewHolder viewHolder, final int position) {
        // set the options contents into respective custom view by position
        viewHolder.getOptionTV().setText(optionsContents[position]);
    }

    // get the size of contents count
    @Override
    public int getItemCount () {
        return optionsContents.length;
    }


}
