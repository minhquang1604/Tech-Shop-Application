package com.example.tech_shop;

import android.util.Base64;
import org.json.JSONObject;

public class JwtUtils {
    public static String getUsernameFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) return null;

            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE));
            JSONObject json = new JSONObject(payload);

            return json.optString("unique_name", null);  // username thật
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}