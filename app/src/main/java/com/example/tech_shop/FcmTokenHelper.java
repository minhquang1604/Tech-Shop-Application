package com.example.tech_shop;

import android.content.Context;
import android.util.Log;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.FcmBody;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmTokenHelper {

    public static void sendTokenToServer(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Failed to get token");
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM", "Got FCM token: " + token);

                    ApiService api = RetrofitClient.getClient(context).create(ApiService.class);

                    // 🔥 Không dùng FcmBody nữa → dùng Map
                    Map<String, String> body = new HashMap<>();
                    body.put("token", token);

                    api.registerFcmToken(body).enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            Log.d("FCM", "Server response: " + response.code());
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Log.e("FCM", "Error: " + t.getMessage());
                        }
                    });
                });
    }

}

