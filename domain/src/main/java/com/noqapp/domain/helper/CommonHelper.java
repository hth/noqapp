package com.noqapp.domain.helper;

import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 7/1/18 11:02 AM
 */
public class CommonHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CommonHelper.class);

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
            case ST:
                return null;
            default:
                LOG.error("Un-supported businessType={}", businessType);
                throw new UnsupportedOperationException("Reached un-supported condition");
        }
    }
}
