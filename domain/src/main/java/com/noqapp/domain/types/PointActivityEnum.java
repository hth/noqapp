package com.noqapp.domain.types;

/**
 * hitender
 * 6/24/21 6:59 AM
 */
public enum PointActivityEnum {
    REV("REV", "Review", 10),
    INV("INV", "Invite", 1000),
    ISU("ISU", "Invitee Signup", 1000),

    BOP("BOP", "Boost Post", -100),
    HIP("HIP", "Household Item Post", -10),
    PRP("PRP", "Property Rental Post", -1000);

    private final String name;
    private final String description;
    private final int point;

    PointActivityEnum(String name, String description, int point) {
        this.name = name;
        this.description = description;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return description;
    }
}
