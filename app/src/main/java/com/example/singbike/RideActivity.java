package com.example.singbike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class RideActivity extends AppCompatActivity {

    @Override
    public void onCreate (Bundle savedInstanceStates) {
        super.onCreate (savedInstanceStates);
        setContentView (R.layout.activity_ride);

        ImageButton backButton = findViewById (R.id.backButton_Riding);

        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (RideActivity.this, MainActivity.class);
                    startActivity (intent);
                }
            }
        );

    }

}
