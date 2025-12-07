package com.example.tech_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.BannerAdapter;
import com.example.tech_shop.adapter.ProductAdapter;
import com.example.tech_shop.adapter.ProductFlashSaleAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.Product;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ShapeableImageView homeIcon;
    private ShapeableImageView heartIcon;
    private ShapeableImageView notifyIcon;
    private ShapeableImageView profileIcon;

    private FrameLayout homeContainer;
    private FrameLayout heartContainer;
    private FrameLayout notifyContainer;
    private FrameLayout profileContainer;

    private TextView searchBox;

    private ViewPager2 bannerViewPager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int currentPage = 0;
    private List<Integer> bannerImages;

    private ImageButton btnCart;
    private TextView tvCartBadge;

    private TextView tvCountdown;
    private Handler countdownHandler = new Handler();
    private Runnable countdownRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);
        bannerViewPager = findViewById(R.id.bannerViewPager);

        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);

        bannerViewPager = findViewById(R.id.bannerViewPager);
        searchBox = findViewById(R.id.searchBox);
        btnCart = findViewById(R.id.btnCart);

        tvCartBadge = findViewById(R.id.tvCartBadge);

        searchBox.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        });

        btnCart.setOnClickListener(v -> {

            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        });

        // --- Danh mục sản phẩm ---
        LinearLayout phonesContainer = findViewById(R.id.PhonesContainer);
        LinearLayout tabletContainer = findViewById(R.id.tabletContainer);
        LinearLayout laptopContainer = findViewById(R.id.laptopContainer);
        LinearLayout cameraContainer = findViewById(R.id.cameraContainer);
        LinearLayout dronesContainer = findViewById(R.id.dronesContainer);
        tvCountdown = findViewById(R.id.tvCountdown);

        // Gán sự kiện click từng danh mục
        phonesContainer.setOnClickListener(v -> openCategory("phones"));
        tabletContainer.setOnClickListener(v -> openCategory("Tablet"));
        laptopContainer.setOnClickListener(v -> openCategory("Laptop"));
        cameraContainer.setOnClickListener(v -> openCategory("cameras"));
        dronesContainer.setOnClickListener(v -> openCategory("Drones"));

        startCountdownToMidnight();

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
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        profileContainer.setOnClickListener(v -> {
            resetIcons();
            profileIcon.setImageResource(R.drawable.person);
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);

        });


        bannerImages = Arrays.asList(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        );

        BannerAdapter adapter = new BannerAdapter(this, bannerImages);
        bannerViewPager.setAdapter(adapter);

        // Auto slide mỗi 3 giây
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == bannerImages.size()) {
                    currentPage = 0;
                }
                bannerViewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // đổi sau 3 giây
            }
        };

        // Gọi API đếm số lượng sản phẩm
        loadCartCount(tvCartBadge);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );


        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Call<List<Product>> call = apiService.getProducts(100);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProducts = response.body();

                    List<Product> flashSaleList = new ArrayList<>();
                    List<Product> normalList = new ArrayList<>();

                    // Phân loại Flash Sale và sản phẩm thường
                    for (Product p : allProducts) {
                        if (p.getSale() != null && p.getSale().isActive()) {
                            flashSaleList.add(p);
                        } else {
                            normalList.add(p);
                        }
                    }

                    // ✅ Giới hạn Flash Sale chỉ 4 sản phẩm
                    if (flashSaleList.size() > 4) {
                        flashSaleList = new ArrayList<>(flashSaleList.subList(0, 4));
                    }

                    // ⚡ Flash Sale RecyclerView (4 cột, dùng ProductFlashSaleAdapter)
                    RecyclerView flashSaleRecycler = findViewById(R.id.recyclerFlashSale);
                    flashSaleRecycler.setHasFixedSize(true);
                    flashSaleRecycler.setLayoutManager(
                            new GridLayoutManager(HomeActivity.this, 4, GridLayoutManager.VERTICAL, false)
                    );
                    flashSaleRecycler.setAdapter(new ProductFlashSaleAdapter(HomeActivity.this, flashSaleList));

                    // 🛍 Sản phẩm thường RecyclerView (2 cột, dùng ProductAdapter)
                    RecyclerView productRecycler = findViewById(R.id.recyclerViewProducts);
                    productRecycler.setHasFixedSize(true);
                    productRecycler.setLayoutManager(
                            new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    );
                    productRecycler.setAdapter(new ProductAdapter(HomeActivity.this, normalList));

                } else {
                    Toast.makeText(HomeActivity.this, "Không thể tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });



    }



    private void openCategory(String category) {
        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void startCountdownToMidnight() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                // Tạo Calendar cho 24h hôm nay
                Calendar midnight = Calendar.getInstance();
                midnight.set(Calendar.HOUR_OF_DAY, 24); // hoặc 23:59:59 nếu muốn chính xác
                midnight.set(Calendar.MINUTE, 0);
                midnight.set(Calendar.SECOND, 0);
                midnight.set(Calendar.MILLISECOND, 0);

                long diff = midnight.getTimeInMillis() - now;

                if (diff > 0) {
                    long hours = diff / (1000 * 60 * 60);
                    long minutes = (diff / (1000 * 60)) % 60;
                    long seconds = (diff / 1000) % 60;

                    String timeLeft = String.format("%02d : %02d : %02d", hours, minutes, seconds);
                    tvCountdown.setText(timeLeft);

                    countdownHandler.postDelayed(this, 1000); // update mỗi giây
                } else {
                    tvCountdown.setText("00 : 00 : 00");
                }
            }
        };

        countdownHandler.post(countdownRunnable);
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

