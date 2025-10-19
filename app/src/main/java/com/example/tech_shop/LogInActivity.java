package com.example.tech_shop;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogInActivity extends AppCompatActivity {

    TextView tvSignup;
    EditText edtEmail, edtPassword;
    Button btnLogin;
    OkHttpClient client = new OkHttpClient();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        tvSignup = findViewById(R.id.tvLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(v -> {
            String username = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            loginUser(username, password);
        });

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
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

    }

    private void loginUser(String username, String password) {
        String url = "http://apibackend.runasp.net/api/Authenticate/login";

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
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
                        Toast.makeText(LogInActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", responseData);

                    runOnUiThread(() ->
                            Toast.makeText(LogInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show());

                    // chuyển sang màn hình HomeActivity
                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LogInActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}