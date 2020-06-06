package com.noqapp.domain.types.catgeory;

import static com.noqapp.domain.types.BusinessCustomerAttributeEnum.GR;
import static com.noqapp.domain.types.BusinessCustomerAttributeEnum.LQ;

import com.noqapp.domain.types.BusinessCustomerAttributeEnum;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    EG("EG", "Ex-Servicemen Grocery", GR),
    SG("SG", "Serving-PBOR Grocery", GR),
    XG("XG", "Ex-Officers Grocery", GR),
    OG("OG", "Officers Grocery", GR),

    EL("EL", "Ex-Servicemen Liquor", LQ),
    SL("SL", "Serving-PBOR Liquor", LQ),
    XL("XL", "Ex-Officers Liquor", LQ),
    OL("OL", "Officers Liquor", LQ);

    private final String description;
    private final String name;
    private final BusinessCustomerAttributeEnum businessCustomerAttribute;

    CanteenStoreDepartmentEnum(String name, String description, BusinessCustomerAttributeEnum businessCustomerAttribute) {
        this.name = name;
        this.description = description;
        this.businessCustomerAttribute = businessCustomerAttribute;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public BusinessCustomerAttributeEnum getBusinessCustomerAttribute() {
        return businessCustomerAttribute;
    }

    @Override
    public String toString() {
        return description;
    }
}
