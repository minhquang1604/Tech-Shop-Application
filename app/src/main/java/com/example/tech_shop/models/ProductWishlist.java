package com.example.tech_shop.models;

import com.google.gson.annotations.SerializedName;

public class ProductWishlist {

    @SerializedName("productId")
    private String productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("image")
    private String image;

    @SerializedName("unitPrice")
    private double unitPrice;

    // ✅ Constructors
    public ProductWishlist() {}

    public ProductWishlist(String productId, String productName, String image, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.image = image;
        this.unitPrice = unitPrice;
    }

    // ✅ Getters & Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
