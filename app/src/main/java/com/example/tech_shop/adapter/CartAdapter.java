package com.example.tech_shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tech_shop.R;
import com.example.tech_shop.models.CartItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnTotalChangeListener {
        void onTotalChange(long total);
    }

    private List<CartItem> cartItems;
    private Context context;
    private OnTotalChangeListener listener;

    // Danh sách ID sản phẩm được chọn
    private Set<String> selectedItems = new HashSet<>();

    public CartAdapter(List<CartItem> cartItems, Context context, OnTotalChangeListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(String.format("%,dđ", item.getUnitPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImage()).into(holder.imgProduct);

        // Set trạng thái checkbox dựa vào danh sách selectedItems
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(selectedItems.contains(item.getProductId()));

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(item.getProductId());
            } else {
                selectedItems.remove(item.getProductId());
            }
            updateTotal();
        });

        // Xử lý nút cộng/trừ số lượng
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            updateTotal();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                updateTotal();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // chỉ tính tổng cho sản phẩm được tick
    private void updateTotal() {
        long total = 0;
        for (CartItem item : cartItems) {
            if (selectedItems.contains(item.getProductId())) {
                total += (long) item.getQuantity() * item.getUnitPrice();
            }
        }
        listener.onTotalChange(total);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvQuantity;
        AppCompatButton btnMinus, btnPlus;
        CheckBox cbSelect;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }

    public CartItem getItemAt(int position) {
        return cartItems.get(position);
    }

    public void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        updateTotal(); // gọi lại hàm tính tổng tiền
    }


}
