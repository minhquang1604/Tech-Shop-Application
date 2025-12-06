package com.example.tech_shop;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Map;

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
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // ✅ Kiểm tra trạng thái đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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

        // Mặc định: ẩn mật khẩu + icon con mắt
        edtPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);

        edtPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtPassword.getRight()
                        - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    // Toggle password visibility
                    if (edtPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                        edtPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);
                    } else {
                        edtPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                        edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                    }

                    edtPassword.setSelection(edtPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        // 1️⃣ Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // get Web Client ID from Google Cloud
                .requestEmail()
                .build();




        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 2️⃣ Hook up Google button
        findViewById(R.id.btnGoogle).setOnClickListener(v -> signInWithGoogle());
    }

    private void loginUser(String username, String password) {

        String url = "http://apibackend.runasp.net/api/Authenticate/login";

        // Tạo JSON body
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
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
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(LogInActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show());
                    return;
                }

                String responseData = response.body().string();
                Log.d("API_RESPONSE", responseData);

                try {
                    JSONObject jsonRes = new JSONObject(responseData);
                    String token = jsonRes.getString("token");

                    // Lưu session
                    SharedPreferences shared = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    shared.edit()
                            .putString("token", token)
                            .putString("username", username)
                            .putBoolean("isLoggedIn", true)
                            .apply();

                    // Gửi FCM token lên server (nếu đã có)
                    FcmTokenHelper.sendTokenToServer(LogInActivity.this);

                    // Chuyển trang
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                        finish();
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            Log.d("GOOGLE_ID_TOKEN", "ID Token: " + idToken);

            if (idToken != null) {
                sendIdTokenToServer(idToken);
            } else {
                Toast.makeText(this, "Failed to get Google ID Token", Toast.LENGTH_SHORT).show();
            }

        } catch (ApiException e) {
            Log.e("GOOGLE_SIGN_IN", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendIdTokenToServer(String idToken) {
        // Replace with your backend endpoint
        String url = "http://apibackend.runasp.net/api/Authenticate/google"; // your backend URL

        // Create a JSON object with the idToken as a key-value pair
        JSONObject json = new JSONObject();
        try {
            json.put("idToken", idToken); // Wrap the idToken in a JSON object
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the request body with JSON content
        RequestBody body = RequestBody.create(
                json.toString(), // Convert JSON object to string
                MediaType.parse("application/json; charset=utf-8") // Set Content-Type to application/json
        );

        // Create the HTTP request with POST method
        Request request = new Request.Builder()
                .url(url)
                .post(body) // Send the JSON body
                .build();

        // Make the asynchronous call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Show error message if the request fails
                runOnUiThread(() ->
                        Toast.makeText(LogInActivity.this, "Failed to contact server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (response.isSuccessful()) {
                    // Log and handle successful response
                    Log.d("SERVER_RESPONSE", responseData);
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Google Sign-In success!", Toast.LENGTH_SHORT).show();

                        // Navigate to your main screen
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // Log and handle failed response
                    runOnUiThread(() ->
                            Toast.makeText(LogInActivity.this, "Login failed: " + response.code() + " " + response.message() + " " + responseData, Toast.LENGTH_SHORT).show());
                }
            }
        });



    }






}
