package com.example.tech_shop;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tech_shop.adapter.CheckoutAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Order;
import com.example.tech_shop.models.OrderItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RateProductActivity extends AppCompatActivity {

    private static final String TAG = "RateProductActivity";

    RatingBar ratingBar;
    EditText etComment;
    Button btnSubmit, btnAddPhoto, btnAddVideo;
    ImageButton btnBack;

    RecyclerView rvProducts;
    CheckoutAdapter adapter;

    List<Uri> mediaUris = new ArrayList<>();
    String productId;
    String orderId;

    ActivityResultLauncher<String> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_product);

        productId = getIntent().getStringExtra("productId");
        orderId = getIntent().getStringExtra("orderId");

        Log.d(TAG, "Received productId: " + productId + ", orderId: " + orderId);

        if (productId == null || productId.isEmpty() || orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Product ID or Order ID missing!", Toast.LENGTH_SHORT).show();
            finish();
        }

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etReview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddVideo = findViewById(R.id.btnAddVideo);
        btnBack = findViewById(R.id.btnBack);
        rvProducts = findViewById(R.id.rvProducts); // Thêm RecyclerView trong layout

        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());
        btnAddPhoto.setOnClickListener(v -> pickMediaLauncher.launch("image/*"));
        btnAddVideo.setOnClickListener(v -> pickMediaLauncher.launch("video/*"));
        btnSubmit.setOnClickListener(v -> submitReview());

        // Load sản phẩm từ orderId
        fetchOrderProducts(orderId);

        // ActivityResultLauncher chọn media
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    mediaUris.clear();
                    if (uris != null && !uris.isEmpty()) {
                        mediaUris.addAll(uris);
                        Log.d(TAG, "Selected media count: " + mediaUris.size());
                        updateMediaButtonText();
                    } else {
                        mediaUris.clear();
                        updateMediaButtonText();
                    }
                }
        );
    }

    private void fetchOrderProducts(String orderId) {
        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);
        api.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    List<OrderItem> products = order.getItems();

                    // Chỉ hiển thị sản phẩm cần review (theo productId)
                    for (OrderItem item : products) {
                        if (!item.getProductID().equals(productId)) {
                            products.remove(item);
                            break;
                        }
                    }

                    adapter = new CheckoutAdapter(products);
                    rvProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Failed to fetch order products", t);
            }
        });
    }

    private void updateMediaButtonText() {
        int photoCount = 0;
        int videoCount = 0;
        for (Uri uri : mediaUris) {
            String type = getContentResolver().getType(uri);
            if (type != null) {
                if (type.startsWith("image")) photoCount++;
                else if (type.startsWith("video")) videoCount++;
            }
        }

        btnAddPhoto.setText(photoCount > 0 ? photoCount + " selected" : "Add Photo");
        btnAddVideo.setText(videoCount > 0 ? videoCount + " selected" : "Add Video");
    }

    private MultipartBody.Part uriToPart(Uri uri) {
        try {
            if (uri == null) return null;

            InputStream in = getContentResolver().openInputStream(uri);
            if (in == null) return null;

            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            in.close();

            String mime = getContentResolver().getType(uri);
            if (mime == null) mime = "application/octet-stream";

            RequestBody fileBody = RequestBody.create(bytes, MediaType.parse(mime));
            Log.d(TAG, "Converted URI to Multipart: " + uri.toString());
            return MultipartBody.Part.createFormData("MediaFiles", "file", fileBody);

        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to Multipart", e);
            return null;
        }
    }

    private void submitReview() {
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Product ID missing!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Product ID is null or empty!");
            return;
        }

        int stars = (int) ratingBar.getRating();
        String comment = etComment.getText().toString();

        Log.d(TAG, "Submitting review: stars=" + stars + ", comment=" + comment + ", mediaCount=" + mediaUris.size());

        RequestBody rbProductId = RequestBody.create(productId, MediaType.parse("text/plain"));
        RequestBody rbStars = RequestBody.create(String.valueOf(stars), MediaType.parse("text/plain"));
        RequestBody rbComment = RequestBody.create(comment, MediaType.parse("text/plain"));

        List<MultipartBody.Part> mediaParts = new ArrayList<>();
        for (Uri uri : mediaUris) {
            MultipartBody.Part part = uriToPart(uri);
            if (part != null) mediaParts.add(part);
        }

        ApiService api = RetrofitClient.getClient(this).create(ApiService.class);
        Call<Void> call = api.createReview(rbProductId, rbStars, rbComment, mediaParts);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Review submitted successfully");
                    showCustomToast("Review submitted!", null, R.drawable.check);
                    // Chuyển sang ProfileActivity
                    Intent intent = new Intent(RateProductActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // đóng RateProductActivity
                } else {
                    Log.e(TAG, "Upload failed, code: " + response.code());
                    showCustomToast("Upload failed", null, R.drawable.error);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                Toast.makeText(RateProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        });
    }
}
