package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.TransactionRecyclerAdapter;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.Transaction;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WalletFragment extends Fragment {

    private static final String DEBUG_TRANSACTIONS = "DEBUG_TRANSACTIONS";

    private ArrayList<Transaction> transactionList;
    private ProgressBar transactionLoadingBar;
    private RecyclerView transactionRecyclerView;
    private TransactionRecyclerAdapter adapter;

    private int amountAdded = 0;

    private TextView balanceTextView;

    public WalletFragment () {
        super (R.layout.fragment_wallet);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* fragment transition animation */
        TransitionInflater inflater = TransitionInflater.from(requireActivity());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

        /* receive data from TopUp Fragment */
        getParentFragmentManager().setFragmentResultListener ("TopUp", this,
                (requestKey, bundle) -> amountAdded = bundle.getInt ("amountAdded"));

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

        transactionLoadingBar = view.findViewById (R.id.loadingProgBar_Transactions);
        transactionRecyclerView = view.findViewById (R.id.transactionRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        transactionRecyclerView.setLayoutManager(layoutManager);

        adapter = new TransactionRecyclerAdapter(transactionList, requireActivity(),
                transaction -> {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog (
                            requireActivity(),
                            R.style.BottomSheetDialogTheme
                    );

                    /* display bottom sheet for transaction info */
                    View bottomSheetView = LayoutInflater.from (requireActivity())
                            .inflate(R.layout.transaction_sheet, view.findViewById(R.id.transactionBottomSheet));
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


                });

        transactionRecyclerView.setAdapter(adapter);

        /* fetch User Transactions */
        AppExecutor.getInstance().getDiskIO().execute (
                () -> {
                    Log.d (DEBUG_TRANSACTIONS, "Fetching User SharedPreferences");
                    Gson gson = new Gson();
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
                    String jsonString = sharedPreferences.getString ("UserDetails", "");
                    User user1 = null;
                    if (jsonString != null && !jsonString.equals("")) {
                        user1 = gson.fromJson (jsonString, User.class);
                        Log.d (DEBUG_TRANSACTIONS, user1.toString());
                    }

                    if (user1 != null) {
                        fetchTransactions (user1.getID());
                    }
                    else {
                        requireActivity().runOnUiThread(
                                () -> displayError(getString(R.string.runtime_error), "Failed to Retrieve User Details!")
                        );
                    }
                }
        );

        balanceTextView = view.findViewById (R.id.balanceTV);
        final Button topUpButton = view.findViewById (R.id.topUpBtn);

        /* Top Up Wallet */
        topUpButton.setOnClickListener (
                v -> requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace (R.id.fragmentContainerView, TopUpFragment.class, null)
                        .addToBackStack ("TopUp")
                        .setReorderingAllowed (true)
                        .commit()
        );
        amountAdded = amountAdded + 5;
        balanceTextView.setText (String.valueOf(amountAdded));
    }


    /* fetch all User transaction */
    private void fetchTransactions (int userID) {
        Log.d (DEBUG_TRANSACTIONS, "Fetching Transactions");
        final String transactionURL = String.format(Locale.getDefault(), "customers/customer_transactions/%d", userID);
        final String NETWORK_ERROR = "NETWORK_ERROR";
        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.fetchTransactions (transactionURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d (DEBUG_TRANSACTIONS, "Response Success!");
                    if (response.body() != null) {
                        Log.d (DEBUG_TRANSACTIONS, response.body().toString());
                        try {
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            String transactionsStr = jsonObject.getString ("transactions");
                            Gson gson = new Gson();
                            Transaction[] transactionArr = gson.fromJson (transactionsStr, Transaction[].class);

                            requireActivity().runOnUiThread ( () -> {
                                transactionList = new ArrayList<>(Arrays.asList (transactionArr));
                                for (Transaction t : transactionArr)
                                    Log.d (DEBUG_TRANSACTIONS, t.toString());
                                transactionLoadingBar.setVisibility (View.GONE);
                                transactionRecyclerView.setVisibility (View.VISIBLE);
                                adapter.setTransactions (transactionList);
                                adapter.notifyDataSetChanged();
                                Log.d (DEBUG_TRANSACTIONS, "Num of Transactions : " + adapter.getItemCount());
                            });

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            requireActivity().runOnUiThread (() -> displayError(NETWORK_ERROR, e.getMessage()));
                        }
                    }
                }
                else {
                    Log.d(DEBUG_TRANSACTIONS, "Response Failed!");
                    requireActivity().runOnUiThread (() -> displayError(NETWORK_ERROR, "Response Failed!"));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d (DEBUG_TRANSACTIONS, t.toString());
                requireActivity().runOnUiThread (() -> displayError(NETWORK_ERROR, t.getMessage()));
            }
        });
    }

    private void displayError (String title, String message) {
        ErrorDialog errorDialog = new ErrorDialog(requireActivity(), title, message);
        errorDialog.show (requireActivity().getSupportFragmentManager(), errorDialog.getTag());
    }

}
