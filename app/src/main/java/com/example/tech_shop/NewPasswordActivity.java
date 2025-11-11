package com.example.tech_shop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {


    private TextInputEditText passwordInput, confirmPasswordInput;
    private Button updateButton;
    private ImageButton btnBack;

    private String email, otp; // nhận từ OTPActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password);

        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        updateButton = findViewById(R.id.update_button);
        btnBack = findViewById(R.id.btnBack);

        // Lấy email & otp từ Intent
        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        btnBack.setOnClickListener(v -> finish());

        updateButton.setOnClickListener(v -> {
            String newPassword = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

                resetPassword(email, newPassword, confirmPassword, otp);
            });
        }
    private void resetPassword(String email, String newPassword, String confirmPassword, String otp) {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("newPassword", newPassword);
        requestBody.put("confirmPassword", confirmPassword);
        requestBody.put("otp", otp);

        Call<Void> call = api.resetPassword(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình login
                } else {
                    Toast.makeText(NewPasswordActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NewPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

