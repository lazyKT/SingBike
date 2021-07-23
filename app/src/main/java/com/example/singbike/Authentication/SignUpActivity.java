/*
 * Sign Up Page
 * The user will able to sign up (or) register into SingBike via this page.
 * This page provides sign up credentials textFields, Sign Up Button and
 * Sign In button which will redirect the user back the sign in page.
 */

package com.example.singbike.Authentication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import
 com.example.singbike.MainActivity;
import com.example.singbike.Models.User;
import com.example.singbike.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String DEBUG_SIGNUP = "DEBUG_SIGN_UP";
    private static final String DEBUG_SP = "DEBUG_WRITE_SP";
    private static final String DEBUG_REGISTER = "DEBUG_REGISTER_REQUEST";

    private RequestQueue requestQueue; // network request for Sign In

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText unameET = findViewById (R.id.usernameET_SignUp);
        final EditText emailET = findViewById (R.id.emailET_SignUp);
        final EditText pwdET = findViewById (R.id.passwordET_SignUp);

        final TextView errorTV = findViewById (R.id.errorTV_SignUp);
        errorTV.setVisibility(View.GONE);

        final Button signUpBtn = findViewById (R.id.signUpButton_SignUp);
        final Button signInBtn = findViewById (R.id.signInBtn_SingUp);

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

                        errorTV.setVisibility(View.VISIBLE);

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
                            requestQueue = Volley.newRequestQueue (SignUpActivity.this);
                            final String registerUrl = "http://10.0.2.2:8000/customers/";

                            // construct JSON object to for POST Request
                            JSONObject registerData = new JSONObject();
                            try {
                                registerData.put ("username", unameET.getText().toString());
                                registerData.put ("email", emailET.getText().toString());
                                registerData.put ("password", pwdET.getText().toString());
                            }
                            catch (JSONException je) {
                                je.printStackTrace();
                            }

                            JsonObjectRequest registerRequest = new JsonObjectRequest (Request.Method.POST, registerUrl, registerData,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse (JSONObject response) {
                                        // successfully register
                                        Log.d (DEBUG_REGISTER, response.toString());

                                        /*
                                         * save the user login state in the device memory
                                         * so that user do not need to login every time when he opens the app.
                                         */
                                        // initialise sharedPreferences
                                        Log.d (DEBUG_SP, "Writing to SharedPreferences ...");
                                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.authState), Context.MODE_PRIVATE);
                                        // initialise sharePreferences editor to write new key-value pairs
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(getString(R.string.isSignedin), true);
                                        editor.apply(); // update(write) values asynchronously

                                        User user = new User();
                                        user.setEmail(emailET.getText().toString());
                                        user.setUsername(unameET.getText().toString());

                                        Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                                        intent.putExtra ("user", user);
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse (VolleyError error) {
                                        // registration error
                                        error.printStackTrace();
                                        Log.d (DEBUG_REGISTER, error.toString());
                                    }
                            });

                            registerRequest.setTag ("RegisterRequest");
                            requestQueue.add (registerRequest);
                        }
                    }
                }
        );

    }


    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onPause () {
        super.onPause();
        if (requestQueue != null)
            requestQueue.cancelAll ("RegisterRequest");
    }

    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onStop () {
        super.onStop();
        if (requestQueue != null)
            requestQueue.cancelAll ("RegisterRequest");
    }


    /**
     * Validation of sign up process.
     * Username: *username must be at least 4 characters long
     * Email: *We will not ask for email verification, however, users must fill in valid email format. eg-example@domain.com
     * Password: *Password must be 8 characters long and must consist of at least 1 Capital Letter, 1 small letter and 1 number
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
