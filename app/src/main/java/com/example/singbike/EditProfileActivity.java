package com.example.singbike;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.singbike.BottomSheets.ChangePasswordBottomSheet;
import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.ImageHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditProfileActivity extends AppCompatActivity {

    private static final String REQUEST_TAG = "PROFILE_UPDATE";
    private static final String DEBUG_PROFILE_UPDATE = "DEBUG_PROFILE_UPDATE";
    private static final String DEBUG_AVATAR_UPLOAD = "DEBUG_AVATAR_UPLOAD";
    private static final String DEBUG_AVATAR_FETCH = "DEBUG_AVATAR_FETCH";
    private static final String DEBUG_FILE_PERMISSION = "DEBUG_FILE_PERMISSION";
    private static final String NETWORK_ERROR = "NETWORK_ERROR";

    private EditText usernameET, emailET;
    private ImageButton avatarImageButton;
    private LinearLayout profileLayout;
    private ProgressBar profileLoadingBar;
    private TextView joinOnTextView, lastUpdatedTextView, creditScoreTextView, totalTimeTextView, totalDistanceTextView;
    private User user = null;
    private RequestQueue requestQueue;


    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_edit_profile);

        final ImageButton backButton = findViewById (R.id.backButton_EditProfile);
        profileLayout = findViewById (R.id.profileLayout);
        profileLoadingBar = findViewById (R.id.profileLoadingBar);
        avatarImageButton = findViewById (R.id.avatarContainerImageButton);
        usernameET = findViewById (R.id.unameET_profile);
        emailET = findViewById (R.id.emailET_profile);
        final Button changePasswordButton = findViewById (R.id.changePwdButton);
        final Button updateProfileButton = findViewById (R.id.updateBtn_profile);
        final LinearLayout creditScoreLayout = findViewById (R.id.creditScoreLayout);
        joinOnTextView = findViewById (R.id.joinOnTextView_Profile);
        lastUpdatedTextView = findViewById (R.id.lastUpdatTextView_Profile);
        creditScoreTextView = findViewById (R.id.creditScoreTextView_Profile);
        totalDistanceTextView = findViewById (R.id.totalDistanceTravelled);
        totalTimeTextView = findViewById (R.id.totalTimeTravelled);

        /* retrieving required information from Local Storage and Web Server on background thread */
        AppExecutor.getInstance().getDiskIO().execute(() -> {
            Gson gson = new Gson();
            SharedPreferences userPrefs = getSharedPreferences ("User", MODE_PRIVATE);
            String jsonString = userPrefs.getString ("UserDetails", "");
            User user1 = null;
            Retrofit retrofit = RetrofitClient.getRetrofit();
            RetrofitServices services = retrofit.create (RetrofitServices.class);

            if (jsonString != null && !jsonString.equals("")) {
                user1 = gson.fromJson (jsonString, User.class);
            }

            if (user1 != null) {
                /* fetch User Avatar from Server */
                fetchAvatar (services, user1.getID());
                fetchTotalRideTime (services, user1.getID());
                fetchTotalDistance (services, user1);
            }
            else {
                // logout
                SharedPreferences.Editor editor = userPrefs.edit();
                editor.clear();
                editor.apply();
                runOnUiThread( () ->
                        startActivity (new Intent(EditProfileActivity.this, MainActivity.class))
                );
            }
        });

        backButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent (EditProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );

        avatarImageButton.setOnClickListener(
                v -> {
                    // open avatar change options
                    openAvatarChangeOptionsSheet();
                }
        );

        /* on focused event on credit score layout: similar to hover effect */
        creditScoreLayout.setOnHoverListener(
                (v, event) -> {
                    Log.d ("OnHover", "event state = " + event.getAction() );
                    return false;
                }
        );

        /* on pressed event on credit score layout */
        creditScoreLayout.setOnClickListener(
                v -> {
                    Intent intent = new Intent (EditProfileActivity.this, CreditScoreActivity.class);
                    startActivity(intent);
                }
        );

        /* update profile */
        updateProfileButton.setOnClickListener(
                v -> {
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
                            response -> {
                                // update successful
                                System.out.println (response);
                                handleUserUpdate (response);

                                /* update UI */
                                usernameET.setText (user.getUsername());
                                lastUpdatedTextView.setText (user.getUpdated_at());

                                Toast.makeText (getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                            }, error -> {
                                // update failed
                                String errorMessage = "Can't connect to Server";
                                if (error != null && error.networkResponse != null) {
                                    Log.d (DEBUG_PROFILE_UPDATE, "status code: " + error.networkResponse.statusCode);
                                    errorMessage = new String (error.networkResponse.data, StandardCharsets.UTF_8);
                                }

                                Log.d (DEBUG_PROFILE_UPDATE, "message : " + errorMessage);
                                displayErrorMessage (NETWORK_ERROR, errorMessage);
                            });

                    updateProfileRequest.setTag ("PROFILE_UPDATE");
                    requestQueue.add (updateProfileRequest);
                }
        );

        /* change password */
        changePasswordButton.setOnClickListener(
                v -> {
                    ChangePasswordBottomSheet changePasswordBottomSheet = new ChangePasswordBottomSheet(getApplicationContext(), user);
                    changePasswordBottomSheet.show (getSupportFragmentManager(), changePasswordBottomSheet.getTag());
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
                .inflate (R.layout.change_avatar_options, this.findViewById (R.id.changeAvatarSheet));
        final Button chooseFromGallery = bottomSheet.findViewById (R.id.chooseGalleryButton_UploadAvatar);
        final Button takePhoto = bottomSheet.findViewById (R.id.openCameraButton_UploadAvatar);
        final Button cancelButton = bottomSheet.findViewById (R.id.cancelButton_UploadAvatar);

        cancelButton.setOnClickListener(
                v -> bottomSheetDialog.dismiss()
        );

        takePhoto.setOnClickListener(
                v -> {
                    bottomSheetDialog.dismiss();
                    viewCamera();
                }
        );

        chooseFromGallery.setOnClickListener(
                v -> {
                    bottomSheetDialog.dismiss();
                    requestExternalStorage();
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
                    .setPositiveButton (R.string.allow, (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(R.string.never, (dialog, which) -> dialog.dismiss());
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

    /* request to read external storage */
    private void requestExternalStorage () {
        if (ContextCompat.checkSelfPermission (getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
               == PackageManager.PERMISSION_GRANTED ) {
            openFileStorage();
        }
        else if (shouldShowRequestPermissionRationale (Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // request app permission
            final AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setTitle (R.string.file_request)
                    .setPositiveButton (R.string.allow, (dialog, which) -> {
                        dialog.dismiss();
                        openFileStorage();
                    })
                    .setNegativeButton(R.string.never, (dialog, which) -> dialog.dismiss());
            builder.create();
            builder.show();
        }
        else {
            requestPermissions (new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            Log.d (DEBUG_FILE_PERMISSION, "Request For File Permission");
        }
    }

    /* open file storage */
    private void openFileStorage () {
        Intent intent = new Intent();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        chooseFromGalleryLauncher.launch (intent);
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

    /* activity result api: get gallery images and upload to server */
    ActivityResultLauncher <Intent> chooseFromGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null)
                        uploadAvatar (imageUri);
                }
            }
    );


    /* fetch user avatar */
    private void fetchAvatar (RetrofitServices services, int userID) {

        final String avatarUrl = String.format (Locale.getDefault(), "customers/avatar/%d", userID);

        Call<ResponseBody> call = services.fetchAvatar (avatarUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                Log.d (DEBUG_AVATAR_FETCH, "status code : " + response.code());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d (DEBUG_AVATAR_FETCH, response.body().toString());
                        /* create bitmap from response image data */
                        Bitmap bitmap = BitmapFactory.decodeStream (response.body().byteStream());
                        avatarImageButton.setImageBitmap (bitmap);
                    }
                    else {
                        Log.d (DEBUG_AVATAR_FETCH, "Response Body is NULL!");
                    }
                }
                else {
                    Log.d (DEBUG_AVATAR_FETCH, "Response is not Successful!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d (DEBUG_AVATAR_FETCH, "Throwable : " + t.toString());
            }
        });
    }


    /* upload user avatar */
    private void uploadAvatar (final Uri avatarUri) {

        if (avatarUri == null || avatarUri.getPath() == null)
            return;

        final String filePath = ImageHelper.getFilePath (getApplicationContext(), avatarUri);

        Log.d (DEBUG_AVATAR_UPLOAD, "Avatar Path: " + filePath);

        if (filePath == null) {
            Toast.makeText (getApplicationContext(), "Unsupported Media Type", Toast.LENGTH_SHORT).show();
            return;
        }

        /* First create a file */
        File file = new File(filePath);
        final String uploadURL = String.format (Locale.getDefault(), "customers/avatar/%d", user.getID());

        /* construct request body */
        RequestBody requestBody = RequestBody.create (MediaType.parse ("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData ("avatar", file.getName(), requestBody);

        /* get retrofit instance */
        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.uploadAvatar(uploadURL, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                // upload success
                runOnUiThread( () -> {
                    if (response.body() != null) {
                        profileLayout.setVisibility (View.VISIBLE);
                        profileLoadingBar.setVisibility (View.GONE);
                        avatarImageButton.setImageURI (avatarUri);
//                        Log.d(DEBUG_AVATAR_UPLOAD, response.body().toString());
                    }
                    else {
                        displayErrorMessage (NETWORK_ERROR, "Failed to Get Response!");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // upload failed
                Log.d (DEBUG_AVATAR_UPLOAD, "Throwable : " + t.toString() );
                runOnUiThread(() -> displayErrorMessage (NETWORK_ERROR, t.getMessage()));
            }
        });
    }

    /* get User Total Ride Time in minutes */
    private void fetchTotalRideTime (RetrofitServices services, int userID) {

        final String totalRideTimeURL = String.format (Locale.getDefault(), "customers/customer_ride_time/%d", userID);

        Call<ResponseBody> call = services.fetchTotalRideTime (totalRideTimeURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        runOnUiThread(() -> {
                            try {
                                totalTimeTextView.setText(response.body().string());
                            } catch (IOException e) {
                                displayErrorMessage (NETWORK_ERROR, e.getMessage());
                            }
                        });
                    }
                }
                else {
                    runOnUiThread(() -> displayErrorMessage (NETWORK_ERROR, "Failed to Retrieve Ride Time Data!"));
                }
            }

            @Override
            public void onFailure( @NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorMessage (NETWORK_ERROR, t.getMessage());
            }
        });
    }

    /* Retrieve User Total Distance Travelled */
    private void fetchTotalDistance (RetrofitServices services, User _user) {
        final String totalDistanceURL = String.format (Locale.getDefault(), "customers/customer_distances/%d", _user.getID());

        Call<ResponseBody> call = services.fetchTotalDistance (totalDistanceURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        runOnUiThread(() -> {
                            try {
                                /* Update UI */
                                profileLoadingBar.setVisibility (View.GONE);
                                profileLayout.setVisibility (View.VISIBLE);
                                totalDistanceTextView.setText (response.body().string());
                                joinOnTextView.setText (_user.getCreated_at());
                                lastUpdatedTextView.setText (_user.getUpdated_at());
                                creditScoreTextView.setText (_user.getCredits());
                                user = new User(_user);
                            } catch (IOException e) {
                                displayErrorMessage (NETWORK_ERROR, e.getMessage());
                            }
                        });
                    }
                }
                else {
                    runOnUiThread(() -> displayErrorMessage (NETWORK_ERROR, "Failed to Retrieve Total Distance Data!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorMessage (NETWORK_ERROR, t.getMessage());
            }
        });
    }

    /* display error dialog */
    private void displayErrorMessage (String title, String message) {
        ErrorDialog errorDialog = new ErrorDialog (EditProfileActivity.this, title, message);
        errorDialog.show (getSupportFragmentManager(), errorDialog.getTag());
    }
}
