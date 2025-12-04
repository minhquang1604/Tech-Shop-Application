package com.example.tech_shop.models;

import java.util.List;
import java.io.Serializable;
public class Order implements Serializable {
    private String orderID;
    private int userID;
    private List<OrderItem> items;
    private double totalAmount;
    private String paymentMethod;
    private String status;
    private String createdAt;
    private ReceiveInfo receiveInfo;

    // getters & setters
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public ReceiveInfo getReceiveInfo() { return receiveInfo; }
    public void setReceiveInfo(ReceiveInfo receiveInfo) { this.receiveInfo = receiveInfo; }
}
