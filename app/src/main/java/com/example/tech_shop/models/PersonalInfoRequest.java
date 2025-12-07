package com.example.tech_shop.models;

public class PersonalInfoRequest {
    private String name;
    private String phoneNumber;
    private String birthday; // ISO format: "2025-12-07T10:38:45.468Z"
    private String gender;
    private String avatar; // URL hoặc Base64 string

    // Constructor rỗng bắt buộc cho Retrofit/Gson
    public PersonalInfoRequest() { }

    // Constructor đầy đủ
    public PersonalInfoRequest(String name, String phoneNumber, String birthday, String gender, String avatar) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.gender = gender;
        this.avatar = avatar;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
