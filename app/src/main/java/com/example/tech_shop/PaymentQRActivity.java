package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.ConfirmPurchaseRequest;
import com.example.tech_shop.models.ReceiveInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentQRActivity extends AppCompatActivity {


    private ImageView imgQR;
    private TextView tvAmount;
    private ImageView btnBack;
    Button btnComplete;
    private String orderId;   // <-- khai báo đúng chỗ

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

        // Lấy dữ liệu Intent
        String qrUrl = getIntent().getStringExtra("QR_URL");
        long amount = getIntent().getLongExtra("AMOUNT", 0);
        orderId = getIntent().getStringExtra("ORDER_ID");

        Glide.with(this).load(qrUrl).into(imgQR);
        tvAmount.setText(String.format("%,d₫", amount));

        // Bấm nút hoàn thành đơn hàng
        btnComplete.setOnClickListener(v -> confirmCOD());


    }

    private void confirmCOD() {
        String paymentMethod = "BANK";

        ConfirmPurchaseRequest body = new ConfirmPurchaseRequest(
                new ReceiveInfo("Cao Minh Quang", "0776292440",
                        "Chung Cư Phúc Đạt, Dĩ An, Bình Dương"),
                paymentMethod
        );

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.confirmPurchase(orderId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showCustomToast("Order placed successfully!");
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


    private void showCustomToast(String message, String subMessage, int iconResId) {
        View customToastView = getLayoutInflater().inflate(R.layout.custom_toast, null);

        // Cập nhật main message
        TextView textView = customToastView.findViewById(R.id.text_message);
        textView.setText(message);

        // Cập nhật sub-message nếu có
        TextView subTextView = customToastView.findViewById(R.id.text_sub_message);
        if (subMessage != null && !subMessage.isEmpty()) {
            subTextView.setText(subMessage);
            subTextView.setVisibility(View.VISIBLE);
        } else {
            subTextView.setVisibility(View.GONE);
        }

        // Cập nhật icon
        ImageView iconView = customToastView.findViewById(R.id.icon_toast);
        iconView.setImageResource(iconResId);

        // Tạo và show Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 1000);  // Vị trí giống hình
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }
    // Overload không cần subMessage, mặc định icon success
    private void showCustomToast(String message) {
        showCustomToast(message, null, R.drawable.check);
    }

    // Overload không cần subMessage, có thể thay icon
    private void showCustomToast(String message, int iconResId) {
        showCustomToast(message, null, iconResId);
    }
}