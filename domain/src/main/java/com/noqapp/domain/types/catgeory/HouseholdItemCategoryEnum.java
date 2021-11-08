package com.noqapp.domain.types.catgeory;

/**
 * hitender
 * 11/7/21 7:34 AM
 */
public enum HouseholdItemCategoryEnum {
    AUTO("AUTO", "Automobile"),
    FURN("FURN", "Furniture"),
    ELAP("ELAP", "Electrical Appliance"),
    FREQ("FREQ", "Farm Equipment"),
    BOOK("BOOK", "Books"),
    FASH("FASH", "Fashion");

    private final String name;
    private final String description;

    HouseholdItemCategoryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
