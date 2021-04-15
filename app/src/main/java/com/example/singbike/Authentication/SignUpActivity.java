/*
 * Sign Up Page
 * The user will able to sign up (or) register into SingBike via this page.
 * This page provides sign up credentials textFields, Sign Up Button and
 * Sign In button which will redirect the user back the sign in page.
 */

package com.example.singbike.Authentication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import
 com.example.singbike.MainActivity;
import com.example.singbike.Models.User;
import com.example.singbike.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText unameET = (EditText) findViewById (R.id.unameET_signup);
        final EditText emailET = (EditText) findViewById (R.id.emailET_signup);
        final EditText pwdET = (EditText) findViewById (R.id.pwdET_signup);
        final EditText confirmPwdET = (EditText) findViewById (R.id.confirmPwdET_signup);
        final TextView errorTV = (TextView) findViewById (R.id.errorTV_signup);

        final Button signUpBtn = (Button) findViewById (R.id.signUpBtn_signup);
        final Button signInBtn = (Button) findViewById (R.id.signInBtn_signup);

        /* Back to Sign In Page */
        signInBtn.setOnClickListener (
                new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
        );

        /* onClick action on Sign Up Button */
        signUpBtn.setOnClickListener (
                new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        // validate the credentials

                        /* username validation */
                        if ( validateCredentials (unameET.getText().toString(), "uname") < 1) {
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText(R.string.uname_err);
                        }
                        /* email address validation */
                        else if ( validateCredentials (emailET.getText().toString(), "email") < 1) {
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText(R.string.email_err);
                        }
                        /* password validation. short password */
                        else if ( validateCredentials (pwdET.getText().toString(), "pwd") < 0) {
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText(R.string.pwd_err_length);
                        }
                        /* password validation. long, weak password */
                        else if ( validateCredentials (pwdET.getText().toString(), "pwd") < 1) {
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText(R.string.pwd_err_chars);
                        }
                        /* confirm password validation. */
                        else if ( !pwdET.getText().toString().equals(confirmPwdET.getText().toString()) ) {
                            errorTV.setVisibility(View.VISIBLE);
                            errorTV.setText(R.string.confirm_pwd_err);
                        }
                        /* Validation Success */
                        else {
                            User user = new User();
                            user.setEmail(emailET.getText().toString());
                            user.setUsername(unameET.getText().toString());
                            user.setPassword(pwdET.getText().toString());
                        }
                    }
                }
        );

    }

    /**
     * Validation of sign up process.
     * Username: *username must be at least 4 characters long
     * Email: *We will not ask for email verification, however, users must fill in valid email format. eg-example@domain.com
     * Password: *Password must be 8 characters long and must consist of at least 1 Capital Letter, 1 small letter and 1
     * Confirm Password: *User must be able to confirm the passwords.
     * @param val Text value (credentials) obtained via EditText
     * @param type type of credentials
     * @return int.
     * 0: validation failed
     * 1: validation passed
     * -1: short password
     */
    private int validateCredentials (String val, String type) {

        switch (type) {
            case "uname":
                if (val.length() < 4)
                    return 0;
                return 1;
            case "email":
                return 0;
            case "pwd":
                if (val.length() < 8)
                    return -1;
                return 1;
        }

        return 0;
    }


}
