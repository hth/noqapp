package com.noqapp.domain.types.catgeory;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    ACT("ACT", "Serving"),
    RET("RET", "Ex-Servicemen"),
    OFF("OFF", "Officers");

    private final String description;
    private final String name;

    CanteenStoreDepartmentEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return description;
    }
}
