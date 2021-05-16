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

    /* implement OnClickListener on individual RecyclerView Item */
    public interface TransactionItemOnClickListener {
        void transactionItemOnClick (Transaction transaction);
    }

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

        public void bind (final Transaction transaction, Context context, final TransactionItemOnClickListener listener) {
            this.transactionType.setText (transaction.getType());
            this.transactionTime.setText (transaction.getTime());
            this.amountTV.setText (transaction.getAmountStr());

            /* if transaction type is TopUp, set text color to Green */
            if (transaction.getType().equals("Top Up")) {
                this.amountTV.setTextColor(Color.GREEN);
                this.sgdTV.setTextColor(Color.GREEN);
            }
            else {
                /* if transaction type is Ride, set text color to Red */
                this.amountTV.setTextColor(Color.RED);
                this.sgdTV.setTextColor(Color.RED);
            }

            /* on Click action on each Transaction */
            itemView.setOnClickListener (
                    new View.OnClickListener () {
                        @Override
                        public void onClick (View v) {
                            listener.transactionItemOnClick(transaction);
                        }
                    }
            );

        }

    }

    private ArrayList<Transaction> transactions;
    private Context context;
    private TransactionItemOnClickListener listener;

    public TransactionRecyclerAdapter (ArrayList<Transaction> transactions, Context context, TransactionItemOnClickListener listener) {
        this.transactions = transactions;
        this.context = context;
        this.listener = listener;
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
        viewHolder.bind(transactions.get(position), this.context, this.listener);
    }

    @Override
    public int getItemCount () {
        return transactions.size();
    }

}
