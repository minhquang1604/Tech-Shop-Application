package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
}
