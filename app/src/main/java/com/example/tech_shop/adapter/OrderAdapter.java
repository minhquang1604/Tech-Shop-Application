package com.example.tech_shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.R;
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
    private final Context context;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
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
        holder.tvOrderHeader.setText("TechShop" + " - " + order.getStatus());

        // Set adapter cho RecyclerView con
        List<OrderItem> items = order.getItems();
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(items);
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvOrderItems.setAdapter(checkoutAdapter);

        // Hiển thị tổng tiền
        holder.tvTotalAmount.setText("Total: " + String.format("%,.0f₫", order.getTotalAmount()));

        holder.btnCancelOrder.setOnClickListener(v -> {
            cancelOrder(order.getOrderID(), position, holder.itemView);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderHeader, tvTotalAmount;
        RecyclerView rvOrderItems;
        Button btnCancelOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderHeader = itemView.findViewById(R.id.tvOrderHeader);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }
    }

    // Phương thức gọi API hủy đơn
    // Trong OrderAdapter
    private void cancelOrder(String orderId, int position, View itemView) {
        ApiService apiService = RetrofitClient.getClient(itemView.getContext()).create(ApiService.class);
        Call<Void> call = apiService.cancelOrder(orderId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa đơn khỏi danh sách và cập nhật RecyclerView
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
