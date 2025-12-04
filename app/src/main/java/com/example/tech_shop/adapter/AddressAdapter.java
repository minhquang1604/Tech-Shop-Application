package com.example.tech_shop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.models.Address;
import com.example.tech_shop.models.ReceiveInfo;

import com.example.tech_shop.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<ReceiveInfo> addressList;
    private int selectedPosition = -1; // vị trí được chọn
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ReceiveInfo info);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AddressAdapter(List<ReceiveInfo> addressList) {
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        ReceiveInfo info = addressList.get(position);
        holder.txtName.setText(info.getName());
        holder.txtPhone.setText(info.getPhone());
        holder.txtAddress.setText(info.getAddress());
        holder.radioButton.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) listener.onItemClick(info);
        });

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            if (listener != null) listener.onItemClick(info);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPhone, txtAddress;
        RadioButton radioButton;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
