package com.example.tech_shop.models;

public class PrepareResponse {
    private String message;
    private Order order;

    // getter & setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}