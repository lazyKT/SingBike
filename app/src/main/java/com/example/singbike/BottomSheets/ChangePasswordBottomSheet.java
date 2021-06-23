package com.example.singbike.BottomSheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.singbike.Models.User;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private User user;
    private final Context context;
    private static final String CHANGE_PWD_TAG = "CHANGE_PWD_REQUEST";
    private RequestQueue requestQueue;
    private TextView errorTextView;

    public ChangePasswordBottomSheet (Context context, User user) {
        this.user = user;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate (R.layout.change_password, container, false);

        errorTextView = v.findViewById (R.id.errorTV_ChangePwd);
        final EditText currentPassword = v.findViewById (R.id.currentPwdEditText);
        final EditText newPassword = v.findViewById (R.id.newPwdEditText);
        final EditText confirmPassword = v.findViewById (R.id.confirmPwdEditText);
        final Button changePasswordButton = v.findViewById (R.id.changePwdButton_sheet);

        errorTextView.setVisibility (View.GONE);

        changePasswordButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentPassword.getText().toString().equals(""))
                        showErrorMessage (R.string.currentPasswordEmpty);
                    else if (newPassword.getText().toString().equals(""))
                        showErrorMessage (R.string.newPasswordEmpty);
                    else if (!confirmPassword.getText().toString().equals(newPassword.getText().toString()))
                        showErrorMessage (R.string.passwordNotMatch);
                    else {
                        errorTextView.setVisibility (View.GONE);
                        requestQueue = Volley.newRequestQueue (context);
                    }
                }
            }
        );

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (requestQueue != null)
            requestQueue.cancelAll (CHANGE_PWD_TAG);
    }

    private void showErrorMessage (int message) {
        errorTextView.setVisibility (View.VISIBLE);
        errorTextView.setText (message);
    }
}
