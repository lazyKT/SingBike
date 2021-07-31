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

import com.example.singbike.NetworkRequests.UserRequest;
import com.example.singbike.Networking.Requests.ChangePasswordRequest;
import com.example.singbike.Networking.Requests.TransactionRequest;

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

    /* get user transactions */
    @GET
    Call<ResponseBody> fetchTransactions (@Url String transactionURL);

    /* create transaction */
    @POST ("customers/transactions/")
    Call<ResponseBody> createTransaction (
            @Body TransactionRequest body
        );
}
