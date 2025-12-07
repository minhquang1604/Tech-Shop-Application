package com.example.tech_shop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        currentPasswordInput = findViewById(R.id.password_input);
        newPasswordInput = findViewById(R.id.confirm_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input2);
        updateButton = findViewById(R.id.update_button);

        updateButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New password and confirmation do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(currentPassword, newPassword);
        });
    }

    private void changePassword(String currentPassword, String newPassword) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("currentPassword", currentPassword);
        body.put("newPassword", newPassword);

        Call<Void> call = apiService.changePassword(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showCustomToast("Password changed successfully", null, R.drawable.check);
                } else {
                    showCustomToast("Failed to change password", null, R.drawable.error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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