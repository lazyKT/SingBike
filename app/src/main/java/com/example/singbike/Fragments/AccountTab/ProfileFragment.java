// editable profile page
package com.example.singbike.Fragments.AccountTab;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.singbike.Models.User;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private static final String DEBUG_VALIDATION = "DEBUG_PWD_VALIDATION";
    private static final String DEBUG_NETWORK = "DEBUG_NETWORK_REQUEST";
    private static final String DEBUG_JSON = "DEBUG_JSON_RESOLVER";
    private static final String REQUEST_TAG = "USER_REQUEST";
    private RequestQueue requestQueue;
    private User user = null;

    public ProfileFragment () {
        super (R.layout.fragment_profile);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /* When fragment is detached */
        if (requestQueue != null)
            requestQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText usernameET = view.findViewById (R.id.unameET_profile);
        final EditText emailET = view.findViewById (R.id.emailET_profile);
        final Button changePasswordButton = view.findViewById (R.id.changePwdButton);

        /* fetch user details */
        requestQueue = Volley.newRequestQueue(requireActivity());
        final String url = "https://jsonplaceholder.typicode.com/users/1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        user = jsonToObject(json);

                        Log.d(DEBUG_JSON, user.getUsername() == null ? "null user" : user.getUsername());

                        usernameET.setText(user.getUsername());
                        emailET.setText(user.getEmail());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d (DEBUG_NETWORK, error.getMessage());
                    }
                });

        requestQueue.add(request);

        changePasswordButton.setOnClickListener (
                new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        /* create bottom sheet for password change */
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog (
                                requireActivity(),
                                R.style.BottomSheetDialogTheme
                        );
                        /* inflate the layout */
                        View bottomSheetView = LayoutInflater.from (requireActivity())
                                .inflate (R.layout.change_password, (LinearLayout) view.findViewById (R.id.changePwdBottomSheet));
                        /* initialize components */
                        final EditText currentPwdEditText, newPwdEditText, confirmPwdEditText;
                        final TextView errorTextView = bottomSheetView.findViewById (R.id.errorTV_changePwd);
                        final Button changeButton = bottomSheetView.findViewById (R.id.changePwdButton_sheet);

                        currentPwdEditText = bottomSheetView.findViewById (R.id.currentPwdEditText);
                        newPwdEditText = bottomSheetView.findViewById (R.id.newPwdEditText);
                        confirmPwdEditText = bottomSheetView.findViewById (R.id.confirmPwdEditText);

                        /* change password button click */
                        changeButton.setOnClickListener (
                            new View.OnClickListener () {
                                @Override
                                public void onClick (View v) {
                                    /* validate the new password */
                                    int validationResult = validateNewPassword(
                                            newPwdEditText.getText().toString(),
                                            confirmPwdEditText.getText().toString()
                                    );

                                    Log.d (DEBUG_VALIDATION, "Validation Result: " + validationResult);

                                    if (validationResult > -1) {
                                        /* send a password change network request */
                                        bottomSheetDialog.dismiss();
                                    }
                                    else {
                                        errorTextView.setVisibility(View.VISIBLE);
                                        errorTextView.setPadding(5,5,5,5);
                                        if (validationResult == -1)
                                            errorTextView.setText(requireActivity().getResources().getString(R.string.confirm_pwd_err));
                                        else if (validationResult == -2)
                                            errorTextView.setText(requireActivity().getResources().getString(R.string.pwd_err_chars));
                                        else
                                            errorTextView.setText(requireActivity().getResources().getString(R.string.pwd_err_length));

                                        currentPwdEditText.setText("");
                                        newPwdEditText.setText("");
                                        confirmPwdEditText.setText("");

                                        newPwdEditText.setFocusable(true);
                                    }

                                }
                            }
                        );

                        bottomSheetDialog.setContentView (bottomSheetView);
                        bottomSheetDialog.show(); // display bottomSheetDialog
//                        BottomSheetBehavior.from (bottomSheetView).setDraggable(false); // set bottomsheet display state to expanded
                    }
                }
        );

    }

    /**
     * Validation of the new password.
     * New Password must meet the password criteria and two passwords must be the same.
     * @param pwd new password
     * @param cPwd confirm new password
     * @return int
     * if validation failed, return negative int, otherwise return 0
     * Upon fail validation, return -1 if the passwords do not match.
     * Return -2, if the passwords do not meet the criteria.
     */
    int validateNewPassword (String pwd, String cPwd) {

        Pattern pwd_regex = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

        if (!pwd.equals(cPwd))
            return -1;

        /* password must be at least 8 characters long */
        if (pwd.length() < 8)
            return -3;

        /* password must contain at least 1 capital letter, 1 small letter and 1 number */
        if (pwd_regex.matcher(pwd).find())
            return -2;

        return 0;
    }


    private User fetchUserDetails (Context context) {

        requestQueue = Volley.newRequestQueue(context);
        final String url = "https://jsonplaceholder.typicode.com/users/1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject json) {
                    user = jsonToObject(json);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d (DEBUG_NETWORK, error.getMessage());
                }
        });

        requestQueue.add(request);

        return user;
    }


    /**
     * convert json object to local User Object
     * @param json -> json object get from the server
     */
    private User jsonToObject (JSONObject json) {
        try {

            Log.d(DEBUG_JSON, "username = " + json.getString("username"));
            Log.d(DEBUG_JSON, "email = " + json.getString("email"));
            String username = json.getString ("username");
            String email = json.getString ("email");

            user = new User(username, email);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

}
