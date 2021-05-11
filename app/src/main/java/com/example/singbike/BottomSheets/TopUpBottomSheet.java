package com.example.singbike.BottomSheets;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.PaymentMethodRecyclerAdapter;
import com.example.singbike.Adapters.QuickTopUpRVAdapter;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


/* Top Up Wallet BottomSheet Fragment */
public class TopUpBottomSheet extends BottomSheetDialogFragment {

    private Boolean showAddCardForm = false;
    private static final String DEBUG_BS_HEIGHT = "DEBUG_SHEET_HEIGHT";

    public TopUpBottomSheet (){}

    @Override
    public void onCreate (@Nullable Bundle savedInstanceStates) {
        super.onCreate (savedInstanceStates);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate (R.layout.top_up_bottomsheet, container, false);

        final LinearLayout newCardForm = v.findViewById (R.id.addNewCardForm);
        /* components of add new card button */
        final TextView addLabel = v.findViewById (R.id.addNewCardTitle);
        final ImageView addIcon = v.findViewById (R.id.addNewCardIcon);

        /* main components inside TopUpBottomSheet */
        final View bottomSheet = v.findViewById (R.id.topUpBottomSheet);
//        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        final RecyclerView paymentMethodRV, quickTopUpRV;
        final EditText topUpAmountEditText = v.findViewById (R.id.topUpAmountET);


        /*  hide add new card form */
        newCardForm.setVisibility(View.GONE);

        int maxHeight = Resources.getSystem().getDisplayMetrics().heightPixels - 50; // screen height - 50

//        bottomSheetBehavior.setPeekHeight(maxHeight);
        Log.d (DEBUG_BS_HEIGHT, "Layout Height " + maxHeight);

        /* components inside add card form */

        /* Payment Methods */
//        paymentMethodRV = v.findViewById (R.id.cardsRecyclerView);
//
//        LinearLayoutManager paymethodLayoutManager = new LinearLayoutManager (requireActivity());
//        paymentMethodRV.setLayoutManager (paymethodLayoutManager);
//
//        PaymentMethodRecyclerAdapter paymentMethodRecyclerAdapter = new PaymentMethodRecyclerAdapter();
//        paymentMethodRV.setAdapter (paymentMethodRecyclerAdapter);

        /* add new payment method/card */
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

        /* Quick Top UP Options */
        quickTopUpRV = v.findViewById (R.id.quickTopUpRecyclerView);
        LinearLayoutManager quickTopUpLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        quickTopUpRV.setLayoutManager(quickTopUpLayoutManager);
        QuickTopUpRVAdapter quickTopUpRVAdapter = new QuickTopUpRVAdapter(requireActivity(),
                new QuickTopUpRVAdapter.TopUpOnClickListener() {
                    @Override
                    public void onClickTopUpListener(String amount) {
                        topUpAmountEditText.setText(amount);
                    }
                });
        quickTopUpRV.setAdapter (quickTopUpRVAdapter);

        return v;
    }

    /* show/hide AddNewCardForm on click action */
    private void toggleAddNewCardForm (View v, LinearLayout addForm) {



    }

    private void init_view (Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
//        context.get
    }

}
