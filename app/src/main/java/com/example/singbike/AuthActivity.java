package com.example.singbike;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.singbike.Authentication.SignInDialogFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

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