package com.example.singbike.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.OptionsRecyclerViewAdapter;
import com.example.singbike.AuthActivity;
import com.example.singbike.Fragments.AccountTab.AchievementsFragment;
import com.example.singbike.Fragments.AccountTab.ContactFragment;
import com.example.singbike.Fragments.AccountTab.ProfileFragment;
import com.example.singbike.Fragments.AccountTab.RideHistoryFragment;
import com.example.singbike.Fragments.AccountTab.WalletFragment;
import com.example.singbike.R;

public class AccountFragment extends  Fragment{

    private static final String D_EDIT = "EDIT_PROFILE";

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

        /* edit profile button tap */
        final Button editBtn = view.findViewById (R.id.editButton_account);
        editBtn.setOnClickListener (
                new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        Log.d (D_EDIT, "button tapped!");

                        (requireActivity()).getSupportFragmentManager()
                                .beginTransaction()
                                .replace (R.id.fragmentContainerView, ProfileFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack ("profile")
                                .commit();

                    }
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
                new OptionsRecyclerViewAdapter.onOptionsClickListener() {
                    @Override
                    public void onOptionsClick(String options) {
                        switch (options) {
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
                            case "Log Out":
                                AlertDialog.Builder builder = new AlertDialog.Builder (requireActivity());
                                builder.setTitle ("Are you sure you want to log out?")
                                        .setPositiveButton ("LOG OUT", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick (DialogInterface dialogInterface, int which) {
                                                dialogInterface.dismiss();
                                                // remove the sharePreferences value from the device storage
                                                SharedPreferences preferences = requireActivity().getSharedPreferences(getString(R.string.authState), Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.clear();
                                                editor.apply();
                                                // logout user
                                                Intent intent = new Intent (requireActivity(), AuthActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create();
                                builder.show();
                                break;
                        }
                    }
                }
        );
        optionsRV.setAdapter(adapter);

    }

}
