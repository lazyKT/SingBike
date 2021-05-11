package com.example.singbike.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.R;


public class QuickTopUpRVAdapter extends RecyclerView.Adapter<QuickTopUpRVAdapter.ViewHolder> {

    /* onClick Action on quickTopUp */
    public interface TopUpOnClickListener {
        public void onClickTopUpListener (String amountStr);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView quickTopUpTextView;

        public ViewHolder (View v) {
            super(v);
            this.quickTopUpTextView = v.findViewById (R.id.quickTopUpTextView);
        }

        public void bind (final String amount, final TopUpOnClickListener listener) {
            String amountStr = String.format("%s %c", amount, '$');
            this.quickTopUpTextView.setText(amountStr);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClickTopUpListener(amount);
                        }
                    }
            );
        }
    }

    private String[] quickTopUpAmounts;
    private TopUpOnClickListener topUpOnClickListener;

    public QuickTopUpRVAdapter (Context context, TopUpOnClickListener topUpOnClickListener) {
        this.quickTopUpAmounts = context.getResources().getStringArray(R.array.quickTopUpAmounts);
        this.topUpOnClickListener = topUpOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int ViewType) {

        View v = LayoutInflater.from (parent.getContext())
                .inflate (R.layout.quick_topup, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind(this.quickTopUpAmounts[position], this.topUpOnClickListener);
    }

    @Override
    public int getItemCount () {
        return this.quickTopUpAmounts.length;
    }
}
