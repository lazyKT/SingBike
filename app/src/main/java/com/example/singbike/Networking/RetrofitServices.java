package com.example.singbike.Networking;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

import com.example.singbike.Networking.Requests.ChangePasswordRequest;
import com.example.singbike.Networking.Requests.ReservationRequest;
import com.example.singbike.Networking.Requests.TopUpRequest;
import com.example.singbike.Networking.Requests.TransactionRequest;
import com.example.singbike.Networking.Requests.TripRequest;
import com.example.singbike.Networking.Requests.UserRequest;

public interface RetrofitServices {

    /* sign in request */
    @POST ("customers/login/")
    Call<ResponseBody> signInRequest (
            @Body UserRequest body
    );

    /* sign up request */
    @POST ("customers/")
    Call<ResponseBody> signUpRequest (
            @Body UserRequest body
            );

    @GET
    Call<ResponseBody> fetchProfile (@Url String url);

    // to upload avatar
    @Multipart
    @POST
    Call<ResponseBody> uploadAvatar (
            @Url String uploadURL,
            @Part MultipartBody.Part part
        );

    // to download avatar
    @GET
    Call<ResponseBody> fetchAvatar (@Url String avatarURL);

    @GET
    Call<ResponseBody> fetchTotalDistance (@Url String totalDistanceURL);

    /* fetch user's total ride time */
    @GET
    Call<ResponseBody> fetchTotalRideTime (@Url String totalRideTImeURL);

    /* change user password request */
    @PUT
    Call<ResponseBody> changePassword (
            @Url String changePwdURL,
            @Body ChangePasswordRequest request
        );

    /* edit/update user information */
    @PUT
    Call<ResponseBody> updateUserProfile (
            @Url String url,
            @Body UserRequest body
    );


    /* get user transactions */
    @GET
    Call<ResponseBody> fetchTransactions (@Url String transactionURL);

    /* create transaction */
    @POST ("customers/transactions/")
    Call<ResponseBody> createTransaction (
            @Body TransactionRequest body
        );

    @PUT
    Call<ResponseBody> topUpBalance (
            @Url String url,
            @Body TopUpRequest body
            );

    @POST ("customers/trips/")
    Call<ResponseBody> createTrip (@Body TripRequest.TripCreateRequest body);

    @PUT
    Call<ResponseBody> endTrip (@Url String url, @Body TripRequest.TripEndRequest body);

    @POST ("bikes/reservations/")
    Call<ResponseBody> createReservation (@Body ReservationRequest.CreateReservationRequest body);

    @PUT
    Call<ResponseBody> editReservation (
            @Url String url,
            @Body ReservationRequest.EditReservationRequest body
            );

    @GET
    Call<ResponseBody> fetchUserTrips (@Url String url);

}
