package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.email_input);
        resetButton = findViewById(R.id.reset_button);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            sendResetEmail(email);
        });
    }

    private void sendResetEmail(String email) {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        // Bọc chuỗi email trong dấu ngoặc kép để tạo JSON hợp lệ
        RequestBody body = RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                "\"" + email + "\""
        );

        Call<Void> call = api.sendEmailVerify(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset link sent successfully!", Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình OTPActivity
                    Intent intent = new Intent(ForgotPasswordActivity.this, OTPActivity.class);
                    intent.putExtra("email", email); // Gửi email sang OTPActivity
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}