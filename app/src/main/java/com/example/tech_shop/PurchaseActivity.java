package com.example.tech_shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.TabAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Order;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseActivity extends AppCompatActivity {

    ImageButton btnBack;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Lấy ApiService từ RetrofitClient
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Gọi API
        apiService.getMyOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();

                    // Log toàn bộ dữ liệu trả về
                    Log.d("PurchaseActivity", "Orders from server: " + orders.toString());

                    // Hoặc log từng order chi tiết
                    for (Order order : orders) {
                        Log.d("PurchaseActivity", "OrderID: " + order.getOrderID()
                                + ", Status: " + order.getStatus()
                                + ", Total: " + order.getTotalAmount());
                    }

                    // Gắn adapter với dữ liệu thực
                    TabAdapter adapter = new TabAdapter(PurchaseActivity.this, orders);
                    viewPager.setAdapter(adapter);

                    // Kết nối TabLayout và ViewPager2
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        switch (position) {
                            case 0: tab.setText("To Pay"); break;
                            case 1: tab.setText("To Ship"); break;
                            case 2: tab.setText("To Receive"); break;
                            case 3: tab.setText("To Rate"); break;
                        }
                    }).attach();

                    // Mở tab được chọn
                    int tabIndex = getIntent().getIntExtra("tab_index", 0);
                    viewPager.setCurrentItem(tabIndex, false);
                } else {
                    // Log nếu response không thành công
                    Log.d("PurchaseActivity", "Response not successful. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                t.printStackTrace();
                Log.d("PurchaseActivity", "API call failed: " + t.getMessage());
            }
        });
    }
}
