package com.example.tech_shop.models;

public class PrepareItem {
    private String productId;
    private int quantity;

    public PrepareItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
