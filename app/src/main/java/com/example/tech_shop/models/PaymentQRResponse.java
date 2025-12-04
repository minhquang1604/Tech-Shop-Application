package com.example.tech_shop.models;

public class PaymentQRResponse {
    private String qr;
    private long amount;
    private String bankId;
    private String account;

    public String getQr() { return qr; }
    public long getAmount() { return amount; }
    public String getBankId() { return bankId; }
    public String getAccount() { return account; }
}
