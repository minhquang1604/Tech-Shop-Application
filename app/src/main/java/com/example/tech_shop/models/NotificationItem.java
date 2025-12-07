package com.example.tech_shop.models;

import java.util.Map;

public class NotificationItem {
    private String id;
    private String title;
    private String createdAt;
    private Map<String, Object> body;  // body = Map

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCreatedAt() { return createdAt; }

    public Map<String, Object> getBody() {
        return body;
    }
}
