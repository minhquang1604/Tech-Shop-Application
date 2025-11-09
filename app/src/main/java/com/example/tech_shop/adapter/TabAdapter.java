package com.example.tech_shop.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tech_shop.TabFragment;
import com.example.tech_shop.models.Order;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStateAdapter {

    private final List<Order> allOrders;

    public TabAdapter(@NonNull FragmentActivity fragmentActivity, List<Order> allOrders) {
        super(fragmentActivity);
        this.allOrders = allOrders;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TabFragment.newInstance(new ArrayList<>(filterOrders(position)));
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    private List<Order> filterOrders(int position) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            switch (position) {
                case 0:
                    if ("Pending".equals(order.getStatus())) filtered.add(order);
                    break;
                case 1: // To Ship -> NotConfirm

                    break;
                case 2: // To Receive -> Shipped

                    break;
                case 3: // To Rate -> Received

                    break;
            }
        }
        return filtered;
    }
}
