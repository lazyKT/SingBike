/*
 * Login BottomSheet Dialog Fragment
 * Users will be able to sign in to application  via this dialog.
 * This dialog will provide credential textfields (Username, Password), SignIn Button and SignUp Button.
 * If the user has not registered yet, he/she can signup via SignUp Button
 * */
package com.example.singbike.Authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SignInDialogFragment extends BottomSheetDialogFragment {

    public SignInDialogFragment() {}

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);

        View v = layoutInflater.inflate(R.layout.fragment_signin, viewGroup, false);

        final View bottomSheet = v.findViewById (R.id.signInBottomSheet);



        return v;
    }
}