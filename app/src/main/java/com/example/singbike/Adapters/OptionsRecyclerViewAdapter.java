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

    /* to handle onClick Events on Recycler View Items */
    public interface onOptionsClickListener {
        void onOptionsClick (String options);
    }

    /* instantiate onOptionsClick Listener */
    private final onOptionsClickListener optionsClickListener;

    /* Inner class to provide the reference to the custom view */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView optionTV;

        public  ViewHolder (View v) {
            super (v);

            optionTV = (TextView) v.findViewById(R.id.optionTV);
        }

        public void bind (final String option, final onOptionsClickListener optionsClickListener) {
            // set option text to TextView
            this.optionTV.setText (option);
            itemView.setOnClickListener (
                    new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            // options onClick Action
                            optionsClickListener.onOptionsClick(option);
                        }
                    }
            );
        }
    }

    /**
     * Initialize the option elements which will be displayed inside recyclerview
     * @param options -> Account Tab Options
     */
    public OptionsRecyclerViewAdapter (String[] options, onOptionsClickListener optionsClickListener) {
        this.optionsContents = options;
        this.optionsClickListener = optionsClickListener;
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
        // 1-st argv is to set the options contents into respective custom view by position
        // 2-nd argv is for the onClickListener of the each options content
        viewHolder.bind (this.optionsContents[position], this.optionsClickListener);
    }

    // get the size of contents count
    @Override
    public int getItemCount () {
        return optionsContents.length;
    }


}
