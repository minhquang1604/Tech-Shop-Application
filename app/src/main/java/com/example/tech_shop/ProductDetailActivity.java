package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.ImageAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Product;
import com.example.tech_shop.models.ProductDetail;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.NumberFormat;
import java.util.Locale;


public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private TextView tvPrice, tvProductName;
    private ImageAdapter imageAdapter;
    private TextView tvImageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvPrice = findViewById(R.id.tvPrice);
        tvProductName = findViewById(R.id.tvProductName);
        tvImageCount = findViewById(R.id.tvImageCount);

        // Lấy ID sản phẩm từ Intent
        String productId = getIntent().getStringExtra("productId");
        loadProductDetails(productId);
    }

    private void loadProductDetails(String id) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getProductDetails(id).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetail product = response.body();

                    // Lấy danh sách ảnh
                    List<String> images = product.getImageURL();

                    // Gán adapter
                    imageAdapter = new ImageAdapter(images, ProductDetailActivity.this);
                    viewPagerImages.setAdapter(imageAdapter);

                    // Hiển thị số ảnh ban đầu
                    tvImageCount.setText("1/" + images.size());

                    // Theo dõi khi người dùng vuốt qua ảnh
                    viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            tvImageCount.setText((position + 1) + "/" + images.size());
                        }
                    });

                    // Hiển thị giá và tên
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    String formattedPrice = formatter.format(product.getPrice());
                    tvPrice.setText(formattedPrice + " ₫");
                    tvProductName.setText(product.getName());
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

}