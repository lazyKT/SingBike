/*
 * Sign Up Page
 * The user will able to sign up (or) register into SingBike via this page.
 * This page provides sign up credentials textFields, Sign Up Button and
 * Sign In button which will redirect the user back the sign in page.
 */

package com.example.singbike.Authentication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import
 com.example.singbike.MainActivity;
import com.example.singbike.Models.User;
import com.example.singbike.R;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String DEBUG_SIGNUP = "DEBUG_SIGN_UP";

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
                            Log.d (DEBUG_SIGNUP, "Credentials Valid!");

                            /*
                             * Upon receiving valid signup credentials, post those to the server,
                             * Currently, the server is not available yet.
                             * Later, will implement a communication with server here.
                             * Upon Successful Post Request to Server, the app will receive back the user details
                             * from the server which will be later created as a User object and be passed
                             * to MainActivity.
                             * That details will also be saved in the device memory (exclude password). so that
                             * the users do not need to sign in everytime when they open the app.
                             * These details will be deleted only when the app is un-installed or the user signs out.
                             */

                            User user = new User();
                            user.setEmail(emailET.getText().toString());
                            user.setUsername(unameET.getText().toString());

                            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                            intent.putExtra ("user", user);
                            startActivity(intent);
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

        /* to validate email address format */
        Pattern email_regex = Pattern.compile ("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        /* to validate password complexity */
        Pattern pwd_regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

        switch (type) {
            case "uname":
                if (val.length() < 4)
                    return 0;
                return 1;
            case "email":
                if (email_regex.matcher(val).find())
                    return 1;
                return 0;
            case "pwd":
                if (val.length() < 8)
                    return -1;
                if (pwd_regex.matcher(val).find())
                    return 1;
                return 0;
        }

        return 0;
    }


}
