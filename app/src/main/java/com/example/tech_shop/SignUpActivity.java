package com.example.tech_shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.OkHttpClient;

public class SignUpActivity extends AppCompatActivity {

    TextView tvLogin;
    EditText edtUsername, edtEmail, edtPassword, edtCPassword;
    Button btnSignup;
    OkHttpClient client = new OkHttpClient();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        tvLogin = findViewById(R.id.tvLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtCPassword = findViewById(R.id.edtCPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            String cpassword = edtCPassword.getText().toString();
            signupUser(username, email, password, cpassword);

        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
        });

        // Mặc định: ẩn mật khẩu
        edtPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        // Gán icon con mắt (ẩn)
        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);

        edtPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // index 0=left,1=top,2=right,3=bottom
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtPassword.getRight()
                        - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    // Toggle password visibility
                    if (edtPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                        // Hiện mật khẩu
                        edtPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
                    } else {
                        // Ẩn mật khẩu
                        edtPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                    }

                    // Giữ con trỏ ở cuối
                    edtPassword.setSelection(edtPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        // Mặc định: ẩn mật khẩu
        edtCPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        // Gán icon con mắt (ẩn)
        edtCPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);

        edtCPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // index 0=left,1=top,2=right,3=bottom
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtCPassword.getRight()
                        - edtCPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    // Toggle password visibility
                    if (edtCPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                        // Hiện mật khẩu
                        edtCPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                        edtCPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
                    } else {
                        // Ẩn mật khẩu
                        edtCPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        edtCPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                    }

                    // Giữ con trỏ ở cuối
                    edtCPassword.setSelection(edtCPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

    }

    private void signupUser(String username, String email, String password, String cpassword) {
        if (!password.equals(cpassword)) {
            Toast.makeText(this, "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://apibackend.runasp.net/api/Authenticate/register";
//        String url = "http://techshop.runasp.net/";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("username", username);
            json.put("password", password);
            json.put("confirmPassword", cpassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SignUpActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", responseData);

                    // 1. Gửi email xác thực ngay sau khi đăng ký thành công
                    sendVerificationEmail(email);

                    runOnUiThread(() ->
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show());

                    Intent intent = new Intent(SignUpActivity.this, VerifyActivity.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void sendVerificationEmail(String email) {
        String url = "http://apibackend.runasp.net/api/Authenticate/EmailVerify/Send";

        // Body của request chỉ là chuỗi email, cần được bọc trong dấu ngoặc kép
        // Nếu API mong đợi chuỗi JSON đơn giản ("email@example.com")
        String jsonString = "\"" + email + "\"";

        RequestBody body = RequestBody.create(
                jsonString,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Xử lý khi kết nối thất bại
                Log.e("API_EMAIL", "Lỗi gửi email xác thực: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(SignUpActivity.this, "Lỗi kết nối khi gửi email", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Xử lý kết quả trả về
                if (response.isSuccessful()) {
                    Log.d("API_EMAIL", "Gửi email xác thực thành công.");
                    // Optional: Thông báo cho người dùng biết email đã được gửi
                    runOnUiThread(() ->
                            Toast.makeText(SignUpActivity.this, "Vui lòng kiểm tra email để xác thực!", Toast.LENGTH_LONG).show());
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Không có thông báo lỗi";
                    Log.e("API_EMAIL", "Gửi email xác thực thất bại: " + response.code() + ", Body: " + errorBody);
                    runOnUiThread(() ->
                            Toast.makeText(SignUpActivity.this, "Không thể gửi email xác thực. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }


}