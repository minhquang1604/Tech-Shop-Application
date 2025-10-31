package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
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

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mở trang kết quả, gửi từ khóa qua Intent
            Intent intent = new Intent(SearchActivity.this, SearchDetailActivity.class);
            intent.putExtra("keyword", keyword);
            startActivity(intent);
        });
    }
}


