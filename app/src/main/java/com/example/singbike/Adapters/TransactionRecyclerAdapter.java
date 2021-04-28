package com.example.singbike.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Models.Transaction;
import com.example.singbike.R;

import java.util.ArrayList;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /* inner class */
        final TextView transactionType, transactionTime, amountTV, sgdTV;

        public ViewHolder(View v) {
            super(v);

            this.transactionType = v.findViewById (R.id.transTypeTV);
            this.transactionTime = v.findViewById (R.id.transactionTimeTV);
            this.amountTV = v.findViewById (R.id.transactionAmountTV);
            this.sgdTV = v.findViewById (R.id.sgdTV);
        }

        public void bind (Transaction transaction, Context context) {
            this.transactionType.setText (transaction.getType());
            this.transactionTime.setText (transaction.getTime());
            this.amountTV.setText (transaction.getAmountStr());

            if (transaction.getType().equals("Top Up")) {
                this.amountTV.setTextColor(Color.GREEN);
                this.sgdTV.setTextColor(Color.GREEN);
            }
            else {
                this.amountTV.setTextColor(Color.RED);
                this.sgdTV.setTextColor(Color.RED);
            }

        }

    }

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionRecyclerAdapter (ArrayList<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int position) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.transactions, viewGroup, false);

        return new ViewHolder (v);
    }


    @Override
    public void onBindViewHolder (ViewHolder viewHolder, final int position) {
        viewHolder.bind(transactions.get(position), this.context);
    }

    @Override
    public int getItemCount () {
        return transactions.size();
    }

}
