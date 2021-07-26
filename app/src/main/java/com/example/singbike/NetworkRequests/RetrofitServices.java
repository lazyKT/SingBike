package com.example.singbike.NetworkRequests;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

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
}
