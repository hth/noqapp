package com.noqapp.domain.types;

/**
 * Added to differentiate between client and business coupons.
 * User: hitender
 * Date: 2019-06-17 16:25
 */
public enum CouponGroupEnum {
    M("M", "Business"),
    C("C", "Client");

    private String name;
    private String description;

    CouponGroupEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
