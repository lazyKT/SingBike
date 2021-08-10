package com.example.singbike.Utilities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.preference.PreferenceManager;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.LocalStorage.UserActivityDatabase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES_BG = "RequestLocationUpdate";

    public Utils () {}

    /* get current datetime string of Singapore Timezone */
    public static String getCurrentDateTime () {
        Instant now = Instant.now();
        ZoneId singaporeTimeZone = ZoneId.of ("Asia/Singapore");
        ZonedDateTime currentDateTime = ZonedDateTime.ofInstant (now, singaporeTimeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("EEEE, MM/dd/yyyy - HH:mm");

        return currentDateTime.format (formatter);
    }

    /* insert user activity record into Room DB */
    public static void insertUserActivity (Context context, String type, int userID) {
        final UserActivityDatabase database = UserActivityDatabase.getInstance (context);

        String currentDateTimeStr = Utils.getCurrentDateTime();
        UserActivity signInActivity = new UserActivity (type, "", userID, currentDateTimeStr);

        database.userActivityDAO().insert (signInActivity);
    }


    public static void setRequestLocationUpdates (Context context, boolean flag) {
        PreferenceManager.getDefaultSharedPreferences (context)
                .edit()
                .putBoolean (KEY_REQUESTING_LOCATION_UPDATES_BG, flag)
                .apply();
    }

    public static boolean requestLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences (context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES_BG, false);
    }

    public static String locationToStringFormat (Location location) {
        return String.format (Locale.getDefault(), "( %.5f, %.5f )", location.getLatitude(), location.getLongitude());
    }

    public static String locationsToStringPaths (List<Location> locations) {

        String path = "";

        for (Location location : locations) {
            path = String.format (Locale.getDefault(), "%s | %s", path, Utils.locationToStringFormat (location));
        }
        return path;
    }

//    public static void displayErrorDialog (Activity activity, String title, String message) {
//        ErrorDialog dialog = new ErrorDialog (context, title, message);
//        dialog.show (activity.getSu);
//    }


//    public static void setRunServiceInBackGround ()
}
