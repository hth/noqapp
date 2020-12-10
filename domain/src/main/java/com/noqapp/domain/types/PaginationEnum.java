package com.noqapp.domain.types;

/**
 * User: hitender
 * Date: 11/23/16 4:46 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum PaginationEnum {
    ALL(-1),
    ONE(1),
    TWO(2),
    THREE(3),
    FIVE(5),
    TEN(10),
    TWENTY(20),
    THIRTY(30);

    private int limit;

    PaginationEnum(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
