package com.example.singbike.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.singbike.LocalStorage.UserActivity;
import com.example.singbike.LocalStorage.UserActivityDatabase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

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
//
//    /* get Day of Week and DateTime from normal DateTime */
//    public static String getDayOfWeekAndDateTime () {
//
//    }

}
