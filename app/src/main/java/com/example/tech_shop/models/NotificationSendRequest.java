package com.example.tech_shop.models;

import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class NotificationSendRequest {

    private String id;
    private String title;
    private Map<String, Object> body = new HashMap<>();
    private String username;
    private boolean isRead = false;
    private String createdAt;

    public NotificationSendRequest(String id, String title, String message, String username) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.createdAt = java.time.Instant.now().toString();

        this.body.put("additionalProp1", message);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Map<String, Object> getBody() { return body; }
    public String getUsername() { return username; }
    public boolean getIsRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}
