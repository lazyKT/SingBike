package com.example.singbike.BottomSheets;

import android.app.AlertDialog;
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
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.Requests.ChangePasswordRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private User user;
    private final Context context;
    private static final String CHANGE_PWD_TAG = "CHANGE_PWD_REQUEST";
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
                v1 -> {

                    if (currentPassword.getText().toString().equals(""))
                        showErrorMessage (R.string.currentPasswordEmpty);
                    else if (newPassword.getText().toString().equals(""))
                        showErrorMessage (R.string.newPasswordEmpty);
                    else if (!confirmPassword.getText().toString().equals(newPassword.getText().toString()))
                        showErrorMessage (R.string.passwordNotMatch);
                    else {
                        errorTextView.setVisibility (View.GONE);
                        handleChangePasswordRequest(currentPassword.getText().toString(), newPassword.getText().toString());
                    }
                }
        );

        return v;
    }

    private void showErrorMessage (int message) {
        errorTextView.setVisibility (View.VISIBLE);
        errorTextView.setText (message);
    }


    private void handleChangePasswordRequest (String oldPassword, String newPassword) {
        final String NETWORK_ERROR = "NETWORK_ERROR";
        final String url = String.format (Locale.getDefault(), "customers/change_password/%d", this.user.getID());

        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        ChangePasswordRequest requestBody = new ChangePasswordRequest (oldPassword, newPassword);

        Call<ResponseBody> call = services.changePassword (url, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // password has been changed successfully
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder (context);
                            builder.setMessage (response.body().string())
                                    .setNeutralButton (R.string.ok, (dialogInterface, which) -> {
                                        dialogInterface.dismiss();
                                    });
                            builder.create();
                            builder.show();
                        }
                        catch (IOException ie) {
                            displayErrorDialog ("APPLICATION ERROR", ie.getMessage());
                        }
                    }
                    else {
                        displayErrorDialog (NETWORK_ERROR, "Response Body Empty!");
                    }
                }
                else {
                    displayErrorDialog (NETWORK_ERROR, "Network Response Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog (NETWORK_ERROR, t.getMessage());
            }
        });
    }

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (this.context, title, message);
        dialog.show (requireActivity().getSupportFragmentManager(), dialog.getTag());
    }

}
