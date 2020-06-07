package com.noqapp.domain.types.catgeory;

import static com.noqapp.domain.types.BusinessCustomerAttributeEnum.GR;
import static com.noqapp.domain.types.BusinessCustomerAttributeEnum.LQ;

import com.noqapp.domain.types.BusinessCustomerAttributeEnum;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 5/23/20 3:27 PM
 */
public enum CanteenStoreDepartmentEnum {

    EG("EG", "Retired/Servicemen (Grocery)", GR),
    XG("XG", "Officer Retired (Grocery)", GR),
    SG("SG", "Serving-PBOR (Grocery)", GR),
    OG("OG", "Officer Serving (Grocery)", GR),

    EL("EL", "Retired/Servicemen (Liquor)", LQ),
    XL("XL", "Officer Retired (Liquor)", LQ),
    SL("SL", "Serving-PBOR (Liquor)", LQ),
    OL("OL", "Officer Serving (Liquor)", LQ);

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

    public static List<CanteenStoreDepartmentEnum> ordered() {
        return new LinkedList<CanteenStoreDepartmentEnum>() {{
            add(OG);
            add(OL);
            add(XG);
            add(XL);
            add(SG);
            add(SL);
            add(EG);
            add(EL);
        }};
    }

    @Override
    public String toString() {
        return description;
    }
}
