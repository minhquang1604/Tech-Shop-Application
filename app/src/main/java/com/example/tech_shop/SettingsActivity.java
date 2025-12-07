package com.example.tech_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.PersonalInfoSimpleRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    Button btnLogOut;
    ImageButton btnBack;
    LinearLayout rowAddress, rowUser, rowPassword;
    TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        btnLogOut = findViewById(R.id.btnLogOut);
        btnBack = findViewById(R.id.btnBack);
        rowAddress = findViewById(R.id.rowAddress);
        rowUser = findViewById(R.id.rowUser);
        rowPassword = findViewById(R.id.rowPassword);
        tvUsername = findViewById(R.id.tvUsername);

        btnBack.setOnClickListener(v -> finish());

        loadUserProfile();

        btnLogOut.setOnClickListener(v -> {
            //Xóa dữ liệu SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Xóa toàn bộ dữ liệu (token, username, isLoggedIn)
            editor.apply();

            //Chuyển về màn hình đăng nhập
            Intent intent = new Intent(SettingsActivity.this, LogInActivity.class);
            startActivity(intent);
            finish(); // đóng HomeActivity để không quay lại bằng nút Back
        });

        rowAddress.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChooseAddressActivity.class);
            startActivity(intent);
        });

        rowUser.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, InformationActivity.class);
            startActivity(intent);
        });

        rowPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

    }

    private void loadUserProfile() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getProfileSimple().enqueue(new Callback<PersonalInfoSimpleRequest>() {
            @Override
            public void onResponse(Call<PersonalInfoSimpleRequest> call, Response<PersonalInfoSimpleRequest> response) {
                if (response.isSuccessful() && response.body() != null) {

                    PersonalInfoSimpleRequest user = response.body();

                    // ⚡ Set username lên TextView
                    tvUsername.setText(user.getUsername());
                }
            }

            @Override
            public void onFailure(Call<PersonalInfoSimpleRequest> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}