package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.TransactionRecyclerAdapter;
import com.example.singbike.Models.Transaction;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class WalletFragment extends Fragment {

    private static final String DEBUG_ON_ATTACH = "DEBUG_ON_ATTACH";
    private ArrayList<Transaction> transactions;

    private int amountAdded = 0;

    private TextView balanceTextView;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* fragment transition animation */
        TransitionInflater inflater = TransitionInflater.from(requireActivity());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

        /* receive data from TopUp Fragment */
        getParentFragmentManager().setFragmentResultListener ("TopUp", this,
            new FragmentResultListener() {
                @Override
                public void onFragmentResult (@NonNull String requestKey, @NonNull Bundle bundle) {
                    amountAdded = bundle.getInt ("amountAdded");
                }
            });

        Log.d ("onCreate_WalletFragment", String.valueOf(amountAdded));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d ("onResume_WalletFragment", String.valueOf(amountAdded));
        amountAdded = amountAdded + 5;
        balanceTextView.setText (String.valueOf(amountAdded));
    }

    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {

        super.onViewCreated (view, savedInstanceState);
        Log.d ("onViewCreated_Wallet", String.valueOf (amountAdded));

        balanceTextView = view.findViewById (R.id.balanceTV);
        final Button topUpButton = view.findViewById (R.id.topUpBtn);
        final RecyclerView transactionRecyclerView = view.findViewById (R.id.transactionRecyclerView);

        /* Top Up Wallet */
        topUpButton.setOnClickListener (
          new View.OnClickListener() {
              @Override
              public void onClick (View v) {
                  requireActivity().getSupportFragmentManager()
                          .beginTransaction()
                          .replace (R.id.fragmentContainerView, TopUpFragment.class, null)
                          .addToBackStack ("TopUp")
                          .setReorderingAllowed (true)
                          .commit();
              }
          }
        );
        amountAdded = amountAdded + 5;
        balanceTextView.setText (String.valueOf(amountAdded));

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        transactionRecyclerView.setLayoutManager(layoutManager);

        TransactionRecyclerAdapter adapter = new TransactionRecyclerAdapter(transactions, requireActivity(),
                new TransactionRecyclerAdapter.TransactionItemOnClickListener() {
                    @Override
                    public void transactionItemOnClick(Transaction transaction) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog (
                                requireActivity(),
                                R.style.BottomSheetDialogTheme
                        );

                        /* display bottom sheet for transaction info */
                        View bottomSheetView = LayoutInflater.from (requireActivity())
                                .inflate(R.layout.transaction_sheet, (LinearLayout)view.findViewById(R.id.transactionBottomSheet));
                        TextView transTypeTV, transAmountTV, transTimeTV;
                        transTypeTV = bottomSheetView.findViewById (R.id.transactionTypeTV_Sheet);
                        transAmountTV = bottomSheetView.findViewById (R.id.transactionAmountTV_Sheet);
                        transTimeTV = bottomSheetView.findViewById (R.id.transactionTimeTV_Sheet);

                        transTimeTV.setText(transaction.getTime());
                        transAmountTV.setText(transaction.getAmountStr());
                        transTypeTV.setText(transaction.getType());

                        if (transaction.getType().equals("Top Up"))
                            transAmountTV.setTextColor (Color.GREEN);
                        else
                            transAmountTV.setTextColor (Color.RED);

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();


                    }
                });

        transactionRecyclerView.setAdapter(adapter);
    }


}
