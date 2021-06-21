package com.example.singbike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.singbike.Authentication.SignInDialogFragment;
import com.example.singbike.Authentication.SignUpActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private static final String DEBUG_LOGIN = "DEBUG_LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final Button joinButton = findViewById (R.id.joinButton);
        final Button signInButton = findViewById (R.id.signInBtnAuth);
        final Button googleSignInButton = findViewById (R.id.googleSignInButton);

        joinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                    }
                }
        );

        googleSignInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                        builder.setTitle("Coming Soon :D")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                }
        );

        signInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        openLoginDialog();
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                AuthActivity.this,
                                R.style.BottomSheetDialogTheme
                        );

                        View bottomSheet = LayoutInflater.from(AuthActivity.this)
                                .inflate (R.layout.fragment_signin, (LinearLayout) findViewById (R.id.signInBottomSheet));

                        final EditText usernameET = bottomSheet.findViewById (R.id.usernameETSignIn);
                        final EditText passwordET = bottomSheet.findViewById (R.id.passwordETSignIn);
                        final Button signInButton = bottomSheet.findViewById (R.id.signInBtnSignIn);

                        signInButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (usernameET.getText().toString().equals("user") &&
                                                passwordET.getText().toString().equals("password")) {
                                            Log.d (DEBUG_LOGIN, "LOG IN SUCCESSFUL!");
                                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                            startActivity (intent);
                                        }
                                        Log.d (DEBUG_LOGIN, "LOG IN FAILED!");
                                    }
                                }
                        );

                        bottomSheetDialog.setContentView(bottomSheet);
                        bottomSheetDialog.show();
                    }
                }
        );

    }


    // open login dialog upon onClick event of ride Button
    private void openLoginDialog () {
        SignInDialogFragment signInBottomSheet = new SignInDialogFragment();
        signInBottomSheet.show(getSupportFragmentManager(), signInBottomSheet.getTag());
    }
}