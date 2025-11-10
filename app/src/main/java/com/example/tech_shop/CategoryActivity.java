package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Product;
import com.example.tech_shop.adapter.ProductAdapter;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private ShapeableImageView homeIcon;
    private ShapeableImageView heartIcon;
    private ShapeableImageView notifyIcon;
    private ShapeableImageView profileIcon;

    private FrameLayout homeContainer;
    private FrameLayout heartContainer;
    private FrameLayout notifyContainer;
    private FrameLayout profileContainer;
    private RecyclerView recyclerView;
    private ImageButton btnBack;
    private ImageView imgCategory;
    private TextView tvCategoryTitle, tvCategoryDesc;
    private List<Product> products; // Lưu danh sách sản phẩm hiện tại
    private ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);

        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);

        recyclerView = findViewById(R.id.rvProducts);
        btnBack = findViewById(R.id.btnBack);
        imgCategory = findViewById(R.id.imgCategory);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryDesc = findViewById(R.id.tvCategoryDesc);

        findViewById(R.id.btnSort).setOnClickListener(v -> showSortOptions());


        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        // Lấy category
        String category = getIntent().getStringExtra("category");
        if (category == null) category = "Laptop";

        setCategoryHeader(category);
        loadProductsByCategory(category, 20);

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

    private void setCategoryHeader(String category) {
        if (category.equalsIgnoreCase("phones")) {
            imgCategory.setImageResource(R.drawable.phones);
            tvCategoryTitle.setText("Phones");
            tvCategoryDesc.setText("Apple, Samsung, Xiaomi, Oppo, Vivo, Realme, Huawei, Nokia...");
        }
        else if (category.equalsIgnoreCase("Tablet")) {
            imgCategory.setImageResource(R.drawable.tablet);
            tvCategoryTitle.setText("Tablet");
            tvCategoryDesc.setText("Apple, Samsung, Xiaomi, Oppo, Vivo, Realme, Huawei, Nokia...");
        }
        else if (category.equalsIgnoreCase("Laptop")) {
            imgCategory.setImageResource(R.drawable.laptop);
            tvCategoryTitle.setText("Laptops");
            tvCategoryDesc.setText("Dell, HP, Asus, Acer, Lenovo, MSI, MacBook...");
        }
        else if (category.equalsIgnoreCase("cameras")) {
            imgCategory.setImageResource(R.drawable.camera);
            tvCategoryTitle.setText("Cameras");
            tvCategoryDesc.setText("Canon, Nikon, Sony, Fujifilm, GoPro, Panasonic...");
        }
        else if (category.equalsIgnoreCase("Drones")) {
            imgCategory.setImageResource(R.drawable.drones);
            tvCategoryTitle.setText("Drones");
            tvCategoryDesc.setText("DJI, Autel, Ryze, Parrot, Holy Stone...");
        }
        else {
            imgCategory.setImageResource(R.drawable.techshop);
            tvCategoryTitle.setText(category);
            tvCategoryDesc.setText("Danh mục sản phẩm khác");
        }
    }


    private void loadProductsByCategory(String category, int number) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<Product>> call = apiService.getProductsByCategory(category, number);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products = response.body();
                    adapter = new ProductAdapter(CategoryActivity.this, products);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    Toast.makeText(CategoryActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }

    private void showSortOptions() {
        if (products == null || products.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để sắp xếp", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] sortOptions = {"Giá tăng dần", "Giá giảm dần", "Tên A-Z", "Tên Z-A"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn cách sắp xếp")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Giá tăng dần
                            products.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                            break;
                        case 1: // Giá giảm dần
                            products.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                            break;
                        case 2: // Tên A-Z
                            products.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                            break;
                        case 3: // Tên Z-A
                            products.sort((p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                            break;
                    }
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
