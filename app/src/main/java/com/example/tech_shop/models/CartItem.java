package com.example.tech_shop.models;

public class CartItem {
    private String productId;
    private String productName;
    private String image;
    private long unitPrice;
    private int quantity;

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImage() {
        return image;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
