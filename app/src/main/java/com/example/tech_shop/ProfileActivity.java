package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {
    ShapeableImageView homeIcon;
    ShapeableImageView heartIcon;
    ShapeableImageView notifyIcon;
    ShapeableImageView profileIcon;

    FrameLayout homeContainer;
    FrameLayout heartContainer;
    FrameLayout notifyContainer;
    FrameLayout profileContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);

        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);


        homeContainer.setOnClickListener(v -> {
            resetIcons(); // reset icon khác về outline
            homeIcon.setImageResource(R.drawable.home); // đổi icon hiện tại
            // Chuyển trang, ví dụ mở Activity HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        heartContainer.setOnClickListener(v -> {
            resetIcons();
            heartIcon.setImageResource(R.drawable.heart);
            //Intent intent = new Intent(this, FavoritesActivity.class);
            //startActivity(intent);
        });

        notifyContainer.setOnClickListener(v -> {
            resetIcons();
            notifyIcon.setImageResource(R.drawable.notifications);

            //Intent intent = new Intent(this, NotificationsActivity.class);
            //startActivity(intent);
        });

        profileContainer.setOnClickListener(v -> {
            resetIcons();
            profileIcon.setImageResource(R.drawable.person);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });


    }

    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }

}