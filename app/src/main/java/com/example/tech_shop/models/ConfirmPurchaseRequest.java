package com.example.tech_shop.models;

public class ConfirmPurchaseRequest {
    private ReceiveInfo receiveInfo;
    private String paymentMethod;

    public ConfirmPurchaseRequest(ReceiveInfo receiveInfo, String paymentMethod) {
        this.receiveInfo = receiveInfo;
        this.paymentMethod = paymentMethod;
    }
    // getters & setters nếu cần
}