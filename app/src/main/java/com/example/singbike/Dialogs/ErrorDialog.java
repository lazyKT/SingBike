package com.example.singbike.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.R;

public class ErrorDialog extends DialogFragment {

    private final String errorTxt, title;
    private final Context context;

    public ErrorDialog (Context context, String title, String errorTxt) {
        this.errorTxt = errorTxt;
        this.context = context;
        this.title = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View v = LayoutInflater.from (context)
                .inflate (R.layout.dialog_error, null);

        builder.setView (v);
        final TextView errorTitle = v.findViewById (R.id.errorTitle);
        final TextView errorTV =  v.findViewById (R.id.errorText);
        final Button dismissButton = v.findViewById (R.id.dismissButton_ErrorDialog);

        errorTitle.setText (title);
        errorTV.setText (errorTxt);

        AlertDialog dialog = builder.create();

        dismissButton.setOnClickListener(v1 -> dialog.dismiss());

        return dialog;
    }
}
