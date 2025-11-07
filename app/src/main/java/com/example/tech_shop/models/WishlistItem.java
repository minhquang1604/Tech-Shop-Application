package com.example.tech_shop.models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class WishlistItem {
    private String productId;
    private String productName;
    private String image;
    private int unitPrice;

    public WishlistItem(String productId, String productName, String image, int unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.unitPrice = unitPrice;
    }

    // Getters & Setters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getImage() { return image; }
    public int getUnitPrice() { return unitPrice; }
}
