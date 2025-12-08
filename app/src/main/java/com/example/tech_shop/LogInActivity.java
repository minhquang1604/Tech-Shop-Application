package com.example.tech_shop;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import androidx.annotation.Nullable;

import org.json.JSONException;
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

    TextView tvSignup, tvForgotPassword;
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
        tvForgotPassword = findViewById(R.id.textView6);

        btnLogin.setOnClickListener(v -> {
            String username = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            loginUser(username, password);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
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
                        showCustomToast("Failed to contact server", null, R.drawable.error));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String token = jsonObject.getString("token"); // Lấy token từ API

                        //  Lưu token và trạng thái đăng nhập
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        runOnUiThread(() -> {
                            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    runOnUiThread(() ->
                            showCustomToast("Wrong username or password", null, R.drawable.error));
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

        }
    }


    private void sendIdTokenToServer(String idToken) {
        // Replace with your backend endpoint
        String url = "http://apibackend.runasp.net/api/Authenticate/google"; // your backend URL
//        String url = "http://techshop.runasp.net/"; // your backend URL

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
                        showCustomToast("Failed to contact server", null, R.drawable.error));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();

                if (response.isSuccessful()) {
                    Log.d("SERVER_RESPONSE", responseData);

                    try {
                        // Parse JSON response
                        JSONObject jsonObject = new JSONObject(responseData);
                        String token = jsonObject.getString("token");
                        String username = jsonObject.optString("username", "GoogleUser");

                        // Save token and user info to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("username", username);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Navigate to main/home screen
                        runOnUiThread(() -> {
                            Toast.makeText(LogInActivity.this, "Google Sign-In success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        });

                    } catch (JSONException e) {
                        Log.e("SERVER_RESPONSE", "JSON parsing error: " + e.getMessage());
                        runOnUiThread(() ->
                                Toast.makeText(LogInActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show());
                    }

                } else {
                    String errorBody = response.body() != null ? responseData : "No response body";
                    Log.e("SERVER_ERROR", "Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() ->
                            Toast.makeText(LogInActivity.this, "Login failed: " + response.code() + " " + response.message(), Toast.LENGTH_SHORT).show());
                }
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
