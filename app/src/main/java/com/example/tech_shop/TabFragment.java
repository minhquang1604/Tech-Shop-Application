package com.example.tech_shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.OrderAdapter;
import com.example.tech_shop.models.Order;

import java.util.ArrayList;

public class TabFragment extends Fragment {

    private static final String ARG_ORDERS = "orders";
    private ArrayList<Order> orders;

    public static TabFragment newInstance(ArrayList<Order> orders) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDERS, orders);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        orders = (ArrayList<Order>) (getArguments() != null ?
                getArguments().getSerializable(ARG_ORDERS) : new ArrayList<>());

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new OrderAdapter(getContext(), orders));


        // Set margin 16dp
        int marginInPx = (int) (16 * getResources().getDisplayMetrics().density);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
        );
        params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
        recyclerView.setLayoutParams(params);

        return recyclerView;
    }

}
