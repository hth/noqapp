package com.token.domain.types;

/**
 * User: hitender
 * Date: 3/1/17 12:23 PM
 */
public enum DeviceTypeEnum {
    A("A", "Android"),
    I("I", "IPhone");

    private String name;
    private String description;

    DeviceTypeEnum(String name, String description) {
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
