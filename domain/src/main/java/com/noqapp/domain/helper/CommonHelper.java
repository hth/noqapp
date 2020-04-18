package com.noqapp.domain.helper;

import static java.util.stream.Collectors.toMap;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.InvocationByEnum;
import com.noqapp.domain.types.catgeory.BankDepartmentEnum;
import com.noqapp.domain.types.catgeory.GroceryEnum;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.domain.types.medical.PharmacyCategoryEnum;

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

    /**
     * Specify who is calling it, as different categories are sent for different invocation.
     * Admin sees different than store manager at times.
     */
    public static Map<String, String> getCategories(BusinessTypeEnum businessType, InvocationByEnum invocationBy) {
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
            case HS:
                List<HealthCareServiceEnum> healthCareServiceEnums = Stream.of(HealthCareServiceEnum.values())
                    .sorted(Comparator.comparing(HealthCareServiceEnum::getDescription))
                    .collect(Collectors.toList());

                map = new LinkedHashMap<>();
                for (HealthCareServiceEnum healthCareService : healthCareServiceEnums) {
                    map.put(healthCareService.name(), healthCareService.getDescription());
                }
                return map;
            case PH:
                /* Pharmacy does not have category at business level, but at store level. */
                return InvocationByEnum.BUSINESS == invocationBy
                    ? null
                    : PharmacyCategoryEnum.asMapWithNameAsKey(); /* For Store show default categories. */
            case GS:
                /* Store does not have category at business level, but at store level. */
                switch (invocationBy) {
                    case BUSINESS:
                        return null;
                    case STORE:
                        List<GroceryEnum> groceryEnums = Stream.of(GroceryEnum.values())
                            .sorted(Comparator.comparing(GroceryEnum::getDescription))
                            .collect(Collectors.toList());

                        /* https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html */
                        return groceryEnums.stream()
                            .collect(toMap(GroceryEnum::getName, GroceryEnum::getDescription, (e1, e2) -> e2, LinkedHashMap::new));
                    default:
                        LOG.error("Un-supported businessType={}", businessType);
                        throw new UnsupportedOperationException("Reached un-supported condition");
                }

            case RS:
            case FT:
            case BA:
            case ST:
            case STQ:
            case GSQ:
            case BAQ:
            case CFQ:
            case RSQ:
            case FTQ:
            case SM:
            case MT:
            case SC:
            case CF:
            case PW:
            case MU:
            case TA:
            case NC:
            case PA:
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
            LOG.error("Failed getting category bizStoreId={} businessType{}", bizStore.getId(), bizStore.getBusinessType());
        }
        return categoryName;
    }

    public static String getBannerImage(BizStoreEntity bizStore) {
        try {
            String bannerImage;
            switch (bizStore.getBusinessType()) {
                case DO:
                    bannerImage = bizStore.getBizName().getBusinessServiceImages().isEmpty() ? null : bizStore.getBizName().getCodeQR() + "/" + bizStore.getBizName().getBusinessServiceImages().iterator().next();
                    break;
                case RS:
                case FT:
                    bannerImage = bizStore.getStoreInteriorImages().isEmpty()
                        ? bizStore.getBizName().getCodeQR() + "/" + bizStore.getBizName().getBusinessServiceImages().iterator().next()
                        : bizStore.getCodeQR() + "/" + bizStore.getStoreInteriorImages().iterator().next();
                    break;
                default:
                    bannerImage = bizStore.getStoreServiceImages().isEmpty() ? null : bizStore.getCodeQR() + "/" + bizStore.getStoreServiceImages().iterator().next();
                    if (StringUtils.isBlank(bannerImage)) {
                        /* If none is found, then get image from bizName. */
                        bannerImage = bizStore.getBizName().getBusinessServiceImages().isEmpty() ? null : bizStore.getBizName().getCodeQR() + "/" + bizStore.getBizName().getBusinessServiceImages().iterator().next();
                    }
            }
            LOG.info("bizStore Id={} name=\"{}\" bannerImage={}", bizStore.getId(), bizStore.getDisplayName(), bannerImage);
            return bannerImage;
        } catch (Exception e) {
            LOG.error("Failed getting banner image for bizStoreId={} displayName=\"{}\" reason={}",
                bizStore.getId(),
                bizStore.getDisplayName(),
                e.getLocalizedMessage(),
                e);
            return "";
        }
    }
}
