package com.example.singbike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.singbike.Authentication.SignUpActivity;
import com.example.singbike.Models.User;
import com.example.singbike.NetworkRequests.RetrofitClient;
import com.example.singbike.NetworkRequests.RetrofitServices;
import com.example.singbike.NetworkRequests.UserRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AuthActivity extends AppCompatActivity {

    private static final String DEBUG_LOGIN = "DEBUG_LOGIN";

    private TextView errorTV;

    @Override
    public void onStart () {
        super.onStart();

        SharedPreferences userPrefs = this.getSharedPreferences("User", MODE_PRIVATE);
        String jsonString = userPrefs.getString ("UserDetails", "");

        if (jsonString != null && !jsonString.equals("")) {
            Log.d (DEBUG_LOGIN, jsonString);
            Intent intent = new Intent (AuthActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final Button joinButton = findViewById (R.id.joinButton);
        final Button signInButton = findViewById (R.id.signInBtnAuth);
        final Button googleSignInButton = findViewById (R.id.googleSignInButton);

        joinButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                    }
                }
        );

        googleSignInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                        builder.setTitle("Coming Soon :D")
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create();
                        builder.show();
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

                        final EditText emailET = bottomSheet.findViewById (R.id.emailETSignIn);
                        final EditText passwordET = bottomSheet.findViewById (R.id.passwordETSignIn);
                        final Button signInButton = bottomSheet.findViewById (R.id.signInBtnSignIn);
                        errorTV = bottomSheet.findViewById (R.id.errorTV_SignIn);

                        signInButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        handleSignIn (emailET.getText().toString(), passwordET.getText().toString());
                                    }
                                }
                        );

                        bottomSheetDialog.setContentView(bottomSheet);
                        bottomSheetDialog.show();
                    }
                }
        );

    }

    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onStop () {
        super.onStop();

    }

    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onPause () {
        super.onPause();

    }

    /* make sign in request */
    private void handleSignIn (String email, String password) {
        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        UserRequest requestBody = new UserRequest ("", email, password);

        Call<ResponseBody> call = services.signInRequest (requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d (DEBUG_LOGIN, response.body().toString());
                        try {
                            JSONObject responseObj = new JSONObject(response.body().string());
                            JSONObject userJsonObject = responseObj.getJSONObject ("customer");

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
                            startActivity (new Intent(AuthActivity.this, MainActivity.class));

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        if (errorTV.getVisibility() == View.GONE) {
                            errorTV.setVisibility (View.VISIBLE);
                        }
                        Log.d (DEBUG_LOGIN, "Response has empty body!!");
                        errorTV.setText (response.message());
                    }
                }
                else {
                    Log.d (DEBUG_LOGIN, "Response has failed!");
                    if (errorTV.getVisibility() == View.GONE) {
                        errorTV.setVisibility (View.VISIBLE);
                    }
                    errorTV.setText (R.string.sign_in_fail);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d (DEBUG_LOGIN, "throwable : " + t.toString());
                if (errorTV.getVisibility() == View.GONE) {
                    errorTV.setVisibility (View.VISIBLE);
                }
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