package com.noqapp.domain.types.catgeory;

import java.util.EnumSet;

/**
 * hitender
 * 1/10/21 11:17 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public enum RentalTypeEnum {
    A("A", "Apartment"),
    H("H", "House"),
    R("R", "Room"),
    T("T", "Townhouse");

    private final String name;
    private final String description;

    RentalTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<RentalTypeEnum> rentalTypes = EnumSet.of(A, H, R, T);
}
