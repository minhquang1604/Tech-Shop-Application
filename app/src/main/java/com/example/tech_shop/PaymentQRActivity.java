package com.example.tech_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.ConfirmPurchaseRequest;
import com.example.tech_shop.models.NotificationSendRequest;
import com.example.tech_shop.models.ReceiveInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentQRActivity extends AppCompatActivity {

    private ImageView imgQR;
    private TextView tvAmount;
    private ImageView btnBack;
    private Button btnComplete;

    private String orderId;

    // ⬅️ NHẬN name – phone – address từ Intent
    private String name;
    private String phone;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_qractivity);

        imgQR = findViewById(R.id.imgQR);
        tvAmount = findViewById(R.id.tvAmount);
        btnBack = findViewById(R.id.btnBack);
        btnComplete = findViewById(R.id.btnPlaceOrder);

        btnBack.setOnClickListener(v -> finish());

        // --- LẤY DỮ LIỆU INTENT ---
        Intent intent = getIntent();
        String qrUrl = intent.getStringExtra("QR_URL");
        long amount = intent.getLongExtra("AMOUNT", 0);
        orderId = intent.getStringExtra("ORDER_ID");

        // ⬅️ Thông tin người nhận
        name = intent.getStringExtra("NAME");
        phone = intent.getStringExtra("PHONE");
        address = intent.getStringExtra("ADDRESS");

        Glide.with(this).load(qrUrl).into(imgQR);
        tvAmount.setText(String.format("%,d₫", amount));

        btnComplete.setOnClickListener(v -> confirmBANK());
    }

    private void confirmBANK() {
        String paymentMethod = "BANK";

        ReceiveInfo receiveInfo = new ReceiveInfo(name, phone, address);

        ConfirmPurchaseRequest body =
                new ConfirmPurchaseRequest(receiveInfo, paymentMethod);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.confirmPurchase(orderId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    sendOrderNotification(orderId);
                    showCustomToast("Order placed successfully!");

                    // ⬅️ QUAY VỀ HOME
                    Intent intent = new Intent(PaymentQRActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    showCustomToast("Order failed", R.drawable.error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showCustomToast("Lỗi mạng", t.getMessage(), R.drawable.error);
            }
        });
    }

    private void sendOrderNotification(String orderId) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username == null) return;

        String id = java.util.UUID.randomUUID().toString();
        String title = "Your order (" + orderId + ") has been placed successfully.";
        String message = "Thank you for shopping with TechShop!";

        NotificationSendRequest request =
                new NotificationSendRequest(id, title, message, username);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.sendNotification(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    private void showCustomToast(String message, String subMessage, int iconResId) {
        View customToastView = getLayoutInflater().inflate(R.layout.custom_toast, null);

        TextView textView = customToastView.findViewById(R.id.text_message);
        textView.setText(message);

        TextView subTextView = customToastView.findViewById(R.id.text_sub_message);
        if (subMessage != null && !subMessage.isEmpty()) {
            subTextView.setText(subMessage);
            subTextView.setVisibility(View.VISIBLE);
        } else {
            subTextView.setVisibility(View.GONE);
        }

        ImageView iconView = customToastView.findViewById(R.id.icon_toast);
        iconView.setImageResource(iconResId);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 1000);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }

    private void showCustomToast(String message) {
        showCustomToast(message, null, R.drawable.check);
    }

    private void showCustomToast(String message, int iconResId) {
        showCustomToast(message, null, iconResId);
    }
}



