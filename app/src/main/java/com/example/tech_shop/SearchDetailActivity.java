package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.ProductAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Product;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailActivity extends AppCompatActivity {

    private ShapeableImageView homeIcon;
    private ShapeableImageView heartIcon;
    private ShapeableImageView notifyIcon;
    private ShapeableImageView profileIcon;

    private FrameLayout homeContainer;
    private FrameLayout heartContainer;
    private FrameLayout notifyContainer;
    private FrameLayout profileContainer;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private ImageButton btnBack;
    private AppCompatButton btnFilter;
    private List<Product> allProducts; // danh sách gốc từ API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_detail);

        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);

        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(v -> {
            String[] options = {
                    "Price: Low → High",
                    "Price: High → Low",
                    "Name: A → Z",
                    "Name: Z → A",
                    "Top Sales"
            };

            new androidx.appcompat.app.AlertDialog.Builder(SearchDetailActivity.this)
                    .setTitle("Filter")
                    .setItems(options, (dialog, which) -> {
                        switch (which) {
                            case 0: sortByPrice(true); break;   // Low → High
                            case 1: sortByPrice(false); break;  // High → Low
                            case 2: sortByName(true); break;    // A → Z
                            case 3: sortByName(false); break;   // Z → A
                            case 4: sortByTopSales(); break;    // Top Sales
                        }
                    })
                    .show();
        });


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

        homeContainer.setOnClickListener(v -> {
            resetIcons(); // reset icon khác về outline
            homeIcon.setImageResource(R.drawable.home); // đổi icon hiện tại
            // Chuyển trang, ví dụ mở Activity HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        heartContainer.setOnClickListener(v -> {
            resetIcons();
            heartIcon.setImageResource(R.drawable.heart);
            Intent intent = new Intent(this, WishListActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        notifyContainer.setOnClickListener(v -> {
            resetIcons();
            notifyIcon.setImageResource(R.drawable.notifications);

            //Intent intent = new Intent(this, NotificationsActivity.class);
            //startActivity(intent);
        });

        profileContainer.setOnClickListener(v -> {
            resetIcons();
            profileIcon.setImageResource(R.drawable.person);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        });
    }

    private void searchProduct(String keyword) {
        Call<List<Product>> call = apiService.getProductsSearch(keyword);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body(); // lưu danh sách gốc
                    recyclerView.setAdapter(new ProductAdapter(SearchDetailActivity.this, new ArrayList<>(allProducts)));
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

    private void sortByPrice(boolean lowToHigh) {
        List<Product> sorted = new ArrayList<>(allProducts);
        sorted.sort((p1, p2) -> lowToHigh
                ? Double.compare(p1.getPrice(), p2.getPrice())
                : Double.compare(p2.getPrice(), p1.getPrice()));
        recyclerView.setAdapter(new ProductAdapter(this, sorted));
    }

    private void sortByName(boolean aToZ) {
        List<Product> sorted = new ArrayList<>(allProducts);
        sorted.sort((p1, p2) -> aToZ
                ? p1.getName().compareToIgnoreCase(p2.getName())
                : p2.getName().compareToIgnoreCase(p1.getName()));
        recyclerView.setAdapter(new ProductAdapter(this, sorted));
    }

    private void sortByTopSales() {
        List<Product> sorted = new ArrayList<>(allProducts);
        sorted.sort((p1, p2) -> Integer.compare(p2.getQuantitySold(), p1.getQuantitySold()));
        recyclerView.setAdapter(new ProductAdapter(this, sorted));
    }

    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }
}