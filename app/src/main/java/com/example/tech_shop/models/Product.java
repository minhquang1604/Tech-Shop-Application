package com.example.tech_shop.models;

public class Product {
    private String product_zipId;
    private String name;
    private int quantitySold;
    private String image;
    private int price;
    private double rating;
    private Sale sale;

    // Constructor rỗng cho Retrofit
    public Product() {}

    // Getter
    public String getProduct_zipId() {
        return product_zipId;
    }

    public String getName() {
        return name;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }
    public Sale getSale() { return sale; }
}