package com.example.tech_shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tech_shop.R;
import com.example.tech_shop.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductFlashSaleAdapter extends RecyclerView.Adapter<ProductFlashSaleAdapter.FlashSaleViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductFlashSaleAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public FlashSaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flash_sale, parent, false);
        return new FlashSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashSaleViewHolder holder, int position) {
        Product product = productList.get(position);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(product.getPrice());
        holder.textPrice.setText(formattedPrice + "₫");



        // Load image (nếu có link)
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context).load(product.getImage()).into(holder.imageProduct);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class FlashSaleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textPrice;

        public FlashSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textPrice = itemView.findViewById(R.id.textPrice);
        }
    }
}
