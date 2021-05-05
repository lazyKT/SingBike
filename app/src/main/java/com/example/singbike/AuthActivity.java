package com.example.singbike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.singbike.Authentication.SignInDialogFragment;

import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private static final String DEBUG_SHAREDPREFERENCES = "DEBUG_SHAREPREF";
    private static final String DEBUG_APP_FLOW = "DEBUG_APP_FLOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.authState), Context.MODE_PRIVATE);

        /* START APP LOADING */

        // retrieve saved key-values in device storage to determine whether user has already logged in.
        boolean isLogIn = sharedPreferences.getBoolean(getString(R.string.isSignedin), false);
        int userID = sharedPreferences.getInt(getString(R.string.userID), -1);

        Log.d (DEBUG_APP_FLOW, "AuthActivity Loaded!");
        System.out.println ("isLogIn: " + isLogIn + ", userID: " + userID);
        for (Map.Entry<String, ?> sp: sharedPreferences.getAll().entrySet()) {
            System.out.println("### DEBUG - " + sp.getKey() + ": " + sp.getValue());
        }

        // if the user already logged in, skip this page and redirect to the application home page
        if (isLogIn) {
            Log.d (DEBUG_SHAREDPREFERENCES, "User already signed in. USER ID : " + userID);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra ("userID", userID);
            startActivity (intent);
        }

        /* STOP APP LOADING */

        // ride button instantiation
        Button rideButton = (Button) findViewById (R.id.rideButton);

        /* onClick action on ride button */
        rideButton.setOnClickListener (
                new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        // open login dialog
                        openLoginDialog();
                    }
                }
        );


    }


    // open login dialog upon onClick event of ride Button
    private void openLoginDialog () {
        SignInDialogFragment signInDialogFragment = new SignInDialogFragment();
        signInDialogFragment.show(getSupportFragmentManager(), "signin");
    }
}