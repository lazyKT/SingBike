package com.example.singbike.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.R;

public class ErrorThenLogOutDialog extends DialogFragment {

    public interface ErrorDialogListener {
        public void onDismissDialog (DialogFragment dialogFragment);
    }

    private ErrorDialogListener listener;
    private final String title, message;
    private final Context context;

    public ErrorThenLogOutDialog (Context context, String tittle, String message) {
        this.context = context;
        this.title = tittle;
        this.message = message;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ErrorDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException (context.toString() + " must implement ErrorDialogListener Interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceStates) {

        AlertDialog.Builder builder = new AlertDialog.Builder (this.context);
        builder.setTitle (this.title);
        builder.setMessage (this.message);

        builder.setNeutralButton (R.string.ok, (dialogInterface, which ) -> {
            this.listener.onDismissDialog (ErrorThenLogOutDialog.this);
            dialogInterface.dismiss();
        });

        return builder.create();
    }

}
