package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.tech_shop.adapter.ProductWishlistAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.ProductWishlist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishListActivity extends AppCompatActivity {
    private ShapeableImageView homeIcon, heartIcon, notifyIcon, profileIcon;
    private FrameLayout homeContainer, heartContainer, notifyContainer, profileContainer;
    private TextView tvCartBadge;
    private ImageButton btnCart;

    private RecyclerView recyclerView;
    private List<ProductWishlist> productsWishlist;
    private ProductWishlistAdapter wishlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wish_list);

        recyclerView = findViewById(R.id.rvProducts);
        homeIcon = findViewById(R.id.r9o6itaym1mt);
        heartIcon = findViewById(R.id.r9jfdv7j60o);
        notifyIcon = findViewById(R.id.r5yyduajv9vh);
        profileIcon = findViewById(R.id.rdgr7gp7q0jv);
        homeContainer = findViewById(R.id.homeContainer);
        heartContainer = findViewById(R.id.heartContainer);
        notifyContainer = findViewById(R.id.notifyContainer);
        profileContainer = findViewById(R.id.profileContainer);
        btnCart = findViewById(R.id.btnCart);
        tvCartBadge = findViewById(R.id.tvCartBadge);

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(WishListActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.btnSort).setOnClickListener(v -> showSortOptions());

        // Setup RecyclerView
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        //Load dữ liệu wishlist từ API
        loadWishlist();

        //Load số lượng sản phẩm trong giỏ hàng
        loadCartCount();

        // Navigation bottom bar
        setupBottomNavigation();
    }

    private void loadWishlist() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<ProductWishlist>> call = apiService.getProductsWishlist();

        call.enqueue(new Callback<List<ProductWishlist>>() {
            @Override
            public void onResponse(Call<List<ProductWishlist>> call, Response<List<ProductWishlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productsWishlist = response.body();
                    wishlistAdapter = new ProductWishlistAdapter(WishListActivity.this, productsWishlist);
                    recyclerView.setAdapter(wishlistAdapter);
                } else {
                    showCustomToast("Không thể tải danh sách yêu thích", null, R.drawable.error);
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProductWishlist>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
                showCustomToast("Lỗi kết nối máy chủ", null, R.drawable.error);
            }
        });
    }

    private void showSortOptions() {
        if (productsWishlist == null || productsWishlist.isEmpty()) {
            showCustomToast("Không có sản phẩm để sắp xếp", null, R.drawable.error);
            return;
        }

        String[] sortOptions = {"Giá tăng dần", "Giá giảm dần", "Tên A-Z", "Tên Z-A"};

        new AlertDialog.Builder(this)
                .setTitle("Chọn cách sắp xếp")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Collections.sort(productsWishlist, (p1, p2) -> Double.compare(p1.getUnitPrice(), p2.getUnitPrice()));
                            break;
                        case 1:
                            Collections.sort(productsWishlist, (p1, p2) -> Double.compare(p2.getUnitPrice(), p1.getUnitPrice()));
                            break;
                        case 2:
                            Collections.sort(productsWishlist, (p1, p2) -> p1.getProductName().compareToIgnoreCase(p2.getProductName()));
                            break;
                        case 3:
                            Collections.sort(productsWishlist, (p1, p2) -> p2.getProductName().compareToIgnoreCase(p1.getProductName()));
                            break;
                    }
                    wishlistAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadCartCount() {
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

    private void setupBottomNavigation() {
        homeContainer.setOnClickListener(v -> {
            resetIcons();
            homeIcon.setImageResource(R.drawable.home);
            startActivity(new Intent(this, HomeActivity.class));
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
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void resetIcons() {
        homeIcon.setImageResource(R.drawable.home_outline);
        heartIcon.setImageResource(R.drawable.heart_outline);
        notifyIcon.setImageResource(R.drawable.notifications_outline);
        profileIcon.setImageResource(R.drawable.person_outline);
    }

    // Hàm custom Toast cho nhiều mục đích
    private void showCustomToast(String message, String subMessage, int iconResId) {
        // Inflate layout
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);  // Không cần root ViewGroup

        // Cập nhật main message
        TextView textView = customToastView.findViewById(R.id.text_message);
        textView.setText(message);

        // Cập nhật sub-message nếu có
        TextView subTextView = customToastView.findViewById(R.id.text_sub_message);
        if (subMessage != null && !subMessage.isEmpty()) {
            subTextView.setText(subMessage);
            subTextView.setVisibility(View.VISIBLE);
        }

        // Cập nhật icon
        ImageView iconView = customToastView.findViewById(R.id.icon_toast);
        iconView.setImageResource(iconResId);

        // Tạo và show Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 1000);  // Vị trí giống hình
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }

    // Overload nếu không cần subMessage và dùng icon default
    private void showCustomToast(String message) {
        showCustomToast(message, null, R.drawable.check);  // Default success icon
    }

    // Overload nếu không cần subMessage nhưng thay icon
    private void showCustomToast(String message, String subMessage) {
        showCustomToast(message, subMessage, R.drawable.check);
    }
}
