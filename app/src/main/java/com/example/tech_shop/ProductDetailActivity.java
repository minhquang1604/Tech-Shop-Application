package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.ImageAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.AddToCartRequest;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.ProductDetail;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private TextView tvPrice, tvProductName, tvImageCount;
    private ImageAdapter imageAdapter;
    private TableLayout tableSpecs;
    private ImageButton btnBack, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvPrice = findViewById(R.id.tvPrice);
        tvProductName = findViewById(R.id.tvProductName);
        tvImageCount = findViewById(R.id.tvImageCount);
        tableSpecs = findViewById(R.id.tableSpecs);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        TextView tvCartBadge = findViewById(R.id.tvCartBadge);
        // Lấy ID sản phẩm từ Intent
        String productId = getIntent().getStringExtra("productId");
        loadProductDetails(productId);

        // Gọi API đếm số lượng sản phẩm
        loadCartCount(tvCartBadge);

        btnBack.setOnClickListener(v -> finish());
        btnCart.setOnClickListener(v -> {
            if (productId == null || productId.isEmpty()) {
                Log.e("Cart", "Product ID is null!");
                return;
            }

            ApiService apiService = RetrofitClient.getClient(ProductDetailActivity.this).create(ApiService.class);
            AddToCartRequest request = new AddToCartRequest(productId, 1); // Mặc định số lượng = 1

            apiService.addToCart(request).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Cart", "Added: " + response.body());
                        String message = response.body().get("message").toString();
                        Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Cart", "Add failed: " + response.code());
                        Toast.makeText(ProductDetailActivity.this, "Failed to add item to cart!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("Cart", "Error: " + t.getMessage());
                    Toast.makeText(ProductDetailActivity.this, "Network error!", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void loadProductDetails(String id) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getProductDetails(id).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetail product = response.body();

                    // Hiển thị ảnh
                    List<String> images = product.getImageURL();
                    imageAdapter = new ImageAdapter(images, ProductDetailActivity.this);
                    viewPagerImages.setAdapter(imageAdapter);
                    tvImageCount.setText("1/" + images.size());
                    viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            tvImageCount.setText((position + 1) + "/" + images.size());
                        }
                    });

                    // Hiển thị thông tin cơ bản
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    String formattedPrice = formatter.format(product.getPrice());
                    tvPrice.setText(formattedPrice + " ₫");
                    tvProductName.setText(product.getName());

                    // ✅ Hiển thị bảng thông số kỹ thuật
                    populateSpecsTable(product.getDetail());
                } else {
                    Log.e("API_ERROR", "Response null or failed");
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void populateSpecsTable(Map<String, String> details) {
        if (details == null || details.isEmpty()) return;

        tableSpecs.removeAllViews();

        int total = details.size();
        int index = 0;

        for (Map.Entry<String, String> entry : details.entrySet()) {
            TableRow row = new TableRow(this);

            // Thiết lập layout cho hai cột
            TableRow.LayoutParams leftParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TableRow.LayoutParams rightParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);

            // Ô bên trái (key)
            TextView keyView = new TextView(this);
            keyView.setText(entry.getKey());
            keyView.setTextSize(14);
            keyView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            keyView.setBackgroundResource(R.drawable.table_cell_left);
            keyView.setPadding(24, 24, 24, 24);
            keyView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            keyView.setLayoutParams(leftParams);

            // Ô bên phải (value)
            TextView valueView = new TextView(this);
            valueView.setText(entry.getValue());
            valueView.setTextSize(14);
            valueView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
//            valueView.setBackgroundResource(R.drawable.table_cell_right);
            valueView.setPadding(24, 24, 24, 24);
            valueView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            valueView.setLayoutParams(rightParams);

            // Thêm hai ô vào hàng
            row.addView(keyView);
            row.addView(valueView);

            // Thêm hàng vào bảng
            tableSpecs.addView(row);

            index++;
        }

        // Thêm viền bo quanh toàn bảng (nếu muốn)
        tableSpecs.setBackgroundResource(R.drawable.table_border_bg);
    }

    private void loadCartCount(TextView tvCartBadge) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getCartCount().enqueue(new Callback<CartCountResponse>() {
            @Override
            public void onResponse(Call<CartCountResponse> call, Response<CartCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getCount();
                    if (count > 0) {
                        tvCartBadge.setText(String.valueOf(count));
                        tvCartBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvCartBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvCartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CartCountResponse> call, Throwable t) {
                tvCartBadge.setVisibility(View.GONE);
                Log.e("CartCount", "Error: " + t.getMessage());
            }
        });
    }



}
