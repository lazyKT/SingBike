package com.example.singbike.Services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.singbike.R;
import com.example.singbike.RideActivity;
import com.example.singbike.Utilities.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;
import java.util.Random;

public class MyLocationService extends Service {

    private static final String DEBUG_RIDE_SERVICE = "DEBUG_RIDE_SERVICE";
    private static final int NOTIFICATION_ID = 1234;
    private static final String CHANNEL_ID = "SinBike_Channel";
    private static final int SERVICE_ID = 224;

    private final IBinder binder = new LocalBinder();
    private final Random rand = new Random();

    private Notification notification;
    private Handler handler;
    private Runnable runnable;

    public class LocalBinder extends Binder {
        public MyLocationService getService() {
            return MyLocationService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d (DEBUG_RIDE_SERVICE, "onStartCommand");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().contains("start")) {
                handler = new Handler();
                runnable = () -> {
                    getNotification();
                    handler.postDelayed (this.runnable, 2000);
                };
                handler.post (runnable);
            }
            else {
                Log.d (DEBUG_RIDE_SERVICE, "onStartCommand : STOP");
                handler.removeCallbacks (runnable);
                stopForeground (true);
                stopSelf();
            }
        }

        return START_STICKY;
    }

    public int getRandomInts () {
        return this.rand.nextInt(100);
    }

    private void getNotification () {
        Intent notificationIntent = new Intent(this, RideActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText(getText(R.string.app_name))
                        .setSmallIcon(R.drawable.logo)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.app_name))
                        .build();

        // Notification ID cannot be 0.
        startForeground(NOTIFICATION_ID, notification);
    }
}
