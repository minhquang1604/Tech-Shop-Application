package com.example.tech_shop.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://apibackend.runasp.net/";
    private static Retrofit retrofit = null;

    // ✅ Hàm lấy Retrofit client có token
    public static Retrofit getClient(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Thêm interceptor tự động gắn token vào mọi request
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("token", null);

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json");

                // Nếu có token thì gắn vào header
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit;
    }
}
