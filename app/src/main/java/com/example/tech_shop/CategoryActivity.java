package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton btnBack;
    private ImageView imgCategory;
    private TextView tvCategoryTitle, tvCategoryDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.rvProducts);
        btnBack = findViewById(R.id.btnBack);
        imgCategory = findViewById(R.id.imgCategory);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        tvCategoryDesc = findViewById(R.id.tvCategoryDesc);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        // Lấy category
        String category = getIntent().getStringExtra("category");
        if (category == null) category = "Laptop";

        setCategoryHeader(category);
        loadProductsByCategory(category, 20);
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
                    List<Product> products = response.body();
                    recyclerView.setAdapter(new ProductAdapter(CategoryActivity.this, products));
                } else {
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
}
