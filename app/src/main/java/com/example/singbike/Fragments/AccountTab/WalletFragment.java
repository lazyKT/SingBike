package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.TransactionRecyclerAdapter;
import com.example.singbike.Models.Transaction;
import com.example.singbike.R;

import java.util.ArrayList;

public class WalletFragment extends Fragment {

    private static final String DEBUG_ON_ATTACH = "DEBUG_ON_ATTACH";
    private ArrayList<Transaction> transactions;

    public WalletFragment () {
        super (R.layout.fragment_wallet);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        /* this method will run as soon as WalletFragment is attached to Host Activity */
        super.onAttach(context);
        Log.d (DEBUG_ON_ATTACH, "Wallet Fragment Attached!!");

        /*
         * Currently, I haven't developed the server yet.
         * After I have finished on server part, the transactions data will be collected from server
         * inside this method, via the network requests.
         * For now, I am using dummy static data for demonstration purpose.
         */

        /* init dummy data */
        transactions  = new ArrayList<>();
        transactions.add (new Transaction("Top Up", "Wed, 21 Apr, 15:40", 5.00));
        transactions.add (new Transaction("Ride", "Wed, 21 Apr, 15:42", 1.00));
        transactions.add (new Transaction("Top Up", "Thurs, 22 Apr, 11:40", 5.00));
        transactions.add (new Transaction("Ride", "Fri, 23 Apr, 15:40", 3.00));
        transactions.add (new Transaction("Top Up", "Wed, 21 Apr, 15:40", 10.00));
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {

        super.onViewCreated (view, savedInstanceState);

        final TextView balanceTextView = view.findViewById (R.id.balanceTV);
        final Button topUpButton = view.findViewById (R.id.topUpBtn);
        final RecyclerView transactionRecyclerView = view.findViewById (R.id.transactionRecyclerView);

        balanceTextView.setText("5");

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        transactionRecyclerView.setLayoutManager(layoutManager);

        TransactionRecyclerAdapter adapter = new TransactionRecyclerAdapter(transactions, requireActivity());
        transactionRecyclerView.setAdapter(adapter);
    }

}
