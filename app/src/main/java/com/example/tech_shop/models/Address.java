package com.example.tech_shop.models;

public class Address {
    private String name;
    private String phone;
    private String address;

    public Address(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
