package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.NotificationAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.NotificationItem;
import com.example.tech_shop.models.PersonalInfoSimpleRequest;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    ShapeableImageView homeIcon, heartIcon, notifyIcon, profileIcon;
    FrameLayout homeContainer, heartContainer, notifyContainer, profileContainer;

    private TextView tvCartBadge;
    RecyclerView rvNotification;
    NotificationAdapter adapter;
    ImageButton btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);

        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);

        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);

        rvNotification = findViewById(R.id.rvNotification);
        rvNotification.setLayoutManager(new LinearLayoutManager(this));

        // Load username và thông báo
        loadUserAndNotifications();

        // Load giỏ hàng
        loadCartCount();

        btnCart.setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, CartActivity.class));
            overridePendingTransition(0, 0);
        });

        homeContainer.setOnClickListener(v -> {
            resetIcons();
            homeIcon.setImageResource(R.drawable.home);
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
        });

        heartContainer.setOnClickListener(v -> {
            resetIcons();
            heartIcon.setImageResource(R.drawable.heart);
            startActivity(new Intent(this, WishListActivity.class));
            overridePendingTransition(0, 0);
        });

        notifyContainer.setOnClickListener(v -> {
            resetIcons();
            notifyIcon.setImageResource(R.drawable.notifications);
            startActivity(new Intent(this, NotificationActivity.class));
            overridePendingTransition(0, 0);
        });

        profileContainer.setOnClickListener(v -> {
            resetIcons();
            profileIcon.setImageResource(R.drawable.person);
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    // ===============================
    // 1. LẤY USERNAME
    // ===============================
    private void loadUserAndNotifications() {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        api.getProfileSimple().enqueue(new Callback<PersonalInfoSimpleRequest>() {
            @Override
            public void onResponse(Call<PersonalInfoSimpleRequest> call, Response<PersonalInfoSimpleRequest> response) {
                if (response.isSuccessful() && response.body() != null) {

                    String username = response.body().getUsername();

                    if (username == null || username.isEmpty()) {
                        Log.e("USER", "Username null từ API getProfileSimple()");
                        return;
                    }

                    loadNotifications(username);
                }
            }

            @Override
            public void onFailure(Call<PersonalInfoSimpleRequest> call, Throwable t) {
                Log.e("API_USER", t.getMessage());
            }
        });
    }

    // ===============================
    // 2. LẤY THÔNG BÁO
    // ===============================
    private void loadNotifications(String username) {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        api.getNotifications(username).enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationItem> list = response.body();

                    // FIX LỖI: Truyền context vào constructor adapter
                    adapter = new NotificationAdapter(NotificationActivity.this, list);

                    rvNotification.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                Log.e("API_NOTI", t.getMessage());
            }
        });
    }

    // ===============================
    // 3. LOAD GIỎ HÀNG
    // ===============================
    private void loadCartCount() {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        api.getCartCount().enqueue(new Callback<CartCountResponse>() {
            @Override
            public void onResponse(Call<CartCountResponse> call, Response<CartCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getCount();

                    if (count > 0) {
                        tvCartBadge.setText(String.valueOf(count));
                        tvCartBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvCartBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvCartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CartCountResponse> call, Throwable t) {
                tvCartBadge.setVisibility(View.GONE);
                Log.e("CartCount", t.getMessage());
            }
        });
    }

    // Reset icon nav
    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }
}
