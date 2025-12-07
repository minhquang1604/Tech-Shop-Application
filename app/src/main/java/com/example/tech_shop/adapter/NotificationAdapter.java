package com.example.tech_shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.R;
import com.example.tech_shop.models.NotificationItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<NotificationItem> list;

    public NotificationAdapter(Context context, List<NotificationItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationItem item = list.get(position);

        holder.tvTitle.setText(item.getTitle());

        StringBuilder sb = new StringBuilder();

        // 🔥 BODY LÀ MAP → CHỈ LẤY VALUE HỢP LỆ
        if (item.getBody() != null) {
            for (Object value : item.getBody().values()) {

                if (value == null) continue;

                String text = value.toString().trim();

                if (text.isEmpty()) continue;
                if (text.equalsIgnoreCase("string")) continue;

                sb.append(text).append("\n");
            }
        }

        // Ẩn nếu không có nội dung
        String finalText = sb.toString().trim();
        if (finalText.isEmpty()) {
            holder.tvMessage.setVisibility(View.GONE);
        } else {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(finalText);
        }

        // 🔥 FORMAT NGÀY GIỜ
        holder.tvTime.setText(formatDate(item.getCreatedAt()));
    }


    private String formatDate(String isoString) {
        // ISO: 2025-12-07T14:59:21.089Z
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date date = isoFormat.parse(isoString);
            return outFormat.format(date);
        } catch (ParseException e) {
            return isoString; // fallback
        }
    }


    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
