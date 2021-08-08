package com.example.singbike.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.R;

public class LoadingDialog extends DialogFragment {

    private final Context context;
    private final String loadingTxt;

    public LoadingDialog (Context context, String loadingTxt) {
        this.context = context;
        this.loadingTxt = loadingTxt;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceState) {
        super.onCreateDialog (savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder (this.context);
        View v = LayoutInflater.from (this.context)
                .inflate (R.layout.loading, null);
        builder.setView (v);
        final TextView loadingTxtView = v.findViewById (R.id.loadingText);
        loadingTxtView.setText (this.loadingTxt);

        return builder.create();
    }

}
