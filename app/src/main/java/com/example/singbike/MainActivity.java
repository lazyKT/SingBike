package com.example.singbike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.singbike.Dialogs.ReservationDialog;
import com.example.singbike.Fragments.AccountFragment;
import com.example.singbike.Fragments.ActivityFragment;
import com.example.singbike.Fragments.BookingFragment;
import com.example.singbike.Fragments.HomeFragment;
import com.example.singbike.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
    implements ReservationDialog.ReservationDialogListener {

    private final static String DEBUG_BOT_NAV = "DEBUG_BOTTOM_NAVIGATION";
    private final static String DEBUG_SAVEDINSTANCES = "DEBUG_SAVEDINSTANCES";
    private final static String DEBUG_CAMERA_PERMISSION = "DEBUG_CAM_PERMISSION";
    private static final String DEBUG_RESERVE = "DEBUG_RESERVE_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User currentUser = null;

        /* Receiving User Details passed from SignUP */
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            currentUser = bundle.getParcelable ("user");

        if (currentUser != null)
            Toast.makeText (getApplicationContext(), "Welcome " + currentUser.getUsername(), Toast.LENGTH_LONG ).show();

//        Log.d (DEBUG_SAVEDINSTANCES, savedInstanceState.toString());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // by default Home Page will be shown first
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add (R.id.fragmentContainerView, HomeFragment.class, null)
                    .commit();
        }
        BottomNavigationView botNavigationView = (BottomNavigationView) findViewById (R.id.botNavView);
        /* onClick Action on Navigation Menus */
        botNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.home_menu:
                                /* go to home page */
                                fragmentManager.beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace (R.id.fragmentContainerView, HomeFragment.class, null)
                                        .addToBackStack("home")
                                        .commit();
//                                Log.d (DEBUG_BOT_NAV, "at Home Page");
                                break;

                            case R.id.booking_menu:
                                /* go to booking page */
                                fragmentManager.beginTransaction()
                                        .setReorderingAllowed(true)
                                        .replace (R.id.fragmentContainerView, BookingFragment.class, null)
                                        .addToBackStack("booking")
                                        .commit();
//                                Log.d (DEBUG_BOT_NAV, "at Booking Page");
                                break;

                            case R.id.activity_menu:
                                /* go to Activity Page */
                                fragmentManager.beginTransaction()
                                        .replace (R.id.fragmentContainerView, ActivityFragment.class, null)
                                        .setReorderingAllowed (true)
                                        .addToBackStack ("activity")
                                        .commit();
//                                Log.d (DEBUG_BOT_NAV, "at Activity Page");
                                break;

                            case R.id.account_menu:
                                /* go to Account Page */
                                fragmentManager.beginTransaction()
                                        .setReorderingAllowed (true)
                                        .replace (R.id.fragmentContainerView, AccountFragment.class, null)
                                        .addToBackStack ("account")
                                        .commit();
//                                Log.d (DEBUG_BOT_NAV, "at Account Page");
                                break;
                        }

                        return true;
                    }
                }
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* this method will be called after the permission dialog action */

        final int CAMERA_ACCESS = 0;
        final int LOCATION_ACCESS = 1;

        switch (requestCode) {
            case CAMERA_ACCESS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d (DEBUG_CAMERA_PERMISSION, "onRequestPermission GRANTED!!!");
                }
                else {
                    // explain to user why we need this permission
                    Log.d (DEBUG_CAMERA_PERMISSION, "onRequestPermission DENIED. Show your DIALOG!!!");
                    Toast.makeText(this, R.string.camera_request, Toast.LENGTH_LONG).show();
                }
                break;
            case LOCATION_ACCESS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                else {

                }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        // Confirmation Result for Reservation of the bicycle
        Log.d (DEBUG_RESERVE, "After Positive Click!");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        // Confirmation Result for Reservation of the bicycle
        Log.d (DEBUG_RESERVE, "After Negative Click!");
    }
}