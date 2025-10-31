package com.example.tech_shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

//public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
//
//    private Context context;
//    private List<CartItem> cartList;
//
//    public CartAdapter(Context context, List<CartItem> cartList) {
//        this.context = context;
//        this.cartList = cartList;
//    }
//
//    @NonNull
//    @Override
//    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
//        return new CartViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
//        CartItem item = cartList.get(position);
//        holder.tvName.setText(item.getName());
//        holder.tvColor.setText(item.getColor());
//        holder.tvPrice.setText(item.getPrice());
//        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
//        holder.imgProduct.setImageResource(item.getImageResId());
//    }
//
//    @Override
//    public int getItemCount() {
//        return cartList.size();
//    }
//
//    public static class CartViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgProduct;
//        TextView tvName, tvColor, tvPrice, tvQuantity;
//        Button btnMinus, btnPlus;
//        CheckBox cbSelect;
//
//        public CartViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgProduct = itemView.findViewById(R.id.imgProduct);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvColor = itemView.findViewById(R.id.tvColor);
//            tvPrice = itemView.findViewById(R.id.tvPrice);
//            tvQuantity = itemView.findViewById(R.id.tvQuantity);
//            btnMinus = itemView.findViewById(R.id.btnMinus);
//            btnPlus = itemView.findViewById(R.id.btnPlus);
//            cbSelect = itemView.findViewById(R.id.cbSelect);
//        }
//    }
//}
