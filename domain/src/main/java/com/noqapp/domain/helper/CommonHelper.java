package com.noqapp.domain.helper;

import static java.util.stream.Collectors.toMap;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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
                List<MedicalDepartmentEnum> medicalDepartmentEnums = Stream.of(MedicalDepartmentEnum.values())
                    .sorted(Comparator.comparing(MedicalDepartmentEnum::getDescription))
                    .collect(Collectors.toList());

                /* https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html */
                return medicalDepartmentEnums.stream()
                    .collect(toMap(MedicalDepartmentEnum::getName, MedicalDepartmentEnum::getDescription, (e1, e2) -> e2, LinkedHashMap::new));
            case BK:
                List<BankDepartmentEnum> bankDepartmentEnums = Stream.of(BankDepartmentEnum.values())
                    .sorted(Comparator.comparing(BankDepartmentEnum::getDescription))
                    .collect(Collectors.toList());

                Map<String, String> map = new LinkedHashMap<>();
                for (BankDepartmentEnum bankDepartment : bankDepartmentEnums) {
                    map.put(bankDepartment.name(), bankDepartment.getDescription());
                }
                return map;
            case PH:
            case RS:
            case ST:
            case SC:
                return null;
            default:
                LOG.error("Un-supported businessType={}", businessType);
                throw new UnsupportedOperationException("Reached un-supported condition");
        }
    }

    /**
     * Finds category name based on business type.
     *
     * @param bizStore
     * @return
     */
    public static String findCategoryName(BizStoreEntity bizStore) {
        /* Pass blank category name as FTL fails to process when its null OR add IF condition in FTL. */
        String categoryName = null;
        try {
            if (StringUtils.isNotBlank(bizStore.getBizCategoryId())) {
                switch (bizStore.getBusinessType()) {
                    case DO:
                        categoryName = MedicalDepartmentEnum.valueOf(bizStore.getBizCategoryId()).getDescription();
                        break;
                    case BK:
                        categoryName = BankDepartmentEnum.valueOf(bizStore.getBizCategoryId()).getDescription();
                        break;
                    default:
                        categoryName = bizStore.getBizCategoryId();
                }
            }
        } catch (Exception e) {
            LOG.error("Failed getting category {} {}", bizStore.getId(), bizStore.getBusinessType());
        }
        return categoryName;
    }
}
