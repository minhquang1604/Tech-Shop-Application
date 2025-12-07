package com.example.tech_shop;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.tech_shop.adapter.ImageAdapter;
import com.example.tech_shop.adapter.ProductAdapter;
import com.example.tech_shop.adapter.ReviewAdapter;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.AddToCartRequest;
import com.example.tech_shop.models.AddToWishlistRequest;
import com.example.tech_shop.models.CartCountResponse;
import com.example.tech_shop.models.PrepareItem;
import com.example.tech_shop.models.PrepareRequest;
import com.example.tech_shop.models.PrepareResponse;
import com.example.tech_shop.models.Product;
import com.example.tech_shop.models.ProductDetail;
import com.example.tech_shop.models.Review;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private TextView tvPrice, tvProductName, tvImageCount, tvSeeMoreReviews;
    private ImageAdapter imageAdapter;
    private TableLayout tableSpecs;
    private ImageButton btnBack, btnCart, btnTopCart;
    RecyclerView recyclerReviews;
    ReviewAdapter reviewAdapter;
    List<Review> reviewList = new ArrayList<>();
    private TextView tvAverageRating;
    TextView tvSold, tvOldPrice, tvDiscount;
    String productId;

    private OkHttpClient sentimentClient = new OkHttpClient();
    private static final String SENTIMENT_API_KEY = "3d3edaba-0b0b-44cc-b2d9-3973642b5c3c";
    private TextView tvOverallSentiment; // hiển thị sentiment tổng quan


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvPrice = findViewById(R.id.tvPrice);
        tvProductName = findViewById(R.id.tvProductName);
        tvImageCount = findViewById(R.id.tvImageCount);
        tableSpecs = findViewById(R.id.tableSpecs);
        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        TextView tvCartBadge = findViewById(R.id.tvCartBadge);
        btnTopCart = findViewById(R.id.btnTopCart);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvSold = findViewById(R.id.tvSold);
        tvOldPrice = findViewById(R.id.tvOldPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvSeeMoreReviews = findViewById(R.id.tvSeeMoreReviews);
        TextView tvDate = findViewById(R.id.tvDate);
        tvOverallSentiment = findViewById(R.id.tvOverallSentiment);


        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        // Ngày bắt đầu: +3 ngày
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        String startDay = new SimpleDateFormat("d", Locale.getDefault()).format(calendar.getTime());
        String month = new SimpleDateFormat("M", Locale.getDefault()).format(calendar.getTime());
        // Ngày kết thúc: +3 ngày nữa (tổng cộng +6 ngày từ hiện tại)
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        String endDay = new SimpleDateFormat("d", Locale.getDefault()).format(calendar.getTime());

        String monthLabel = "Th" + month;
        String shippingDate = "Guaranteed to get by " + startDay + " " + monthLabel + " - " + endDay + " " + monthLabel;
        tvDate.setText(shippingDate);




        // Lấy ID sản phẩm từ Intent
        productId = getIntent().getStringExtra("productId");
        loadProductDetails(productId);

        // Gọi API đếm số lượng sản phẩm
        loadCartCount(tvCartBadge);

        btnBack.setOnClickListener(v -> finish());

        btnTopCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        tvSeeMoreReviews.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
            intent.putExtra("productId", productId); // truyền productId
            startActivity(intent);
            overridePendingTransition(0, 0);
        });


        btnCart.setOnClickListener(v -> {
            if (productId == null || productId.isEmpty()) {
                Log.e("Cart", "Product ID is null!");
                return;
            }

            ApiService apiService = RetrofitClient.getClient(ProductDetailActivity.this).create(ApiService.class);
            AddToCartRequest request = new AddToCartRequest(productId, 1); // Mặc định số lượng = 1

            apiService.addToCart(request).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Cart", "Added: " + response.body());
                        showCustomToast("Added item to cart", null, R.drawable.check);
                        // Cập nhật badge
                        loadCartCount(tvCartBadge);
                    } else {
                        Log.e("Cart", "Add failed: " + response.code());
                        showCustomToast("Failed to add item to cart!", null, R.drawable.error);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("Cart", "Error: " + t.getMessage());
                    Toast.makeText(ProductDetailActivity.this, "Network error!", Toast.LENGTH_SHORT).show();
                }
            });
        });


        // reviews
        recyclerReviews = findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerReviews.setAdapter(reviewAdapter);
        //Thêm gạch ngang phân cách giữa các review
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                int childCount = parent.getChildCount();

                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#DDDDDD")); // Màu xám nhạt
                paint.setStrokeWidth(2f); // Độ dày đường kẻ

                for (int i = 0; i < childCount - 1; i++) { // Không vẽ dòng cuối
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    float left = child.getLeft();
                    float right = child.getRight();
                    float y = child.getBottom() + params.bottomMargin;

                    c.drawLine(left, y, right, y, paint);
                }
            }
        };


        recyclerReviews.addItemDecoration(divider);


        loadReviews(productId);



        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Call<List<Product>> call = apiService.getProducts(100);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
                    recyclerView.setAdapter(new ProductAdapter(ProductDetailActivity.this, products));
                } else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });


        ImageButton btnWishlist = findViewById(R.id.btnWishlist);

        btnWishlist.setOnClickListener(v -> {
            if (imageAdapter == null || imageAdapter.getImages().isEmpty()) {
                Toast.makeText(ProductDetailActivity.this, "Không có ảnh sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy dữ liệu từ giao diện
            String productName = tvProductName.getText().toString();
            String image = imageAdapter.getImages().get(0); // lấy ảnh đầu tiên
            String priceText = tvPrice.getText().toString()
                    .replace("₫", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();

            long price = 0;
            try {
                price = Long.parseLong(priceText); // 👈 Dùng Long.parseLong thay vì Double
            } catch (NumberFormatException e) {
                Log.e("Wishlist", "Price parse error: " + e.getMessage());
            }


            // Tạo request
            AddToWishlistRequest request = new AddToWishlistRequest(
                    getIntent().getStringExtra("productId"),
                    productName,
                    image,
                    price
            );


            apiService.addToWishlist(request).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        showCustomToast("Added item to wishlist", null, R.drawable.check);

                    } else {
                        showCustomToast("Fail to add item to wishlist!", null, R.drawable.error);
                        Log.e("Wishlist", "Error code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    Log.e("Wishlist", "Error: " + t.getMessage());
                }
            });
        });

        Button btnBuyNow = findViewById(R.id.btnBuyNow);
        btnBuyNow.setOnClickListener(v -> showBuyNowPopup());
    }

    private void loadReviews(String productId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getReviewsByProductId(productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    List<Review> allReviews = response.body();

                    // ✅ Tính trung bình số sao (stars)
                    float totalStars = 0f;
                    for (Review r : allReviews) {
                        totalStars += r.getStars();
                    }

                    float avgStars = 0f;
                    if (!allReviews.isEmpty()) {
                        avgStars = totalStars / allReviews.size();
                    }

                    // Làm tròn 1 chữ số thập phân
                    avgStars = Math.round(avgStars * 10f) / 10f;


                    // ✅ Hiển thị kiểu: 4.9 ★ Product Ratings (29)
                    tvAverageRating.setText(avgStars + " ⭐ Product Ratings (" + allReviews.size() + ")");

                    // ✅ Giới hạn chỉ hiển thị tối đa 2 review
                    if (allReviews.size() > 2) {
                        reviewList.addAll(allReviews.subList(0, 2));
                    } else {
                        reviewList.addAll(allReviews);
                    }

                    reviewAdapter.notifyDataSetChanged();

//                    // ✅ Gửi từng review lên API sentiment
//                    for (Review r : allReviews) {
//                        analyzeReviewSentiment(r.getComment());
//                    }


                } else {
                    Log.e("API", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
            }
        });
    }

//    private void analyzeReviewSentiment(String comment) {
//        String url = "https://api.apiverve.com/v1/sentimentanalysis";
//
//        JSONObject json = new JSONObject();
//        try {
//            json.put("text", comment);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(
//                json.toString(),
//                MediaType.parse("application/json; charset=utf-8")
//        );
//
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("x-api-key", SENTIMENT_API_KEY)
//                .build();
//
//        sentimentClient.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e("SentimentAPI", "Failed: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                if (response.isSuccessful() && response.body() != null) {
//                    String responseData = response.body().string();
//                    try {
//                        JSONObject jsonObject = new JSONObject(responseData);
//                        JSONObject data = jsonObject.getJSONObject("data");
//
//                        String sentimentText = data.getString("sentimentText");
//                        double comparative = data.getDouble("comparative");
//
//                        runOnUiThread(() -> updateOverallSentimentFromApi(sentimentText, comparative));
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.e("SentimentAPI", "Error: " + response.code());
//                }
//            }
//        });
//    }
//
//    private void updateOverallSentimentFromApi(String sentimentText, double comparative) {
//        String sentence1, sentence2;
//
//        // Câu 1: tổng quan dựa vào sentimentText
//        switch (sentimentText.toLowerCase()) {
//            case "very positive":
//                sentence1 = "The product is highly praised by users.";
//                break;
//            case "positive":
//                sentence1 = "The product is positively reviewed by users.";
//                break;
//            case "very negative":
//                sentence1 = "The product is strongly criticized by users.";
//                break;
//            case "negative":
//                sentence1 = "The product is negatively reviewed by users.";
//                break;
//            default:
//                sentence1 = "The product has mixed reviews.";
//                break;
//        }
//
//
//        // Câu 2: mô tả mức độ chi tiết dựa vào comparative
//        if (comparative >= 0.6) {
//            sentence2 = "Users are extremely satisfied with this product.";
//        } else if (comparative >= 0.2) {
//            sentence2 = "Users are generally happy with this product.";
//        } else if (comparative > -0.2) {
//            sentence2 = "Users have mixed feelings about this product.";
//        } else if (comparative > -0.6) {
//            sentence2 = "Users are somewhat dissatisfied with this product.";
//        } else {
//            sentence2 = "Users are very unhappy with this product.";
//        }
//
//        tvOverallSentiment.setText(sentence1 + "\n" + sentence2);
//    }


    private void loadProductDetails(String id) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getProductDetails(id).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(Call<ProductDetail> call, Response<ProductDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetail product = response.body();

                    // Hiển thị ảnh
                    List<String> images = product.getImageURL();
                    imageAdapter = new ImageAdapter(images, ProductDetailActivity.this);
                    viewPagerImages.setAdapter(imageAdapter);
                    tvImageCount.setText("1/" + images.size());
                    viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            tvImageCount.setText((position + 1) + "/" + images.size());
                        }
                    });

                    // Hiển thị thông tin cơ bản
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    tvProductName.setText(product.getName());

                    // Kiểm tra sale
                    if (product.getSale() != null && product.getSale().isActive()) {
                        double percent = product.getSale().getPercent();
                        // Giá gốc
                        int oldPrice = (int) Math.round(product.getPrice() / (1 - percent));
                        tvOldPrice.setText(formatter.format(oldPrice) + " ₫");
                        tvOldPrice.setVisibility(View.VISIBLE);

                        // Giảm giá %
                        int discountPercent = (int) Math.round(percent * 100);
                        tvDiscount.setText("-" + discountPercent + "%");
                        tvDiscount.setVisibility(View.VISIBLE);

                        // Giá hiện tại
                        String formattedPrice = formatter.format(product.getPrice());
                        tvPrice.setText(formattedPrice + " ₫");
                    } else {
                        // Không sale
                        String formattedPrice = formatter.format(product.getPrice());
                        tvPrice.setText(formattedPrice + " ₫");
                        tvOldPrice.setVisibility(View.GONE);
                        tvDiscount.setVisibility(View.GONE);
                    }

                    // ✅ Hiển thị số lượng sản phẩm đã bán
                    int soldCount = product.getSold();
                    tvSold.setText(soldCount + " sold");

                    // ✅ Hiển thị bảng thông số kỹ thuật
                    populateSpecsTable(product.getDetail());

                } else {
                    Log.e("API_ERROR", "Response null or failed");
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }


    private void populateSpecsTable(Map<String, String> details) {
        if (details == null || details.isEmpty()) return;

        tableSpecs.removeAllViews();

        int total = details.size();
        int index = 0;

        for (Map.Entry<String, String> entry : details.entrySet()) {
            TableRow row = new TableRow(this);

            // Thiết lập layout cho hai cột
            TableRow.LayoutParams leftParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TableRow.LayoutParams rightParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);

            // Ô bên trái (key)
            TextView keyView = new TextView(this);
            keyView.setText(entry.getKey());
            keyView.setTextSize(14);
            keyView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            keyView.setBackgroundResource(R.drawable.table_cell_left);
            keyView.setPadding(24, 24, 24, 24);
            keyView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            keyView.setLayoutParams(leftParams);

            // Ô bên phải (value)
            TextView valueView = new TextView(this);
            valueView.setText(entry.getValue());
            valueView.setTextSize(14);
            valueView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
//            valueView.setBackgroundResource(R.drawable.table_cell_right);
            valueView.setPadding(24, 24, 24, 24);
            valueView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            valueView.setLayoutParams(rightParams);

            // Thêm hai ô vào hàng
            row.addView(keyView);
            row.addView(valueView);

            // Thêm hàng vào bảng
            tableSpecs.addView(row);

            index++;
        }

        // Thêm viền bo quanh toàn bảng (nếu muốn)
        tableSpecs.setBackgroundResource(R.drawable.table_border_bg);
    }

    private void loadCartCount(TextView tvCartBadge) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getCartCount().enqueue(new Callback<CartCountResponse>() {
            @Override
            public void onResponse(Call<CartCountResponse> call, Response<CartCountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getCount();
                    if (count > 0) {
                        tvCartBadge.setText(String.valueOf(count));
                        tvCartBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvCartBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvCartBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CartCountResponse> call, Throwable t) {
                tvCartBadge.setVisibility(View.GONE);
                Log.e("CartCount", "Error: " + t.getMessage());
            }
        });
    }

    private void showBuyNowPopup() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.item_buy, null);
        dialog.setContentView(view);

        // View từ layout
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvPricePopup = view.findViewById(R.id.tvPrice);
        TextView tvStockPopup = view.findViewById(R.id.tvStock);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        Button btnMinus = view.findViewById(R.id.btnMinus);
        Button btnPlus = view.findViewById(R.id.btnPlus);
        Button btnBuyNow = view.findViewById(R.id.btnBuyNow);

        // Hiển thị dữ liệu
        tvPricePopup.setText(tvPrice.getText().toString());
        tvStockPopup.setText(tvSold.getText().toString());

        Glide.with(this)
                .load(imageAdapter.getImages().get(0))
                .into(imgProduct);

        // Quantity mặc định
        tvQuantity.setText("1");
        int maxStock = 99999; // Nếu bạn có stock thật thì truyền vào

        // --- NÚT + ---
        btnPlus.setOnClickListener(v -> {
            int q = Integer.parseInt(tvQuantity.getText().toString());
            if (q < maxStock) {
                tvQuantity.setText(String.valueOf(q + 1));
            }
        });

        // --- NÚT - ---
        btnMinus.setOnClickListener(v -> {
            int q = Integer.parseInt(tvQuantity.getText().toString());
            if (q > 1) {
                tvQuantity.setText(String.valueOf(q - 1));
            }
        });

        // --- BUY NOW ---
        btnBuyNow.setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());

            // CHỈ BUY 1 SẢN PHẨM
            List<PrepareItem> items = new ArrayList<>();
            items.add(new PrepareItem(productId, quantity));  // productId phải có trong ProductDetailActivity

            PrepareRequest request = new PrepareRequest(items);
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

            apiService.prepareOrder(request).enqueue(new Callback<PrepareResponse>() {
                @Override
                public void onResponse(Call<PrepareResponse> call, Response<PrepareResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String orderId = response.body().getOrder().getOrderID();

                        // Chuyển sang checkout
                        Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                        intent.putExtra("ORDER_ID", orderId);
                        startActivity(intent);

                        dialog.dismiss();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Không thể tạo đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PrepareResponse> call, Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }


    private void showCustomToast(String message, String subMessage, int iconResId) {
        View customToastView = getLayoutInflater().inflate(R.layout.custom_toast, null);

        // Cập nhật main message
        TextView textView = customToastView.findViewById(R.id.text_message);
        textView.setText(message);

        // Cập nhật sub-message nếu có
        TextView subTextView = customToastView.findViewById(R.id.text_sub_message);
        if (subMessage != null && !subMessage.isEmpty()) {
            subTextView.setText(subMessage);
            subTextView.setVisibility(View.VISIBLE);
        } else {
            subTextView.setVisibility(View.GONE);
        }// ... phần gọi API như hiện tại

        // Cập nhật icon
        ImageView iconView = customToastView.findViewById(R.id.icon_toast);
        iconView.setImageResource(iconResId);

        // Tạo và show Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 1000);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(customToastView);
        toast.show();
    }

    // Overload không cần subMessage, mặc định icon success
    private void showCustomToast(String message) {
        showCustomToast(message, null, R.drawable.check);
    }

    // Overload không cần subMessage, có thể thay icon
    private void showCustomToast(String message, int iconResId) {
        showCustomToast(message, null, iconResId);
    }

}
