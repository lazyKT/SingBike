package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.PaymentMethodRecyclerAdapter;
import com.example.singbike.Adapters.QuickTopUpRVAdapter;
import com.example.singbike.Models.PaymentMethod;
import com.example.singbike.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TopUpFragment extends Fragment {

    private Boolean showAddCardForm = false;
    private String TAG, CARD_TAG;
    private static final String DEBUG_BS_HEIGHT = "DEBUG_SHEET_HEIGHT";
    private ArrayList<PaymentMethod> savedCards;
    private SharedPreferences sharedPreferences;

    public TopUpFragment () {
        super(R.layout.top_up_bottomsheet);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        TAG = context.getString(R.string.authState);
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
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

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
        final EditText expiryCard = v.findViewById (R.id.expiryDateCardET);
        final EditText cvcCard = v.findViewById (R.id.cvcCardET);
        final Button addCardButton = v.findViewById (R.id.addNewCreditCardButton);

        /* Payment Methods */
        paymentMethodRV = v.findViewById (R.id.cardsRecyclerView);

        LinearLayoutManager paymethodLayoutManager = new LinearLayoutManager (requireActivity());
        paymentMethodRV.setLayoutManager (paymethodLayoutManager);

        for (PaymentMethod p : savedCards) {
            System.out.printf ("%s %s %s", p.getName(), p.getCardNum(), p.getExpiryDate());
        }

        final PaymentMethodRecyclerAdapter paymentMethodRecyclerAdapter = new PaymentMethodRecyclerAdapter(savedCards);
        paymentMethodRV.setAdapter (paymentMethodRecyclerAdapter);

        /* show add credit card form */
        final LinearLayout addNewCardLayout = v.findViewById (R.id.addNewCardLayoutButton);
        addNewCardLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
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
                }
        );

        /* add new credit card to payment methods */
        addCardButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {

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
                new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {

                        if (paymentMethodRecyclerAdapter.getItemCount() < 1) {
                            Toast.makeText (requireActivity(), "Please Choose Payment Method!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (topUpAmountEditText.getText().toString().equals("")) {
                            Toast.makeText (requireActivity(), "Please Enter Top up amount!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        /* pass the topup amount back to the wallet fragment */
                        Bundle topUpBundle = new Bundle();
                        topUpBundle.putInt ("amountAdded", Integer.parseInt(topUpAmountEditText.getText().toString()));
                        getParentFragmentManager().setFragmentResult ("TopUp", topUpBundle);

                        backToWalletPage();
                    }
                }
        );

        /* cancel Top Up Process and goes back to wallet page */
        cancelTopUpButton.setOnClickListener (
            new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    backToWalletPage();
                }
            }
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

    private void saveCreditCard (ArrayList<PaymentMethod> paymentMethods) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(paymentMethods);
        editor.putString(CARD_TAG, jsonString);
        editor.apply();
    }
}
