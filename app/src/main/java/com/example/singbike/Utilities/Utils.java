package com.example.singbike.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.LocalStorage.UserActivityDatabase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static final String KEY_REQUESTING_LOCATION_UPDATES_BG = "RequestLocationUpdate";

    public Utils () {}

//    public static ZonedDateTime getLocalCurrentDateTime () {
//        ZonedDateTime zonedDateTime = ZonedDateTime.now();
//
//    }

    /* get current datetime string of Singapore Timezone */
    public static String getCurrentDateTime () {
        Instant now = Instant.now();
        ZoneId singaporeTimeZone = ZoneId.of ("Asia/Singapore");
        ZonedDateTime currentDateTime = ZonedDateTime.ofInstant (now, singaporeTimeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("EEEE, MM/dd/yyyy - HH:mm");

        return currentDateTime.format (formatter);
    }

    public static String dateToStringFormat (ZonedDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern ("EEEE, MM/dd/yyyy - HH:mm");
        return dateTime.format (dateTimeFormatter);
    }

    public static String toLocalDateTime (String datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("d/MM/yy HH:mm", Locale.getDefault());
        LocalDateTime  localDateTime = LocalDateTime.parse (datetime, formatter);
        ZoneId singaporeTimeZone = ZoneId.of ("Asia/Singapore");
        ZonedDateTime utcDatetime = ZonedDateTime.of (localDateTime, ZoneOffset.UTC);
        ZonedDateTime sgDateTime = utcDatetime.withZoneSameInstant (singaporeTimeZone);

        return Utils.dateToStringFormat (sgDateTime);
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

    public static boolean isReservationExpired (String createdTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("d/MM/yy HH:mm", Locale.getDefault());
        LocalDateTime  localDateTime = LocalDateTime.parse (createdTime, formatter);
        ZoneId singaporeTimeZone = ZoneId.of ("Asia/Singapore");
        ZonedDateTime utcDatetime = ZonedDateTime.of (localDateTime, ZoneOffset.UTC);
        ZonedDateTime sgDateTime = utcDatetime.withZoneSameInstant (singaporeTimeZone);
        Log.d ("DEBUG_RESERVATION", sgDateTime.toString() + " : " + ZonedDateTime.now().toString());
        Log.d ("DEBUG_RESERVATION", ChronoUnit.MINUTES.between (sgDateTime, ZonedDateTime.now()) + " minutes");
        if (ChronoUnit.MINUTES.between (sgDateTime, ZonedDateTime.now()) > 10) {
            Log.d ("DEBUG_RESERVATION", "Reservation Expired!");
        }
        else {
            Log.d ("DEBUG_RESERVATION", "Reservation Still Active!");
        }


        return ChronoUnit.MINUTES.between (sgDateTime, ZonedDateTime.now()) > 10;
    }

    /* total ride time in seconds */
    public static int getTotalRideTime (String rideTime) {
        int minutesToSeconds = Integer.parseInt (rideTime.split(":")[0]) * 60;
        return minutesToSeconds + Integer.parseInt (rideTime.split(":")[1]);
    }

    /* Ride Charges: 1$ per 30 minutes */
    public static double calculateTripFare (String rideTime) {
        int totalSeconds = Utils.getTotalRideTime (rideTime);
        int totalMinutes = totalSeconds / 60;

        return (double) (totalMinutes / 30) + 1;
    }

    /* calculate distance based on rideTime. Assume rider can reach 200 metres in 10 seconds */
    public static double calculateDistance (String rideTime) {
        int totalRideTime = Utils.getTotalRideTime (rideTime); // seconds
        int distanceInMetres = (totalRideTime % 10) * 200;
        return (double) distanceInMetres/1000; // convert it to kilomeres
    }


//    public static void setRunServiceInBackGround ()
}
