package com.example.singbike;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.singbike.Authentication.SignInDialogFragment;
import com.example.singbike.Authentication.SignUpActivity;
import com.example.singbike.Models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private static final String DEBUG_LOGIN = "DEBUG_LOGIN";

    private RequestQueue signInRequest;

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

                        final EditText usernameET = bottomSheet.findViewById (R.id.usernameETSignIn);
                        final EditText passwordET = bottomSheet.findViewById (R.id.passwordETSignIn);
                        final Button signInButton = bottomSheet.findViewById (R.id.signInBtnSignIn);
                        final TextView errorTextView = bottomSheet.findViewById (R.id.errorTV_SignIn);

                        signInButton.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        signInRequest = Volley.newRequestQueue (AuthActivity.this);
                                        final String signInURL = "http://10.0.2.2:8000/customers/login/";

                                        // Construct JSON Data for POST Request
                                        JSONObject signInData = new JSONObject();
                                        try {
                                            signInData.put ("email", usernameET.getText().toString());
                                            signInData.put ("password", passwordET.getText().toString());
                                        }
                                        catch (JSONException je) {
                                            je.printStackTrace();
                                        }

                                        // Construct the request
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, signInURL, signInData,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    /* successfully sign in */
                                                    errorTextView.setVisibility (View.GONE);
                                                    handleSignInSuccess (AuthActivity.this, response);
                                                }
                                            }, new Response.ErrorListener () {
                                                @Override
                                                public void onErrorResponse (VolleyError error) {
                                                    /* Error Sign In */
                                                    errorTextView.setVisibility (View.VISIBLE);
                                                    Log.d (DEBUG_LOGIN, "status code : " + String.valueOf(error.networkResponse.statusCode));
                                                    String errorMessage = new String (error.networkResponse.data, StandardCharsets.UTF_8);
                                                    Log.d (DEBUG_LOGIN, "message : " + errorMessage);
                                                    errorTextView.setText (errorMessage);
                                                }
                                        });

                                        jsonObjectRequest.setTag ("SignInRequest");
                                        signInRequest.add (jsonObjectRequest);
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
        if (signInRequest != null)
            signInRequest.cancelAll ("SignInRequest");
    }

    /* Cancel Network Request if the activity is not longer in the foreground */
    @Override
    public void onPause () {
        super.onPause();
        if (signInRequest != null)
            signInRequest.cancelAll ("SignInRequest");
    }

    /* sign in user */
    private void handleSignInSuccess (Context context, JSONObject response) {
        try {
            JSONObject userJSONObject = response.getJSONObject ("customer");
            User user = new User();
            user.setID (userJSONObject.getInt ("id"));
            user.setEmail (userJSONObject.getString("email"));
            user.setUsername (userJSONObject.getString("username"));
            user.setBalance (userJSONObject.getDouble("balance"));
            user.setCredits (userJSONObject.getInt("credits"));
            user.setCreated_at (userJSONObject.getString("created_at"));
            user.setUpdated_at (userJSONObject.getString("updated_at"));

            /* save user details in SharedPreferences (Local Storage) */
            saveToSharedPref (user);

            Intent intent = new Intent (context, MainActivity.class);
            intent.putExtra ("user", user);
            startActivity (intent);
        }
        catch (JSONException je) {
            je.printStackTrace();
        }
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