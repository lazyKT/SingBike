package com.example.singbike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String DEBUG_SHAREDPREFERENCES = "DEBUG_SHAREPREF";
    private static final String DEBUG_APP_FLOW = "DEBUG_APP_FLOW";

    @Override
    public void onCreate (Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        String prefName = getResources().getString(R.string.authState);
        String isSignedInKey = getResources().getString(R.string.isSignedin);
        String userIDKey = getResources().getString(R.string.userID);

        SharedPreferences preferences = getSharedPreferences(prefName, MODE_PRIVATE);

        boolean isSignedIn = preferences.getBoolean(isSignedInKey, false);
        int userID = preferences.getInt(userIDKey, -1);
        Log.d (DEBUG_SHAREDPREFERENCES, "User signed in " + isSignedIn + " user id: " + userID);
        if (isSignedIn)
        {
            // user already signed in
            // go to home screen
            Intent intent = new Intent (this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
        else {
            // user has not signed in yet
            // go to sign in screen
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
    }

}
