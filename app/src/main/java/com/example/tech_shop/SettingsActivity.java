package com.example.tech_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    Button btnLogOut;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        btnLogOut = findViewById(R.id.btnLogOut);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnLogOut.setOnClickListener(v -> {
            // ✅ Xóa dữ liệu SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Xóa toàn bộ dữ liệu (token, username, isLoggedIn)
            editor.apply();

            Toast.makeText(SettingsActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // ✅ Chuyển về màn hình đăng nhập
            Intent intent = new Intent(SettingsActivity.this, LogInActivity.class);
            startActivity(intent);
            finish(); // đóng HomeActivity để không quay lại bằng nút Back
        });
    }
}