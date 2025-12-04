package com.example.tech_shop;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;

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

    List<Uri> mediaUris = new ArrayList<>();
    String productId;

    ActivityResultLauncher<String> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_product);

        // Lấy ID sản phẩm từ Intent
        productId = getIntent().getStringExtra("productId");
        Log.d(TAG, "Received productId: " + productId);

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etReview);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnAddVideo = findViewById(R.id.btnAddVideo);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Chọn ảnh/video
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        mediaUris.clear();
                        mediaUris.addAll(uris);
                        Log.d(TAG, "Selected media count: " + mediaUris.size());
                        for (Uri uri : mediaUris) {
                            Log.d(TAG, "Media URI: " + uri.toString());
                        }
                    } else {
                        Log.d(TAG, "No media selected");
                    }
                }
        );

        btnAddPhoto.setOnClickListener(v -> pickMediaLauncher.launch("image/*"));
        btnAddVideo.setOnClickListener(v -> pickMediaLauncher.launch("video/*"));

        btnSubmit.setOnClickListener(v -> submitReview());
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

        // Convert media
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
                    Toast.makeText(RateProductActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Upload failed, code: " + response.code());
                    Toast.makeText(RateProductActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                Toast.makeText(RateProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
