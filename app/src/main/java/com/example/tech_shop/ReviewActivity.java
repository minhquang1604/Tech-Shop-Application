package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.ReviewAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Review;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView rvReview;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;       // dùng cho RecyclerView (hiển thị)
    private List<Review> allReviewsList;   // lưu toàn bộ review gốc
    private AppCompatButton btnSort;
    private ImageButton btnBack;


    private String productId; // Product ID cần load review

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);

        // Init view
        rvReview = findViewById(R.id.rvReview);
        btnSort = findViewById(R.id.btnSort);
        btnBack = findViewById(R.id.btnBack);

        reviewList = new ArrayList<>();
        allReviewsList = new ArrayList<>(); // danh sách gốc
        reviewAdapter = new ReviewAdapter(this, reviewList);

        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvReview.setAdapter(reviewAdapter);

        btnBack.setOnClickListener(v -> finish());

        // Load reviews
        productId = getIntent().getStringExtra("productId");
        loadReviews(productId);

        btnSort.setOnClickListener(v -> {
            String[] starsOptions = {"1 ⭐", "2 ⭐", "3 ⭐", "4 ⭐", "5 ⭐"};
            new androidx.appcompat.app.AlertDialog.Builder(ReviewActivity.this)
                    .setTitle("Sort by Stars")
                    .setItems(starsOptions, (dialog, which) -> {
                        int selectedStar = which + 1;
                        filterReviewsByStar(selectedStar);
                    })
                    .show();
        });
    }

    private void loadReviews(String productId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getReviewsByProductId(productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allReviewsList.clear();
                    allReviewsList.addAll(response.body()); // lưu toàn bộ review gốc

                    reviewList.clear();
                    reviewList.addAll(allReviewsList); // hiển thị tất cả review
                    reviewAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
            }
        });
    }

    private void filterReviewsByStar(int star) {
        List<Review> filtered = new ArrayList<>();
        for (Review r : allReviewsList) { // ✅ Luôn lọc trên danh sách gốc
            if (r.getStars() == star) {
                filtered.add(r);
            }
        }
        reviewAdapter.updateList(filtered); // adapter sẽ hiển thị danh sách filtered
    }
}


