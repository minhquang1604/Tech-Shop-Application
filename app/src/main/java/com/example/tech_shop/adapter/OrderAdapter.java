package com.example.tech_shop.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.R;
import com.example.tech_shop.RateProductActivity;
import com.example.tech_shop.api.ApiService;
import com.example.tech_shop.api.RetrofitClient;
import com.example.tech_shop.models.Order;
import com.example.tech_shop.models.OrderItem;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> orders;
    private final Activity activity; // Truyền Activity thay vì Context

    public OrderAdapter(Activity activity, List<Order> orders) {
        this.activity = activity;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderHeader.setText("TechShop - " + order.getStatus());

        // Set adapter cho RecyclerView con
        List<OrderItem> items = order.getItems();
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(items);
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvOrderItems.setAdapter(checkoutAdapter);

        // Hiển thị tổng tiền
        holder.tvTotalAmount.setText("Total: " + String.format("%,.0f₫", order.getTotalAmount()));

        // Ẩn/hiện nút theo trạng thái
        if ("Pending".equals(order.getStatus())) { // To Pay
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnReview.setVisibility(View.GONE);
        } else if ("To rate".equals(order.getStatus())) { // To Rate
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnReview.setVisibility(View.VISIBLE);
        } else { // Các trạng thái khác
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnReview.setVisibility(View.GONE);
        }

        // Cancel order
        holder.btnCancelOrder.setOnClickListener(v -> cancelOrder(order.getOrderID(), position));

        // Open review
        holder.btnReview.setOnClickListener(v -> {
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                String productId = order.getItems().get(0).getProductID(); // sản phẩm đầu tiên của đơn
                String orderId = order.getOrderID(); // orderId

                Log.d("OrderAdapter", "Opening review for productId: " + productId + ", orderId: " + orderId);

                if (productId != null && !productId.isEmpty() && orderId != null && !orderId.isEmpty()) {
                    Intent intent = new Intent(activity, RateProductActivity.class);
                    intent.putExtra("productId", productId);
                    intent.putExtra("orderId", orderId); // thêm orderId
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Product ID or Order ID is missing!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "No product to review", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderHeader, tvTotalAmount;
        RecyclerView rvOrderItems;
        Button btnCancelOrder, btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderHeader = itemView.findViewById(R.id.tvOrderHeader);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }

    private void cancelOrder(String orderId, int position) {
        ApiService apiService = RetrofitClient.getClient(activity).create(ApiService.class);
        Call<Void> call = apiService.cancelOrder(orderId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    orders.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orders.size());
                    Log.d("OrderAdapter", "Order canceled: " + orderId);
                } else {
                    Log.e("OrderAdapter", "Failed to cancel order: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("OrderAdapter", "Error canceling order", t);
            }
        });
    }
}
