/*
 * Sign Up Page
 * The user will able to sign up (or) register into SingBike via this page.
 * This page provides sign up credentials textFields, Sign Up Button and
 * Sign In button which will redirect the user back the sign in page.
 */

package com.example.singbike.Authentication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singbike.MainActivity;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.NetworkRequests.UserRequest;
import com.example.singbike.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    private static final String DEBUG_SIGNUP = "DEBUG_SIGN_UP";
    private static final String DEBUG_REGISTER = "DEBUG_REGISTER_REQUEST";

    private TextView errorTV;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText unameET = findViewById (R.id.usernameET_SignUp);
        final EditText emailET = findViewById (R.id.emailET_SignUp);
        final EditText pwdET = findViewById (R.id.passwordET_SignUp);

        errorTV = findViewById (R.id.errorTV_SignUp);
        errorTV.setVisibility(View.GONE);

        final Button signUpBtn = findViewById (R.id.signUpButton_SignUp);
        final Button signInBtn = findViewById (R.id.signInBtn_SingUp);

        /* Back to Sign In Page */
        signInBtn.setOnClickListener (
                v -> {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
        );

        /* onClick action on Sign Up Button */
        signUpBtn.setOnClickListener (
                v -> {
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
                        UserRequest newUser = new UserRequest (
                                unameET.getText().toString(),
                                emailET.getText().toString(),
                                pwdET.getText().toString()
                        );
                        handleSignUp(newUser);
                    }
                }
        );

    }


    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onPause () {
        super.onPause();

    }

    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onStop () {
        super.onStop();

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


    /* Handle SignUp Request with Retrofit2 */
    private void handleSignUp (UserRequest user) {

        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.signUpRequest (user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d (DEBUG_REGISTER, response.body().toString());
                        try {
                            /* convert response to JSONObject*/
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            JSONObject userJsonObject = jsonObject.getJSONObject("customer");

                            /* set new user */
                            User user = new User();
                            user.setID (userJsonObject.getInt("id"));
                            user.setUsername (userJsonObject.getString("username"));
                            user.setEmail (userJsonObject.getString("email"));
                            user.setBalance (userJsonObject.getDouble("balance"));
                            user.setCredits (userJsonObject.getInt("credits"));
                            user.setCreated_at (userJsonObject.getString("created_at"));
                            user.setUpdated_at (userJsonObject.getString("updated_at"));

                            /* save user data in Local Storage (SharedPreferences) */
                            saveToSharedPref (user);

                            /* redirect to Home Page */
                            startActivity (new Intent(SignUpActivity.this, MainActivity.class));

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.d (DEBUG_REGISTER, "Response has empty body!!");
                        errorTV.setText (response.message());
                    }
                }
                else {
                    Log.d (DEBUG_REGISTER, "Response has failed!");
                    errorTV.setText (R.string.sign_up_fail);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d (DEBUG_REGISTER, "throwable : " + t.toString());
                errorTV.setText (t.getMessage());
            }
        });
    }

    /* save user data in SharedPreferences */
    private void saveToSharedPref (User user) {
        SharedPreferences userPrefs = this.getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson (user);
        editor.putString ("UserDetails", json);
        editor.apply();
    }
}
