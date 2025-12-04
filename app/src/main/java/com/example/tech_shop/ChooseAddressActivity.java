package com.example.tech_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.AddressAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.ReceiveInfo;
import com.example.tech_shop.models.UserProfileResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppCompatButton btnAddAdress, btnSelectAddress;
    private AddressAdapter adapter;
    private List<ReceiveInfo> addressList = new ArrayList<>();
    private ReceiveInfo selectedAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_address);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddAdress = findViewById(R.id.btnAddAdress);
        btnSelectAddress = findViewById(R.id.btnSelectAddress);

        adapter = new AddressAdapter(addressList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Khi click vào item RecyclerView, đánh dấu là địa chỉ được chọn
        adapter.setOnItemClickListener(info -> selectedAddress = info);

        fetchAddresses();

        // Thêm địa chỉ mới
        btnAddAdress.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAddressActivity.this, NewAddressActivity.class);
            startActivity(intent);
        });

        // Chọn địa chỉ đã đánh dấu
        btnSelectAddress.setOnClickListener(v -> {
            if (selectedAddress != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", selectedAddress.getName());
                resultIntent.putExtra("phone", selectedAddress.getPhone());
                resultIntent.putExtra("address", selectedAddress.getAddress());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // Lấy danh sách địa chỉ từ API
    private void fetchAddresses() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getProfile("Bearer " + getToken()).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addressList.clear();
                    addressList.addAll(response.body().getReceiveInfo());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChooseAddressActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(ChooseAddressActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lấy token lưu trong SharedPreferences
    private String getToken() {
        return getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("token", "");
    }
}
