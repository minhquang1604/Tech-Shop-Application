package com.example.tech_shop.models;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tech_shop.adapter.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDetail {
    private String productId;
    private String name;
    private String description;
    private double price;
    private List<String> imageURL;
    private Rating rating;
    private Map<String, String> detail;
    private String category;
    private int sold;
    private Sale sale; // thêm trường sale

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public List<String> getImageURL() { return imageURL; }
    public Rating getRating() { return rating; }
    public Map<String, String> getDetail() { return detail; }
    public String getCategory() { return category; }
    public int getSold() { return sold; }
    public Sale getSale() { return sale; } // getter sale

    public static class Rating {
        private int rate_1;
        private int rate_2;
        private int rate_3;
        private int rate_4;
        private int rate_5;

        public int getRate_1() { return rate_1; }
        public int getRate_2() { return rate_2; }
        public int getRate_3() { return rate_3; }
        public int getRate_4() { return rate_4; }
        public int getRate_5() { return rate_5; }
    }

    // class Sale
    public static class Sale {
        private double percent;
        private String startDate;
        private String endDate;
        private boolean isActive;

        public double getPercent() { return percent; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public boolean isActive() { return isActive; }
    }
}

