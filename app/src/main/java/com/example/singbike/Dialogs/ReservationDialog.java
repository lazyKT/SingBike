package com.example.singbike.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.singbike.CreateReservationActivity;
import com.example.singbike.R;

public class ReservationDialog extends DialogFragment {

    /* event listener. Event action on Dialog Dismiss */
    public interface ReservationDialogListener {
        /* after confirming the reservation */
        void onDialogPositiveClick (DialogFragment dialogFragment);
        /* after dismiss / cancel the reservation */
        void onDialogNegativeClick (DialogFragment dialogFragment);
    }

    // instance of Event Listener
    private ReservationDialogListener listener;
    private static final String DEBUG_BIKEID = "DEBUG_BIKEID";
    private int userID;

    /*
    * this method return the new instance of the ReservationDialog.
    * The purpose of this method is to receive the data from host activity/fragment
    * */
    public static ReservationDialog newInstance (String bikeID, int userID) {
        ReservationDialog dialog = new ReservationDialog();
        Bundle bundle = new Bundle();
        bundle.putString ("bikeID", bikeID);
        bundle.putInt ("userID", userID);
        dialog.setArguments (bundle);
        return dialog;
    }


    @Override
    public void onAttach (@NonNull Context context) {
        super.onAttach(context);
        /* any host activity or fragment which implements ReservationDialog, must also implement ReservationDialogListener */
        try {
            // instantiate the listener so that we can send events back to the host
            listener = (ReservationDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ReservationDialogListener!");
        }
    }


    @Override
    @NonNull
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (getArguments() == null)
            this.dismiss();

        String bikeID;

        if (getArguments().getString ("bikeID") == null)
            bikeID = "Unknown Status";
        else
            bikeID = getArguments().getString ("bikeID");

        userID = getArguments().getInt ("userID");


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.RoundCornerDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate (R.layout.dialog_reservation, null);
        builder.setView(view);

        final Button cancelButton = view.findViewById (R.id.cancel_button_Reservation);
        final Button confirmButton = view.findViewById (R.id.confirm_button_Reservation);
        final TextView bikeIDTextView = view.findViewById (R.id.bikeID_Reservation);
        bikeIDTextView.setText (bikeID);

        cancelButton.setOnClickListener(
                v -> {
                    listener.onDialogNegativeClick(ReservationDialog.this);
                    dismiss();
                }
        );

        confirmButton.setOnClickListener(
                v -> {
                    // make a reservation
                    Intent reserveIntent = new Intent (requireActivity(), CreateReservationActivity.class);
                    reserveIntent.putExtra ("bikeID", bikeID);
                    reserveIntent.putExtra ("userID", userID);
                    startActivity (reserveIntent);
                    dismiss();
                }
        );

        return builder.create();
    }

}
