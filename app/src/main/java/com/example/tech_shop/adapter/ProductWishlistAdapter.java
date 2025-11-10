package com.example.tech_shop.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tech_shop.ProductDetailActivity;
import com.example.tech_shop.R;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.AddToCartRequest;
import com.example.tech_shop.models.ProductWishlist;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductWishlistAdapter extends RecyclerView.Adapter<ProductWishlistAdapter.ViewHolder> {

    private Context context;
    private List<ProductWishlist> productList;

    public ProductWishlistAdapter(Context context, List<ProductWishlist> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_product_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductWishlist product = productList.get(position);

        // Load ảnh
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context).load(product.getImage()).into(holder.imageProduct);
        }

        // Set tên & giá
        holder.textName.setText(product.getProductName());

        // Format giá theo VNĐ
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = "₫" + formatter.format(product.getUnitPrice());
        holder.textPrice.setText(formattedPrice);

        holder.btnDelete.setOnClickListener(v -> {
            removeFromWishlist(product.getProductId(), position, holder.itemView);
        });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            context.startActivity(intent);
        });

        holder.btnCart.setOnClickListener(v -> {
            String productId = product.getProductId();

            if (productId == null || productId.isEmpty()) {
                Log.e("Cart", "Product ID is null!");
                Toast.makeText(context, "Lỗi: không tìm thấy mã sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
            AddToCartRequest request = new AddToCartRequest(productId, 1);

            apiService.addToCart(request).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Cart", "Added: " + response.body());
                        String message = response.body().get("message").toString();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        // Dùng vị trí hiện tại an toàn
                        int currentPosition = holder.getBindingAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            removeFromWishlist(productId, currentPosition, holder.itemView);
                        }
                    } else {
                        Log.e("Cart", "Add failed: " + response.code());
                        Toast.makeText(context, "Không thể thêm sản phẩm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e("Cart", "Error: " + t.getMessage());
                    Toast.makeText(context, "Lỗi mạng khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName, textPrice;
        ImageButton btnCart, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            btnCart = itemView.findViewById(R.id.btnCart);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void removeFromWishlist(String productId, int position, View itemView) {
        ApiService apiService = RetrofitClient.getClient(itemView.getContext()).create(ApiService.class);
        Call<Void> call = apiService.removeProductFromWishlist(productId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa item khỏi list và cập nhật RecyclerView
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                    Toast.makeText(context, "Đã xóa sản phẩm khỏi Wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("WishlistAdapter", "Failed to remove product: " + response.code());
                    Toast.makeText(context, "Không thể xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("WishlistAdapter", "Error removing product", t);
                Toast.makeText(context, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });


    }


}



