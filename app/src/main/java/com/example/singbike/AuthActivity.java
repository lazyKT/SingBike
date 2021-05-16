package com.example.singbike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.singbike.Authentication.SignInDialogFragment;
import com.example.singbike.Authentication.SignUpActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final Button joinButton = findViewById (R.id.joinButton);
        final Button signInButton = findViewById (R.id.signInBtnAuth);


        joinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
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