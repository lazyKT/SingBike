package com.example.singbike.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.OptionsRecyclerViewAdapter;
import com.example.singbike.AuthActivity;
import com.example.singbike.EditProfileActivity;
import com.example.singbike.Fragments.AccountTab.AchievementsFragment;
import com.example.singbike.Fragments.AccountTab.ContactFragment;
import com.example.singbike.Fragments.AccountTab.ReportFragment;
import com.example.singbike.Fragments.AccountTab.RideHistoryFragment;
import com.example.singbike.Fragments.AccountTab.WalletFragment;
import com.example.singbike.Models.User;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.R;
import com.google.gson.Gson;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountFragment extends  Fragment{

    private static final String REQUEST_TAG = "PROFILE_UPDATE";
    private static final String DEBUG_AVATAR_FETCH = "DEBUG_AVATAR_FETCH";

    User user = null;
    private ImageView avatarImageView;

    public AccountFragment () {
        super (R.layout.fragment_account);
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition (R.transition.slide_right));
    }


    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarImageView = view.findViewById (R.id.avatarIV);
        final TextView usernameTV = view.findViewById (R.id.usernameTV_account);
        final TextView emailTV = view.findViewById (R.id.emailTV_account);

        Gson gson = new Gson();
        SharedPreferences userPrefs = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String jsonString = userPrefs.getString ("UserDetails", "");
        if (jsonString != null) {
            if (!jsonString.equals("")) {
                user = gson.fromJson(jsonString, User.class);
            }
            else {
                // if the app is unable to get user details from SharedPreferences, Log out the app
                logOut();
            }
        }
        else {
            logOut();
        }


        /* fetch and show the User Avatar on avatarImageView */
        fetchAvatar ();
        usernameTV.setText (user.getUsername());
        emailTV.setText (user.getEmail());

        /* edit profile button tap */
        final Button editBtn = view.findViewById (R.id.editButton_account);
        editBtn.setOnClickListener (
                v -> {
                    Intent intent = new Intent (requireActivity(), EditProfileActivity.class);
                    startActivity(intent);
                }
        );

        /* Container for other options, payment, setting etc ... */
        final RecyclerView optionsRV = view.findViewById (R.id.accountRecyclerView);
        final String[] options = getResources().getStringArray(R.array.account_options);

        // instantiate layout manager to attach a view to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager (getActivity());
        optionsRV.setLayoutManager (layoutManager);

        OptionsRecyclerViewAdapter adapter = new OptionsRecyclerViewAdapter(options,
                // options item click listener
                options1 -> {
                    switch (options1) {
                        case "Contact":
                            (requireActivity()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .setReorderingAllowed (true)
                                    .addToBackStack ("contact")
                                    .replace (R.id.fragmentContainerView, ContactFragment.class, null)
                                    .commit();
                            break;
                        case "Wallet":
                            (requireActivity()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .setReorderingAllowed (true)
                                    .addToBackStack ("wallet")
                                    .replace (R.id.fragmentContainerView, WalletFragment.class, null)
                                    .commit();
                            break;
                        case "Achievements":
                            (requireActivity()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .setReorderingAllowed (true)
                                    .addToBackStack ("achievements")
                                    .replace (R.id.fragmentContainerView, AchievementsFragment.class, null)
                                    .commit();
                            break;
                        case "Ride History":
                            (requireActivity()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .setReorderingAllowed (true)
                                    .addToBackStack ("ride history")
                                    .replace (R.id.fragmentContainerView, RideHistoryFragment.class, null)
                                    .commit();
                            break;
                        case "Report":
                            (requireActivity()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack ("report")
                                    .setReorderingAllowed (true)
                                    .replace (R.id.fragmentContainerView, ReportFragment.class, null)
                                    .commit();
                            break;
                        case "Log Out":
                            logOut();
                            break;
                    }
                }
        );
        optionsRV.setAdapter(adapter);

    }

    /* display error dialog */
    private void displayErrorDialog () {

    }


    /* fetch Avatar from the server */
    private void fetchAvatar () {

        if (user == null)
            return;

        final String avatarUrl = String.format (Locale.getDefault(), "customers/avatar/%d", user.getID());

        Retrofit retrofit = RetrofitClient.getRetrofit();
        RetrofitServices services = retrofit.create (RetrofitServices.class);

        Call<ResponseBody> call = services.fetchAvatar (avatarUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream (response.body().byteStream());
                        avatarImageView.setImageBitmap (bitmap);
                    }
                    else {
                        Log.d (DEBUG_AVATAR_FETCH, "Response body is NULL!");
                    }
                }
                else {
                    Log.d (DEBUG_AVATAR_FETCH, "Response is not successful!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d (DEBUG_AVATAR_FETCH, "Throwable : " + t.toString());
            }
        });
    }

    private void logOut () {
        AlertDialog.Builder builder = new AlertDialog.Builder (requireActivity());
        builder.setTitle ("Are you sure you want to log out?")
                .setPositiveButton ("LOG OUT", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    // remove the sharePreferences value from the device storage
                    SharedPreferences userPrefs = requireActivity().getSharedPreferences ("User", Context.MODE_PRIVATE);
                    SharedPreferences preferences = requireActivity().getSharedPreferences(getString(R.string.authState), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    SharedPreferences.Editor editor1 = userPrefs.edit();
                    editor1.clear();
                    editor1.apply();
                    editor.clear();
                    editor.apply();
                    // logout user
                    Intent intent = new Intent (requireActivity(), AuthActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

}
