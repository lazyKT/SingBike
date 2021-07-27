package com.example.singbike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class CreditScoreActivity extends AppCompatActivity {


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_credit);

        final ImageButton backButton = findViewById (R.id.backButton_CreditScore);

        backButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (CreditScoreActivity.this, EditProfileActivity.class);
                    startActivity (intent);
                }
            }
        );

    }

}
