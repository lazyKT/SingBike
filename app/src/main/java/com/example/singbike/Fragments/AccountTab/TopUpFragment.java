package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.PaymentMethodRecyclerAdapter;
import com.example.singbike.Adapters.QuickTopUpRVAdapter;
import com.example.singbike.AuthActivity;
import com.example.singbike.Dialogs.CardExpiryPickerDialog;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.PaymentMethod;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.TopUpRequest;
import com.example.singbike.Networking.Requests.TransactionRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TopUpFragment extends Fragment {

    private Boolean showAddCardForm = false;
    private String CARD_TAG;
    private User user;
    private static final String DEBUG_BS_HEIGHT = "DEBUG_SHEET_HEIGHT";
    private ArrayList<PaymentMethod> savedCards;
    private SharedPreferences sharedPreferences;
    private EditText expiryCard;

    public TopUpFragment () {
        super(R.layout.top_up_bottomsheet);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        String TAG = context.getString(R.string.authState);
        CARD_TAG = context.getString(R.string.savedCards);
        savedCards = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = sharedPreferences.getString(CARD_TAG, "");
        if (jsonString!= null && !jsonString.equals("")) {
            Type type = new TypeToken<ArrayList<PaymentMethod>>() {}.getType();
            savedCards = gson.fromJson(jsonString, type);
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition(R.transition.slide_top));

        getParentFragmentManager().setFragmentResultListener ("card_expiry", this, (requestKey, bundle) -> {
            expiryCard.setText (bundle.getString ("expiry_card"));
        });
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        SharedPreferences preferences = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = preferences.getString ("UserDetails", "");

        if (jsonString != null) {
            if (!jsonString.equals("")) {
                user = gson.fromJson (jsonString, User.class);
            }
            else {
                // log out
                logOut();
            }
        }
        else {
            // log out
            logOut();
        }

        final LinearLayout newCardForm = v.findViewById (R.id.addNewCardForm);
        /* components of add new card button */
        final TextView addLabel = v.findViewById (R.id.addNewCardTitle);
        final ImageView addIcon = v.findViewById (R.id.addNewCardIcon);

        /* main components inside TopUpBottomSheet */
        final RecyclerView paymentMethodRV, quickTopUpRV;
        final EditText topUpAmountEditText = v.findViewById (R.id.topUpAmountET);
        final Button topUpButton = v.findViewById (R.id.topUpButtonTopUp);
        final Button cancelTopUpButton = v.findViewById (R.id.cancelTopUpButtonTopUp);

        /*  hide add new card form */
        newCardForm.setVisibility(View.GONE);

        int maxHeight = Resources.getSystem().getDisplayMetrics().heightPixels - 50; // screen height - 50

        Log.d (DEBUG_BS_HEIGHT, "Layout Height " + maxHeight);

        /* components inside add card form */
        final EditText nameOnCard = v.findViewById (R.id.nameOnCardET);
        final EditText cardNumber = v.findViewById (R.id.cardNumberET);
        expiryCard = v.findViewById (R.id.expiryDateCardET);
        final EditText cvcCard = v.findViewById (R.id.cvcCardET);
        final Button addCardButton = v.findViewById (R.id.addNewCreditCardButton);

        /* Payment Methods */
        paymentMethodRV = v.findViewById (R.id.cardsRecyclerView);

        LinearLayoutManager paymentMethodLayoutManager = new LinearLayoutManager (requireActivity());
        paymentMethodRV.setLayoutManager (paymentMethodLayoutManager);

        expiryCard.setInputType (InputType.TYPE_NULL);
        expiryCard.setOnClickListener (v1 -> {
            // show expiry date picker
            CardExpiryPickerDialog dialog = new CardExpiryPickerDialog (requireActivity());
            dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
        });

        expiryCard.setOnFocusChangeListener ((v1, hasFocus) -> {
            if (hasFocus) {
                // show expiry date picker
                CardExpiryPickerDialog dialog = new CardExpiryPickerDialog (requireActivity());
                dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
            }
        });

        for (PaymentMethod p : savedCards) {
            System.out.printf ("%s %s %s", p.getName(), p.getCardNum(), p.getExpiryDate());
        }

        final PaymentMethodRecyclerAdapter paymentMethodRecyclerAdapter = new PaymentMethodRecyclerAdapter(savedCards);
        paymentMethodRV.setAdapter (paymentMethodRecyclerAdapter);

        /* show add credit card form */
        final LinearLayout addNewCardLayout = v.findViewById (R.id.addNewCardLayoutButton);
        addNewCardLayout.setOnClickListener(
                v1 -> {
                    if (showAddCardForm) {
                        /* hide add form */
                        addIcon.setImageResource (R.drawable.addnewcard);
                        addLabel.setText(R.string.addNewCard);
                        newCardForm.setVisibility(View.GONE);
                        showAddCardForm = false;
                    }
                    else {
                        /* show add form */
                        addIcon.setImageResource (R.drawable.delete);
                        addLabel.setText(R.string.cancel);
                        newCardForm.setVisibility(View.VISIBLE);
                        showAddCardForm = true;
                    }
                }
        );

        /* add new credit card to payment methods */
        addCardButton.setOnClickListener(
                view -> {

                    if (paymentMethodRecyclerAdapter.getItemCount() >= 3) {
                        Toast.makeText(requireActivity(), "You cannot add more than 3 credit cards.", Toast.LENGTH_LONG).show();
                    }

                    PaymentMethod newCard = new PaymentMethod();
                        newCard.setCardNum(cardNumber.getText().toString());
                        newCard.setCvc(cvcCard.getText().toString());
                        newCard.setExpiryDate(cvcCard.getText().toString());
                        newCard.setMethod("CreditCard");
                        newCard.setName(nameOnCard.getText().toString());
                        newCard.setExpiryDate (expiryCard.getText().toString());
                        newCard.setType("Visa");

                    paymentMethodRecyclerAdapter.addNewCard (newCard);
                    paymentMethodRecyclerAdapter.notifyDataSetChanged();

                    saveCreditCard (paymentMethodRecyclerAdapter.getPaymentMethods());

                    newCardForm.setVisibility(View.GONE);
                    addIcon.setImageResource (R.drawable.addnewcard);
                    addLabel.setText(R.string.addNewCard);
                }
        );

        /* Quick Top UP Options */
        quickTopUpRV = v.findViewById (R.id.quickTopUpRecyclerView);
        LinearLayoutManager quickTopUpLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        quickTopUpRV.setLayoutManager(quickTopUpLayoutManager);
        final QuickTopUpRVAdapter quickTopUpRVAdapter = new QuickTopUpRVAdapter(requireActivity(),
                new QuickTopUpRVAdapter.TopUpOnClickListener() {
                    @Override
                    public void onClickTopUpListener(String amount) {
                        topUpAmountEditText.setText(amount);
                    }
                });
        quickTopUpRV.setAdapter (quickTopUpRVAdapter);

        /* onClick event on topUp Button */
        topUpButton.setOnClickListener (
                v13 -> {

                    if (paymentMethodRecyclerAdapter.getItemCount() < 1) {
                        Toast.makeText (requireActivity(), "Please Choose Payment Method!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (topUpAmountEditText.getText().toString().equals("")) {
                        Toast.makeText (requireActivity(), "Please Enter Top up amount!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    handleTopUpRequest(Double.parseDouble(topUpAmountEditText.getText().toString()));

                    backToWalletPage();
                }
        );

        /* cancel Top Up Process and goes back to wallet page */
        cancelTopUpButton.setOnClickListener (
                v12 -> backToWalletPage()
        );
    }

    /* go back to wallet page */
    private void backToWalletPage () {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack ("wallet")
                .setReorderingAllowed(true)
                .replace (R.id.fragmentContainerView, WalletFragment.class, null)
                .commit();
    }

    /* save credit card details in local storage */
    private void saveCreditCard (ArrayList<PaymentMethod> paymentMethods) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(paymentMethods);
        editor.putString(CARD_TAG, jsonString);
        editor.apply();
    }

    /* Top Up Request */
    private void handleTopUpRequest (double amount) {
        if (user == null)
            return;

        final String url = String.format (Locale.getDefault(), "customers/%d", user.getID());
        final Retrofit retrofit = RetrofitClient.getRetrofit();
        final RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.topUpBalance(url, new TopUpRequest(amount));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() == null) {
                    displayErrorDialog ("TOP-UP Response Body is Empty!");
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject (response.body().string());
                        Log.d ("DEBUG_TOP_UP", response.body().string());
                        JSONObject userJSONObject = jsonObject.getJSONObject ("customer");
                        user.setBalance (userJSONObject.getDouble("balance"));
                        updateSharedPrefs();
                    } catch (JSONException | IOException e) {
                        displayErrorDialog (e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog (t.getMessage());
            }
        });

        /* record transaction */
        Call<ResponseBody> call1 = services.createTransaction(new TransactionRequest(user.getID(), amount, "Top Up"));
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        AppExecutor.getInstance().getDiskIO().execute (() -> {
                            /* add to User Activity (Local Database) */
                            Utils.insertUserActivity (requireActivity(), "top-up", user.getID());
                        });
                    }
                    else {
                        displayErrorDialog ("POST Transaction Response Body is Empty!");
                    }
                }
                else {
                    displayErrorDialog ("POST Transaction Response is not successful!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog (t.getMessage());
            }
        });
    }

    private void displayErrorDialog (String message) {
        final String NETWORK_ERROR = "NETWORK_ERROR";

        ErrorDialog dialog = new ErrorDialog (requireActivity(), NETWORK_ERROR, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

    private void updateSharedPrefs () {
        Log.d ("DEBUG_UPDATE_USER", "Updating User SharedPreferences!!!!");
        SharedPreferences preferences = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String jsonString = gson.toJson (user);
        editor.putString ("UserDetails", jsonString);
        editor.apply();
    }

    private void logOut () {
        SharedPreferences preferences = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        startActivity (new Intent(requireActivity(), AuthActivity.class));
    }
}
