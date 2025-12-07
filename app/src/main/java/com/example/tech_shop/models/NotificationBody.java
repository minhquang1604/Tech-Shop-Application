package com.example.tech_shop.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class NotificationBody {

    @SerializedName("additionalProp1")
    private String additionalProp1;

    @SerializedName("additionalProp2")
    private String additionalProp2;

    @SerializedName("additionalProp3")
    private String additionalProp3;

    // Hoặc hứng tất cả key còn lại
    private Map<String, Object> rawBody;

    public String getAdditionalProp1() {
        return additionalProp1;
    }

    public String getAdditionalProp2() {
        return additionalProp2;
    }

    public String getAdditionalProp3() {
        return additionalProp3;
    }

    public Map<String, Object> getRawBody() {
        return rawBody;
    }
}
