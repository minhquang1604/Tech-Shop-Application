package com.example.tech_shop;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmTokenHelper {

    public static void sendTokenToServer(String fcmToken) {

        if (fcmToken == null) {
            Log.e("FCM", "FCM Token null, cannot send to server");
            return;
        }

        Log.d("FCM", "Sending token to server: " + fcmToken);

        String url = "http://apibackend.runasp.net/api/Authenticate/fcm/register";

        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("token", fcmToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("FCM", "Failed to send token: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.d("FCM", "Server respond: " + response.code());
            }
        });
    }
}
