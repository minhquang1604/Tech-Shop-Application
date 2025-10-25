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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.ProductAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Product;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Arrays;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {
    ShapeableImageView homeIcon;
    ShapeableImageView heartIcon;
    ShapeableImageView notifyIcon;
    ShapeableImageView profileIcon;

    FrameLayout homeContainer;
    FrameLayout heartContainer;
    FrameLayout notifyContainer;
    FrameLayout profileContainer;

    LinearLayout favouriteContainer;
    LinearLayout laptopContainer;
    LinearLayout phoneContainer;
    LinearLayout tabletContainer;
    LinearLayout cameraContainer;

    ImageView imgPopular;
    ImageView imgLaptop;
    ImageView imgPhone;
    ImageView imgTablet;
    ImageView imgCamera;


    private ViewPager2 bannerViewPager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int currentPage = 0;
    private List<Integer> bannerImages;
    DatabaseHelper dbHelper;

    Button button;

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

        favouriteContainer = findViewById(R.id.favouriteContainer);
        laptopContainer = findViewById(R.id.laptopContainer);
        phoneContainer = findViewById(R.id.phoneContainer);
        tabletContainer = findViewById(R.id.tabletContainer);
        cameraContainer = findViewById(R.id.cameraContainer);

        imgPopular = findViewById(R.id.imgPopular);
        imgLaptop = findViewById(R.id.imgLaptop);
        imgPhone = findViewById(R.id.imgPhone);
        imgTablet = findViewById(R.id.imgTablet);
        imgCamera = findViewById(R.id.imgCamera);

        button = findViewById(R.id.button);
        dbHelper = new DatabaseHelper(this);


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

        homeContainer.setOnClickListener(v -> {
            resetIcons(); // reset icon khác về outline
            homeIcon.setImageResource(R.drawable.home); // đổi icon hiện tại
            // Chuyển trang, ví dụ mở Activity HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
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

        });

        favouriteContainer.setOnClickListener(v -> {
            resetIconsCategory(); // reset icon khác về outline
            imgPopular.setImageResource(R.drawable.star_white); // đổi icon hiện tại
            favouriteContainer.setBackgroundResource(R.drawable.test1);
            // Chuyển trang, ví dụ mở Activity HomeActivity
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
        });

        laptopContainer.setOnClickListener(v -> {
            resetIconsCategory(); // reset icon khác về outline
            imgLaptop.setImageResource(R.drawable.laptop_white); // đổi icon hiện tại
            laptopContainer.setBackgroundResource(R.drawable.test1);
            // Chuyển trang, ví dụ mở Activity HomeActivity
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
        });

        phoneContainer.setOnClickListener(v -> {
            resetIconsCategory(); // reset icon khác về outline
            imgPhone.setImageResource(R.drawable.phone_white); // đổi icon hiện tại
            phoneContainer.setBackgroundResource(R.drawable.test1);
            // Chuyển trang, ví dụ mở Activity HomeActivity
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
        });
        tabletContainer.setOnClickListener(v -> {
            resetIconsCategory(); // reset icon khác về outline
            imgTablet.setImageResource(R.drawable.tablet_white); // đổi icon hiện tại
            tabletContainer.setBackgroundResource(R.drawable.test1);
            // Chuyển trang, ví dụ mở Activity HomeActivity
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
        });

        cameraContainer.setOnClickListener(v -> {
            resetIconsCategory(); // reset icon khác về outline
            imgCamera.setImageResource(R.drawable.camera_white); // đổi icon hiện tại
            cameraContainer.setBackgroundResource(R.drawable.test1);
            // Chuyển trang, ví dụ mở Activity HomeActivity
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
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

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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


    private void resetIconsCategory () {
        imgPopular.setImageResource(R.drawable.star);
        imgLaptop.setImageResource(R.drawable.laptop);
        imgPhone.setImageResource(R.drawable.phone);
        imgTablet.setImageResource(R.drawable.tablet);
        imgCamera.setImageResource(R.drawable.camera);

        favouriteContainer.setBackgroundResource(R.drawable.test2);
        laptopContainer.setBackgroundResource(R.drawable.test2);
        phoneContainer.setBackgroundResource(R.drawable.test2);
        tabletContainer.setBackgroundResource(R.drawable.test2);
        cameraContainer.setBackgroundResource(R.drawable.test2);
    }
}

