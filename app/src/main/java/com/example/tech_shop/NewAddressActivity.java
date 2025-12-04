package com.example.tech_shop;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.ReceiveInfo;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAddressActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtCity, edtStreet;
    private AppCompatButton btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_address);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtCity = findViewById(R.id.edtCity);
        edtStreet = findViewById(R.id.edtStreet);
        btnComplete = findViewById(R.id.btnComplete);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnComplete.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String city = edtCity.getText().toString().trim();
            String street = edtStreet.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
                    TextUtils.isEmpty(city) || TextUtils.isEmpty(street)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = city + ", " + street;
            ReceiveInfo receiveInfo = new ReceiveInfo(name, phone, fullAddress);

            addReceiveInfo(receiveInfo);
        });
    }

    private void addReceiveInfo(ReceiveInfo receiveInfo) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.addReceiveInfo(receiveInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewAddressActivity.this, "Địa chỉ đã được thêm", Toast.LENGTH_SHORT).show();
                    // Trả kết quả về Activity trước nếu cần
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(NewAddressActivity.this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(NewAddressActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
