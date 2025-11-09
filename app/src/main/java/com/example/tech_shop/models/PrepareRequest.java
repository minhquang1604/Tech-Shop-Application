package com.example.tech_shop.models;

import java.util.List;

public class PrepareRequest {
    private List<PrepareItem> items;

    public PrepareRequest(List<PrepareItem> items) {
        this.items = items;
    }

    public List<PrepareItem> getItems() { return items; }
}
