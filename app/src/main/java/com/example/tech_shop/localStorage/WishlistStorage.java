package com.example.tech_shop.localStorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tech_shop.models.WishlistItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



import com.google.gson.Gson;

public class WishlistStorage {
    private static final String PREF_NAME = "wishlist_pref";
    private static final String KEY_WISHLIST = "wishlist";

    public static void saveWishlist(Context context, List<WishlistItem> wishlist) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(wishlist);
        editor.putString(KEY_WISHLIST, json);
        editor.apply();
    }

    public static List<WishlistItem> getWishlist(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_WISHLIST, null);

        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<WishlistItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addToWishlist(Context context, WishlistItem item) {
        List<WishlistItem> wishlist = getWishlist(context);
        // Tránh trùng sản phẩm
        boolean exists = false;
        for (WishlistItem w : wishlist) {
            if (w.getProductId().equals(item.getProductId())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            wishlist.add(item);
            saveWishlist(context, wishlist);
        }
    }

    public static void removeFromWishlist(Context context, String productId) {
        List<WishlistItem> wishlist = getWishlist(context);
        wishlist.removeIf(item -> item.getProductId().equals(productId));
        saveWishlist(context, wishlist);
    }
}
