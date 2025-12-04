package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.CheckoutAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.CartItem;
import com.example.tech_shop.models.Order;
import com.example.tech_shop.models.ConfirmPurchaseRequest;
import com.example.tech_shop.models.OrderItem;
import com.example.tech_shop.models.PaymentQRResponse;
import com.example.tech_shop.models.ReceiveInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvTotalPayment, tvMerchSubtotal, tvTotal;
    private RadioButton rbCOD, rbBank;
    private Button btnPlaceOrder;
    private CheckoutAdapter adapter;

    private String orderId;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);



        rvProducts = findViewById(R.id.rvProducts);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        rbCOD = findViewById(R.id.rbCOD);
        rbBank = findViewById(R.id.rbBank);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        tvMerchSubtotal = findViewById(R.id.tvMerchSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        rbCOD.setOnClickListener(v -> rbBank.setChecked(false));
        rbBank.setOnClickListener(v -> rbCOD.setChecked(false));

        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout layoutReceiver = findViewById(R.id.layoutReceiver);



        // Nhận orderId từ Intent
        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId != null) {
            fetchOrder(orderId);
        }

        btnPlaceOrder.setOnClickListener(v -> confirmPurchase());
    }

    private void fetchOrder(String orderId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();

                    // Load sản phẩm vào RecyclerView
                    List<OrderItem> products = order.getItems();
                    adapter = new CheckoutAdapter(products);
                    rvProducts.setAdapter(adapter);

                    // Hiển thị tổng tiền
                    tvTotalPayment.setText(String.format("%,.0f₫", order.getTotalAmount()));
                    tvMerchSubtotal.setText(String.format("%,.0f₫", order.getTotalAmount()));
                    tvTotal.setText(String.format("%,.0f₫", order.getTotalAmount()));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                showCustomToast("Lỗi tải đơn hàng", t.getMessage(), R.drawable.error);
            }
        });
    }

    private void confirmPurchase() {
        if (orderId == null) return;

        // COD
        if (rbCOD.isChecked()) {
            confirmCOD();
        }

        // BANK
        else if (rbBank.isChecked()) {
            getPaymentQR(); // 🔥 GỌI API QR
        }
    }


    private void confirmCOD() {
        String paymentMethod = "COD";

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

    private void getPaymentQR() {

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getPaymentQR(orderId).enqueue(new Callback<PaymentQRResponse>() {
            @Override
            public void onResponse(Call<PaymentQRResponse> call, Response<PaymentQRResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentQRResponse qr = response.body();

                    // 🔥 Chuyển sang màn hình hiển thị QR
                    Intent intent = new Intent(CheckoutActivity.this, PaymentQRActivity.class);
                    intent.putExtra("QR_URL", qr.getQr());
                    intent.putExtra("AMOUNT", qr.getAmount());
                    intent.putExtra("BANK_ID", qr.getBankId());
                    intent.putExtra("ACCOUNT", qr.getAccount());
                    intent.putExtra("ORDER_ID", orderId);

                    startActivity(intent);
                } else {
                    showCustomToast("Không load được QR", R.drawable.error);
                }
            }

            @Override
            public void onFailure(Call<PaymentQRResponse> call, Throwable t) {
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
