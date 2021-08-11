package com.example.singbike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.singbike.Dialogs.ErrorDialog;
import com.example.singbike.Networking.Requests.ReservationRequest;
import com.example.singbike.Networking.RetrofitClient;
import com.example.singbike.Networking.RetrofitServices;
import com.example.singbike.Utilities.AppExecutor;
import com.example.singbike.Utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateReservationActivity extends AppCompatActivity {

    private static final String DEBUG_RESERVATION = "DEBUG_RESERVATION";
    private static final String CHANNEL_ID = "RESERVATION_CANCELLED";
    private static final int NOTIFICATION_ID = 6864740;

    private String bikeID;
    private int userID;
    private RetrofitServices services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);

        Retrofit retrofit = RetrofitClient.getRetrofit();
        services = retrofit.create (RetrofitServices.class);

        if (getIntent() == null)
            finish();

        bikeID = getIntent().getStringExtra ("bikeID");
        userID = getIntent().getIntExtra ("userID", 0);

        if (userID == 0)
            finish();

        fetchLastReservation();

    }


    private void fetchLastReservation () {
        String url = String.format (Locale.getDefault(), "customers/last_reservation/%d", userID);

        Call<ResponseBody> call = services.getLastReservation (url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.code() == 204) {
                    // can create one
                    createReservation();
                }
                else if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // check the reservation,
                        // if it exceeds time limit (10 minutes), cancel and show notification to user, and create a new
                        // if it doesn't exceed the time and still active, show an information to user that he/she cannot have more than one reservation
                        try {
                            JSONObject jsonObject = new JSONObject (response.body().string());
                            Log.d (DEBUG_RESERVATION, response.body().string());
                            if (jsonObject.getString("status").equals("active")) {
                                // check the time
                                // if reservation created time is more than 10 minutes
                                if (Utils.isReservationExpired (jsonObject.getString("created_at"))) {
                                    // reservation expired
                                    // cancelled it
                                    // show notification
                                    showNotification();
                                    cancelReservation (jsonObject.getInt("reservation_id"));
                                }
                                else {
                                    // one user can reserve one bike at a time
                                    // if the current reservation is still active and valid,
                                    // the user cannot make another reservation
                                    // explain to the user why
                                    AlertDialog.Builder builder = new AlertDialog.Builder (CreateReservationActivity.this);
                                    builder.setTitle ("Reservation Failed!")
                                            .setMessage (R.string.reservation_in_progress)
                                            .setNeutralButton (R.string.ok,
                                                    (dialogInterface, which) -> startActivity (
                                                            new Intent(CreateReservationActivity.this, MainActivity.class
                                                            )
                                                    )
                                            );
                                    builder.create();
                                    builder.show();
                                }
                            }
                            else {
                                createReservation();
                            }
                        }
                        catch (JSONException | IOException e) {
                            displayErrorDialog ("RUNTIME ERROR", e.getMessage());
                        }
                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", "GET LAST Reservation: Response is Empty!");
                    }
                }
                else {
                    displayErrorDialog ("NETWORK_ERROR", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }


    private void createReservation () {

        ReservationRequest.CreateReservationRequest request = new ReservationRequest.CreateReservationRequest(userID, bikeID);

        Call<ResponseBody> call = services.createReservation (request);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        AppExecutor.getInstance().getDiskIO().execute(
                                () -> Utils.insertUserActivity (CreateReservationActivity.this, "reservation", userID)
                        );

                        AlertDialog.Builder builder = new AlertDialog.Builder (CreateReservationActivity.this);
                        View v = LayoutInflater.from (CreateReservationActivity.this)
                                .inflate (R.layout.dialog_reserve_success, null);

                        final TextView bikeIDtv = v.findViewById (R.id.bikeID_ReservationSuccess);
                        final TextView startTime = v.findViewById (R.id.time_ReservationSuccess);
                        final TextView endTime = v.findViewById (R.id.endTime_ReservationSuccess);
                        final Button dismissButton = v.findViewById (R.id.dismissButton_ReservationSucess);

                        bikeIDtv.setText (bikeID);
                        startTime.setText (Utils.getCurrentDateTime());
                        endTime.setText (Utils.dateToStringFormat(ZonedDateTime.now().plusMinutes(10)));

                        builder.setView (v);

                        dismissButton.setOnClickListener (v1 -> startActivity (new Intent (CreateReservationActivity.this, MainActivity.class)));

                        builder.create();
                        builder.show();

                    }
                    else {
                        displayErrorDialog ("NETWORK_ERROR", "POST Reservation: Response is Empty!");
                    }
                }
                else {
                    displayErrorDialog ("NETWORK_ERROR", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });

    }

    private void cancelReservation (int reservationID) {
        final String url = String.format (Locale.getDefault(), "bikes/cancel_reservation/%d", reservationID);

        Call<ResponseBody> call = services.cancelReservation (url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.code() == 204) {
                    // good
                    createReservation();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                displayErrorDialog ("NETWORK_ERROR", t.getMessage());
            }
        });
    }


    /* show notification to the user that the previous reservation is expired!!! */
    private void showNotification () {
        CharSequence name = getString(R.string.app_name);
        String description = "You have reserved a bike for more than 10 minutes and the bike has been released back to other users.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel (CHANNEL_ID, name, importance);
        channel.setDescription (description);
        NotificationManager manager = getSystemService (NotificationManager.class);
        manager.createNotificationChannel (channel);

        Intent intent = new Intent (CreateReservationActivity.this, MainActivity.class);
        intent.putExtra ("goto", "reservation");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(CreateReservationActivity.this, CHANNEL_ID)
                .setSmallIcon (R.drawable.logo)
                .setContentTitle ("Reservation Expired")
                .setStyle (new Notification.BigTextStyle()
                .bigText (description))
                .setContentIntent(pendingIntent)
                .setAutoCancel (true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from (CreateReservationActivity.this);
        notificationManagerCompat.notify (NOTIFICATION_ID, builder.build());
    }

    private void displayErrorDialog (String title, String message) {
        ErrorDialog dialog = new ErrorDialog (CreateReservationActivity.this, title, message);
        dialog.show (getSupportFragmentManager(), dialog.getTag());
    }

}