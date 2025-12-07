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
import java.util.Locale;
import java.util.Map;

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

        // Title
        holder.tvTitle.setText(item.getTitle());

        // Convert body map → text
        StringBuilder bodyText = new StringBuilder();
        Map<String, String> bodyMap = item.getBody();

        if (bodyMap != null) {
            for (String key : bodyMap.keySet()) {
                bodyText.append(key).append(": ").append(bodyMap.get(key)).append("\n");
            }
        }
        holder.tvMessage.setText(bodyText.toString().trim());

        // Format createdAt
        holder.tvTime.setText(formatTime(item.getCreatedAt()));
    }

    // Sử dụng SimpleDateFormat để hỗ trợ minSdk=24
    private String formatTime(String isoDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = input.parse(isoDate);

            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            return output.format(date);
        } catch (ParseException e) {
            return isoDate;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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
