/*
 * Login Dialog Fragment
 * Users will be able to sign in to application  via this dialog.
 * This dialog will provide credential textfields (Username, Password), SignIn Button and SignUp Button.
 * If the user has not registered yet, he/she can signup via SignUp Button
 * */
package com.example.singbike.Authentication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.singbike.R;

public class SignInDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        // build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // create layout inflater instance to load custom layout into dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // load a custom layout
        View view = inflater.inflate(R.layout.fragment_signin, null);

        // instantiate components inside sign in layout
        final Button signInBtn = view.findViewById (R.id.signInBtn_signin);
        final Button signUpBtn = view.findViewById (R.id.signUpBtn_signin);
        final EditText unameET = view.findViewById (R.id.unameET_signin);
        final EditText pwdET = view.findViewById (R.id.pwdET_signin);

        builder.setView(view);

        // onClick action on Sing Up Button
        signUpBtn.setOnClickListener (
                new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        // redirect to Sign Up Page
                        Intent intent = new Intent(getContext(), SignUpActivity.class);
                        startActivity(intent);
                    }
                }
        );

        return builder.create();
    }
}
