package com.example.tech_shop;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tech_shop.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PurchaseActivity extends AppCompatActivity {

    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Adapter tạm (chưa có dữ liệu, chỉ hiển thị text)
        viewPager.setAdapter(new TabAdapter(this));


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("To Pay"); break;
                case 1: tab.setText("To Ship"); break;
                case 2: tab.setText("To Receive"); break;
                case 3: tab.setText("To Rate"); break;
            }
        }).attach();

        // ✅ Nhận chỉ số tab được gửi từ ProfileActivity
        int tabIndex = getIntent().getIntExtra("tab_index", 0);

        // ✅ Mở đúng tab tương ứng
        viewPager.setCurrentItem(tabIndex, false);
    }
}