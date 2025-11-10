package com.example.tech_shop;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.CartAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.CartItem;
import com.example.tech_shop.models.PrepareItem;
import com.example.tech_shop.models.PrepareRequest;
import com.example.tech_shop.models.PrepareResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerCart;
    private TextView tvTotalPrice;
    private Button btnBuyNow;
    private ImageButton btnBack;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnBack = findViewById(R.id.btnBack);

        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        loadCartItems();

        btnBack.setOnClickListener(v -> finish());

        btnBuyNow.setOnClickListener(v -> {
            List<CartItem> selectedItems = cartAdapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                showCustomToast("You have not selected any items for checkout", null, R.drawable.error);  // Ví dụ error icon
                return;
            }

            // Chuẩn bị danh sách gửi đi
            List<PrepareItem> prepareItems = new ArrayList<>();
            for (CartItem item : selectedItems) {
                prepareItems.add(new PrepareItem(item.getProductId(), item.getQuantity()));
            }

            PrepareRequest request = new PrepareRequest(prepareItems);
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
            apiService.prepareOrder(request).enqueue(new Callback<PrepareResponse>() {
                @Override
                public void onResponse(Call<PrepareResponse> call, Response<PrepareResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PrepareResponse prepareResponse = response.body();
                        String orderId = prepareResponse.getOrder().getOrderID();
                        // Xóa sản phẩm đã chọn khỏi UI
                        removeSelectedItemsFromCart(selectedItems);
                        // Chuyển sang CheckoutActivity
                        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                        intent.putExtra("ORDER_ID", orderId);
                        startActivity(intent);
                    } else {
                        showCustomToast("Không thể tạo đơn hàng!", null, R.drawable.error);
                    }
                }

                @Override
                public void onFailure(Call<PrepareResponse> call, Throwable t) {
                    showCustomToast("Lỗi kết nối: " + t.getMessage(), null, R.drawable.error);
                }
            });
        });
    }

    private void loadCartItems() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getCart().enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> cartItems = response.body();
                    cartAdapter = new CartAdapter(cartItems, CartActivity.this, total -> {
                        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                        tvTotalPrice.setText(nf.format(total) + "đ");
                    });
                    recyclerCart.setAdapter(cartAdapter);
                    attachSwipeToDelete(); // Thêm tính năng vuốt xóa
                } else {
                    showCustomToast("Failed to load cart!", null, R.drawable.error);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("Cart", "Error: " + t.getMessage());
                showCustomToast("Error loading data!", null, R.drawable.error);
            }
        });
    }

    private void removeSelectedItemsFromCart(List<CartItem> selectedItems) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        for (CartItem item : selectedItems) {
            int position = cartAdapter.getCartItems().indexOf(item);
            if (position != -1) {
                cartAdapter.removeItem(position);
            }
            apiService.removeFromCart(item.getProductId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // Có thể log hoặc showCustomToast nếu muốn
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("CartActivity", "Failed to remove product: " + t.getMessage());
                }
            });
        }
    }

    // Vuốt trái/phải để xóa sản phẩm
    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CartItem item = cartAdapter.getItemAt(position);
                // Xóa khỏi giao diện ngay
                cartAdapter.removeItem(position);
                // Gọi API DELETE để xóa thật
                ApiService apiService = RetrofitClient.getClient(CartActivity.this).create(ApiService.class);
                Call<Void> call = apiService.removeFromCart(item.getProductId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showCustomToast("Đã xóa " + item.getProductName() + " khỏi giỏ hàng!", null, R.drawable.check);
                        } else {
                            showCustomToast("Lỗi khi xóa sản phẩm khỏi server!", null, R.drawable.error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showCustomToast("Lỗi kết nối khi xóa sản phẩm!", null, R.drawable.error);
                    }
                });
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Nền đỏ và chữ Delete (giữ nguyên như code cũ)
                Paint paint = new Paint();
                View itemView = viewHolder.itemView;
                if (dX > 0) { // Vuốt sang phải
                    paint.setColor(Color.parseColor("#F44336"));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getLeft() + dX, (float) itemView.getBottom(), paint);
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(48);
                    paint.setTextAlign(Paint.Align.LEFT);
                    c.drawText("Delete", itemView.getLeft() + 50, itemView.getTop() + (itemView.getHeight() / 2f) + 16, paint);
                } else if (dX < 0) { // Vuốt sang trái
                    paint.setColor(Color.parseColor("#F44336"));
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(48);
                    paint.setTextAlign(Paint.Align.RIGHT);
                    c.drawText("Delete", itemView.getRight() - 50, itemView.getTop() + (itemView.getHeight() / 2f) + 16, paint);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerCart);
    }

    // Hàm custom Toast cho nhiều mục đích
    private void showCustomToast(String message, String subMessage, int iconResId) {
        // Inflate layout
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);  // Không cần root ViewGroup

        // Cập nhật main message
        TextView textView = customToastView.findViewById(R.id.text_message);
        textView.setText(message);

        // Cập nhật sub-message nếu có
        TextView subTextView = customToastView.findViewById(R.id.text_sub_message);
        if (subMessage != null && !subMessage.isEmpty()) {
            subTextView.setText(subMessage);
            subTextView.setVisibility(View.VISIBLE);
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

    // Overload nếu không cần subMessage và dùng icon default
    private void showCustomToast(String message) {
        showCustomToast(message, null, R.drawable.check);  // Default success icon
    }

    // Overload nếu không cần subMessage nhưng thay icon
    private void showCustomToast(String message, String subMessage) {
        showCustomToast(message, subMessage, R.drawable.check);
    }
}