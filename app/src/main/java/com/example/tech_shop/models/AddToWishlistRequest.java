package com.example.tech_shop.models;

public class AddToWishlistRequest {
    private String productId;
    private String productName;
    private String image;
    private long unitPrice;

    public AddToWishlistRequest(String productId, String productName, String image, long unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImage() {
        return image;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
