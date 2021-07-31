/* Configurations for REST Retrofit Client */
package com.example.singbike.Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
//    private static final String baseUrl = "http://167.71.221.189/";
    private static final String baseUrl = "http://10.0.2.2:8000/";

    public static Retrofit getRetrofit () {

        if (retrofit == null) {

            Gson gson = new GsonBuilder().setLenient().create();

            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            /* initialize retrofit instance */
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient).build();
        }
        return retrofit;
    }
}
