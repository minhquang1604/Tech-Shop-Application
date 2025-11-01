package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.ProductAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApiService apiService;
    private ImageButton btnBack, btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_detail);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Nhận từ khóa từ Intent
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            searchProduct(keyword);
        } else {
            Toast.makeText(this, "Không có từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchProduct(String keyword) {
        Call<List<Product>> call = apiService.getProductsSearch(keyword);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    recyclerView.setAdapter(new ProductAdapter(SearchDetailActivity.this, products));
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    Toast.makeText(SearchDetailActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(SearchDetailActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }


}