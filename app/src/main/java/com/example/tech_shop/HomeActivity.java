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
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.localStorage.WishlistStorage;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.Product;
import com.example.tech_shop.models.WishlistItem;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private Button button;

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
        button = findViewById(R.id.button);

        tvCartBadge = findViewById(R.id.tvCartBadge);


        button.setOnClickListener(v -> {
            // ✅ Xóa dữ liệu SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Xóa toàn bộ dữ liệu (token, username, isLoggedIn)
            editor.apply();

            Toast.makeText(HomeActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // ✅ Chuyển về màn hình đăng nhập
            Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
            startActivity(intent);
            finish(); // đóng HomeActivity để không quay lại bằng nút Back

        });

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
            //Intent intent = new Intent(this, FavoritesActivity.class);
            //startActivity(intent);
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
                    List<Product> products = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
                    recyclerView.setAdapter(new ProductAdapter(HomeActivity.this, products));
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });



        loadWishlistFromApi();


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




    private void loadWishlistFromApi() {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);

        Log.d("WishlistDebug", "Starting API call to fetch wishlist...");

        api.getWishlist().enqueue(new Callback<List<WishlistItem>>() {
            @Override
            public void onResponse(Call<List<WishlistItem>> call, Response<List<WishlistItem>> response) {
                Log.d("WishlistDebug", "API response received.");

                if (response.isSuccessful() && response.body() != null) {
                    List<WishlistItem> wishlist = response.body();

                    // Convert list to JSON string for debug
                    Gson gson = new Gson();
                    String json = gson.toJson(wishlist);
                    Log.d("WishlistDebug", "Full JSON response from API:\n" + json);

                    // Save locally
                    WishlistStorage.saveWishlist(HomeActivity.this, wishlist);
                    Log.d("WishlistDebug", "Wishlist saved locally. Size: " + wishlist.size());

                    // 🔍 Read back to confirm correct serialization/deserialization
                    List<WishlistItem> restoredWishlist = WishlistStorage.getWishlist(HomeActivity.this);
                    Log.d("WishlistDebug", "Wishlist restored from local storage. Size: " + restoredWishlist.size());

                    // Pretty-print restored items for validation
                    for (WishlistItem item : restoredWishlist) {
                        Log.d("WishlistDebug", "Restored item: "
                                + "ID=" + item.getProductId()
                                + ", Name=" + item.getProductName()
                                + ", Price=" + item.getUnitPrice()
                                + ", Image=" + item.getImage());
                    }

                    Toast.makeText(HomeActivity.this, "Wishlist loaded", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("WishlistDebug", "Failed response. Code: " + response.code());
                    try {
                        Log.e("WishlistDebug", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("WishlistDebug", "Error reading error body", e);
                    }
                    Toast.makeText(HomeActivity.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WishlistItem>> call, Throwable t) {
                Log.e("WishlistDebug", "API call failed: " + t.getMessage(), t);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}

