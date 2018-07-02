package com.noqapp.domain.helper;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 7/1/18 11:02 AM
 */
public class CommonHelper {

    public static Map<String, String> getCategories(BusinessTypeEnum businessType) {
        switch (businessType) {
            case DO:
                return Stream.of(MedicalDepartmentEnum.values())
                    .collect(Collectors.toMap(MedicalDepartmentEnum::getName, MedicalDepartmentEnum::getDescription));
            case BK:
                return Stream.of(BankDepartmentEnum.values())
                    .collect(Collectors.toMap(BankDepartmentEnum::getName, BankDepartmentEnum::getDescription));
            case PH:
            case RS:
                return null;
            default:
                throw new UnsupportedOperationException("Reached un-supported condition");
        }
    }
}
