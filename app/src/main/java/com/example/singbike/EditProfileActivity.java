package com.example.singbike;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    private static final String REQUEST_TAG = "USER_DETAIL_REQUEST";
    private EditText usernameET, emailET;
    private ImageButton avatarImageButton;
    private User user = null;
    private RequestQueue requestQueue;

    @Override
    public void onStart () {
        super.onStart();
        /* making a network request for user details */
        requestQueue = Volley.newRequestQueue (this);
        final String url = "https://jsonplaceholder.typicode.com/users/1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String username = response.getString ("username");
                        String email = response.getString ("email");
                        user = new User (username, email);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        );

        jsonObjectRequest.setTag (REQUEST_TAG);
        requestQueue.add (jsonObjectRequest);
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_profile);

        final ImageButton backButton = findViewById (R.id.backButton_EditProfile);
        avatarImageButton = findViewById (R.id.avatarContainerImageButton);
        usernameET = findViewById (R.id.unameET_profile);
        emailET = findViewById (R.id.emailET_profile);
        final Button changePasswordButton = findViewById (R.id.changePwdButton);

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
