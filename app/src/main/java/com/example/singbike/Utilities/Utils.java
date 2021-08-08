package com.example.singbike.Utilities;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.LocalStorage.UserActivityDatabase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "RequestLocationUpdate";

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
                .putBoolean (KEY_REQUESTING_LOCATION_UPDATES, flag)
                .apply();
    }

    public static boolean requestLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences (context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }
}
