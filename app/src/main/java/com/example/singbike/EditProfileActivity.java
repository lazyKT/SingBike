package com.example.singbike;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.singbike.BottomSheets.ChangePasswordBottomSheet;
import com.example.singbike.Models.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final String REQUEST_TAG = "PROFILE_UPDATE";
    private static final String DEBUG_PROFILE_UPDATE = "DEBUG_PROFILE_UPDATE";
    private EditText usernameET, emailET;
    private ImageButton avatarImageButton;
    private User user = null;
    private RequestQueue requestQueue;


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_profile);

        Gson gson = new Gson();
        SharedPreferences userPrefs = getSharedPreferences ("User", MODE_PRIVATE);
        String jsonString = userPrefs.getString ("UserDetails", "");
        if (jsonString != null && !jsonString.equals("")) {
            user = gson.fromJson (jsonString, User.class);
            Log.d (REQUEST_TAG, user.toString());
        }
        else {
            // if the app is unable to get user details from SharedPreferences, Log out the app
            startActivity (new Intent(EditProfileActivity.this, AuthActivity.class));
        }

        final ImageButton backButton = findViewById (R.id.backButton_EditProfile);
        avatarImageButton = findViewById (R.id.avatarContainerImageButton);
        usernameET = findViewById (R.id.unameET_profile);
        emailET = findViewById (R.id.emailET_profile);
        final Button changePasswordButton = findViewById (R.id.changePwdButton);
        final Button updateProfileButton = findViewById (R.id.updateBtn_profile);
        final LinearLayout creditScoreLayout = findViewById (R.id.creditScoreLayout);
        final TextView joinOnTextView = findViewById (R.id.joinOnTextView_Profile);
        final TextView lastUpdatedTextView = findViewById (R.id.lastUpdatTextView_Profile);
        final TextView creditScoreTextView = findViewById (R.id.creditScoreTextView_Profile);

        joinOnTextView.setText (user.getCreated_at());
        lastUpdatedTextView.setText (user.getUpdated_at());
        creditScoreTextView.setText (String.valueOf(user.getCredits()));

        backButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent (EditProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );

        avatarImageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // open avatar change options
                        openAvatarChangeOptionsSheet();
                    }
                }
        );

        /* on focused event on credit score layout: similar to hover effect */
        creditScoreLayout.setOnHoverListener(
            new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {
                    Log.d ("OnHover", "event state = " + event.getAction() );
                    return false;
                }
            }
        );

        /* on pressed event on credit score layout */
        creditScoreLayout.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (EditProfileActivity.this, CreditScoreActivity.class);
                    startActivity(intent);
                }
            }
        );

        /* update profile */
        updateProfileButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestQueue = Volley.newRequestQueue (EditProfileActivity.this);
                    final String editProfileURL = String.format (Locale.getDefault(), "http://10.0.2.2:8000/customers/%d", user.getID());

                    JSONObject updatedData = new JSONObject();

                    try {
                        updatedData.put ("username", usernameET.getText().toString());
                        updatedData.put ("email", emailET.getText().toString());
                    }
                    catch (JSONException je) {
                        je.printStackTrace();
                    }

                    Log.d (DEBUG_PROFILE_UPDATE, "UPDATE USER URL : " + editProfileURL);
                    JsonObjectRequest updateProfileRequest = new JsonObjectRequest (Request.Method.PUT, editProfileURL, updatedData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse (JSONObject response) {
                                // update successful
                                System.out.println (response);
                                handleUserUpdate (response);

                                /* update UI */
                                usernameET.setText (user.getUsername());
                                lastUpdatedTextView.setText (user.getUpdated_at());

                                Toast.makeText (getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener () {
                            @Override
                            public void onErrorResponse (VolleyError error) {
                                // update failed
                                String errorMessage = "Can't connect to Server";
                                if (error != null) {
                                    Log.d (DEBUG_PROFILE_UPDATE, "status code: " + error.networkResponse.statusCode);
                                    errorMessage = new String (error.networkResponse.data, StandardCharsets.UTF_8);
                                }
                                Log.d (DEBUG_PROFILE_UPDATE, "message : " + errorMessage);

                                AlertDialog.Builder builder = new AlertDialog.Builder (EditProfileActivity.this);
                                builder.setTitle (errorMessage)
                                        .setNegativeButton (R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick (DialogInterface dialogInterface, int which) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }
                    });

                    updateProfileRequest.setTag ("PROFILE_UPDATE");
                    requestQueue.add (updateProfileRequest);
                }
            }
        );

        /* change password */
        changePasswordButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangePasswordBottomSheet changePasswordBottomSheet = new ChangePasswordBottomSheet(getApplicationContext(), user);
                    changePasswordBottomSheet.show (getSupportFragmentManager(), changePasswordBottomSheet.getTag());
                }
            }
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        if (user != null) {
            usernameET.setText (user.getUsername());
            emailET.setText (user.getEmail());
        }

    }

    @Override
    public void onPause () {
        super.onPause();
        if (requestQueue != null)
            requestQueue.cancelAll (REQUEST_TAG);
    }

    /* post-update operations */
    private void handleUserUpdate (JSONObject response) {
        try {
            JSONObject updatedUser = response.getJSONObject ("customer");

            /* update the model first */
            user.setUsername (updatedUser.getString("username"));
            user.setUpdated_at (updatedUser.getString("updated_at"));

            /* update SharedPreferences (Local Storage) */
            Gson gson = new Gson();
            SharedPreferences userPrefs = getSharedPreferences ("User", MODE_PRIVATE);
            SharedPreferences.Editor editor = userPrefs.edit();
            String jsonString = gson.toJson (user);
            editor.putString ("UserDetails", jsonString);
            editor.apply();
        }
        catch (JSONException je) {
            je.printStackTrace();
        }
    }

    /* open avatar change options bottom sheet */
    private void openAvatarChangeOptionsSheet () {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                this,
                R.style.BottomSheetDialogTheme
        );
        final View bottomSheet = LayoutInflater.from (getApplicationContext())
                .inflate (R.layout.change_avatar_options, (LinearLayout) this.findViewById (R.id.changeAvatarSheet));
        final Button chooseFromGallery = bottomSheet.findViewById (R.id.chooseGalleryButton_UploadAvatar);
        final Button takePhoto = bottomSheet.findViewById (R.id.openCameraButton_UploadAvatar);
        final Button cancelButton = bottomSheet.findViewById (R.id.cancelButton_UploadAvatar);

        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                }
        );

        takePhoto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        viewCamera();
                    }
                }
        );

        chooseFromGallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setType ("image/*");
                        intent.setAction (Intent.ACTION_GET_CONTENT);
                        chooseFromGalleryLauncher.launch (intent);
                    }
                }
        );


        bottomSheetDialog.setContentView (bottomSheet);
        bottomSheetDialog.show();
    }


    /* request camera permission */
    private void viewCamera () {
        if (ContextCompat.checkSelfPermission (this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // permission already granted
            openCamera();
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // request app permission
            final AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setTitle (R.string.camera_request)
                    .setPositiveButton (R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton(R.string.never, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create();
            builder.show();
        }
    }

    private void openCamera () {
        try {
            Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoLauncher.launch (intent);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* activity result api: get result from another activity or intent */
    ActivityResultLauncher <Intent> takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (null != extras) {
                            Bitmap imageBitmap = (Bitmap) extras.get ("data");
                            avatarImageButton.setImageBitmap (imageBitmap);
                        }
                    }
                }
            });

    /* activity result api: get gallery images */
    ActivityResultLauncher <Intent> chooseFromGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null)
                            avatarImageButton.setImageURI (imageUri);
                    }
                }
            }
    );
}
