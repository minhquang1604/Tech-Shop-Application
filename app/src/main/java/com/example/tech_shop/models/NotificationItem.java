package com.example.tech_shop.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class NotificationItem {

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private Map<String, String> body;

    @SerializedName("createdAt")
    private String createdAt;

    public NotificationItem() {}

    public String getTitle() { return title; }
    public Map<String, String> getBody() { return body; }
    public String getCreatedAt() { return createdAt; }
}
