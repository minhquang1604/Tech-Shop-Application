package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    private EditText digit1, digit2, digit3, digit4, digit5, digit6;
    private Button verifyButton;
    private TextView resendLink;
    private ImageButton btnBack;

    private String email; // lấy từ màn hình trước (ForgotPasswordActivity)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpactivity);

        digit1 = findViewById(R.id.digit1);
        digit2 = findViewById(R.id.digit2);
        digit3 = findViewById(R.id.digit3);
        digit4 = findViewById(R.id.digit4);
        digit5 = findViewById(R.id.digit5);
        digit6 = findViewById(R.id.digit6);
        verifyButton = findViewById(R.id.verify_button);
        resendLink = findViewById(R.id.resend_link);
        btnBack = findViewById(R.id.btnBack);

        // Nhận email từ ForgotPasswordActivity
        email = getIntent().getStringExtra("email");

        btnBack.setOnClickListener(v -> finish());

        verifyButton.setOnClickListener(v -> verifyOtp());

        resendLink.setOnClickListener(v -> resendEmail());
    }

    private void verifyOtp() {
        String otp = digit1.getText().toString().trim() +
                digit2.getText().toString().trim() +
                digit3.getText().toString().trim() +
                digit4.getText().toString().trim() +
                digit5.getText().toString().trim() +
                digit6.getText().toString().trim();

        if (otp.length() != 6) {
            Toast.makeText(this, "Please enter 6-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);

        Call<Void> call = api.verifyOtpPassword(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OTPActivity.this, "OTP verified successfully!", Toast.LENGTH_SHORT).show();

                    // ✅ Chuyển sang màn hình đặt lại mật khẩu
                    Intent intent = new Intent(OTPActivity.this, NewPasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("otp", otp);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OTPActivity.this, "Invalid OTP or expired code!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendEmail() {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        // Gói email thành RequestBody đúng định dạng JSON
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                "\"" + email + "\""
        );

        // ✅ Gửi RequestBody, không phải String
        Call<Void> call = api.sendEmailVerify(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OTPActivity.this, "Resent email successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OTPActivity.this, "Failed to resend email.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
