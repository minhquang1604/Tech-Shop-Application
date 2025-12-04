//package com.example.tech_shop.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.tech_shop.models.Address;
//
//import java.util.List;
//
//public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
//
//    private List<Address> list;
//    private int selectedIndex = -1;
//    private OnAddressSelected listener;
//
//    public interface OnAddressSelected {
//        void onSelected(Address address);
//    }
//
//    public AddressAdapter(List<Address> list, OnAddressSelected listener) {
//        this.list = list;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_address, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
//        Address a = list.get(i);
//
//        h.name.setText(a.getName() + " (" + a.getPhone() + ")");
//        h.address.setText(a.getAddress());
//        h.checkBox.setChecked(i == selectedIndex);
//
//        h.itemView.setOnClickListener(v -> {
//            selectedIndex = i;
//            notifyDataSetChanged();
//            listener.onSelected(a);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        CheckBox checkBox;
//        TextView name, address;
//
//        public ViewHolder(View v) {
//            super(v);
//            checkBox = v.findViewById(R.id.cbSelect);
//            name = v.findViewById(R.id.tvName);
//            address = v.findViewById(R.id.tvAddress);
//        }
//    }
//}
