package com.example.singbike.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.R;

import java.util.Locale;


public class CardExpiryPickerDialog extends DialogFragment {

    private final Context context;

    public CardExpiryPickerDialog (Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        super.onCreateDialog (savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder (context);
        View v = LayoutInflater.from (context)
                .inflate (R.layout.card_expiry_dialog, null);
        builder.setView (v);

        final NumberPicker monthPicker = v.findViewById (R.id.monthPicker);
        final NumberPicker yearPicker = v.findViewById (R.id.yearPicker);

        monthPicker.setMinValue (1);
        monthPicker.setMaxValue (12);

        yearPicker.setMinValue (2021);
        yearPicker.setMaxValue (2041);

        builder.setNeutralButton (R.string.ok, (dialogInterface, which) -> {

            String month = monthPicker.getValue() < 10 ? '0' + String.valueOf(monthPicker.getValue()) : String.valueOf(monthPicker.getValue());
            String year = String.valueOf(yearPicker.getValue()).substring(2);

            Bundle bundle = new Bundle();
            bundle.putString ("expiry_card", String.format(Locale.getDefault(), "%s/%s", month, year));
            getParentFragmentManager().setFragmentResult ("card_expiry", bundle);
            dialogInterface.dismiss();
        });

        return builder.create();
    }
}