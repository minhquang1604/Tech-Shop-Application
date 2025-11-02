package com.example.tech_shop.models;

public class AddToCartRequest {
    private String productId;
    private int quantity;

    public AddToCartRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
