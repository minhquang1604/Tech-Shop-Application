package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerifyActivity extends AppCompatActivity {
    AppCompatButton btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyActivity.this, LogInActivity.class);
            startActivity(intent);
        });

    }
}