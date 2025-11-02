package com.example.tech_shop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView tvTotalPrice;
    private Button btnBuyNow;
    private ImageView btnBack;
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
        btnBuyNow.setOnClickListener(v ->
                Toast.makeText(this, "Buying feature coming soon!", Toast.LENGTH_SHORT).show());
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
                    attachSwipeToDelete(); // 🔽 Thêm tính năng vuốt xóa
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("Cart", "Error: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Error loading data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔽 Vuốt trái/phải để xóa sản phẩm
    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
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
                            Toast.makeText(CartActivity.this,
                                    "Đã xóa " + item.getProductName() + " khỏi giỏ hàng!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CartActivity.this,
                                    "Lỗi khi xóa sản phẩm khỏi server!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CartActivity.this,
                                "Lỗi kết nối khi xóa sản phẩm!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                // Nền đỏ và chữ Delete
                Paint paint = new Paint();
                View itemView = viewHolder.itemView;

                if (dX > 0) { // Vuốt sang phải
                    paint.setColor(Color.parseColor("#F44336"));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(),
                            (float) itemView.getLeft() + dX, (float) itemView.getBottom(), paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(48);
                    paint.setTextAlign(Paint.Align.LEFT);
                    c.drawText("Delete", itemView.getLeft() + 50,
                            itemView.getTop() + (itemView.getHeight() / 2f) + 16, paint);
                } else if (dX < 0) { // Vuốt sang trái
                    paint.setColor(Color.parseColor("#F44336"));
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(48);
                    paint.setTextAlign(Paint.Align.RIGHT);
                    c.drawText("Delete", itemView.getRight() - 50,
                            itemView.getTop() + (itemView.getHeight() / 2f) + 16, paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerCart);
    }

}
