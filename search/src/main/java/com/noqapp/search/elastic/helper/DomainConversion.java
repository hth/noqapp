package com.noqapp.search.elastic.helper;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.StoreHourElastic;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helps transform data for persisting in Elastic.
 * <p>
 * hitender
 * 11/21/17 6:01 PM
 */
public class DomainConversion {
    private static final Logger LOG = LoggerFactory.getLogger(DomainConversion.class);

    /**
     * Sets business images based on business types.
     * Note: Keep updating these based on business type.
     */
    public static BusinessImageHolder populateBizAndStoreImages(BizStoreEntity bizStore) {
        String bannerImage = "";
        Set<String> serviceImages;

        String codeQR;
        switch (bizStore.getBusinessType()) {
            case DO:
                codeQR = bizStore.getBizName().getCodeQR();
                if (!bizStore.getBizName().getBusinessServiceImages().isEmpty()) {
                    bannerImage = codeQR + FileUtil.getFileSeparator() + bizStore.getBizName().getBusinessServiceImages().iterator().next();
                }
                serviceImages = bizStore.getBizName().getBusinessServiceImages().stream().map(x -> codeQR + FileUtil.getFileSeparator() + x).collect(Collectors.toSet());
                break;
            default:
                bannerImage = bizStore.getStoreInteriorImages().isEmpty() ? null : bizStore.getCodeQR() + FileUtil.getFileSeparator() + bizStore.getStoreInteriorImages().iterator().next();
                if (StringUtils.isBlank(bannerImage)) {
                    /* Put business name first image as banner image. */
                    codeQR = bizStore.getBizName().getCodeQR();
                    if (!bizStore.getBizName().getBusinessServiceImages().isEmpty()) {
                        bannerImage = codeQR + FileUtil.getFileSeparator() + bizStore.getBizName().getBusinessServiceImages().iterator().next();
                    }
                    serviceImages = bizStore.getBizName().getBusinessServiceImages().stream().map(x -> codeQR + FileUtil.getFileSeparator() + x).collect(Collectors.toSet());
                } else {
                    serviceImages = bizStore.getStoreInteriorImages().stream().map(x -> bizStore.getCodeQR() + FileUtil.getFileSeparator() + x).collect(Collectors.toSet());
                }
        }

        return new BusinessImageHolder()
            .setBannerImage(bannerImage)
            .setServiceImages(serviceImages);
    }

    /** Persist for Elastic. */
    public static BizStoreElastic getAsBizStoreElastic(BizStoreEntity bizStore, List<StoreHourEntity> storeHours) {
        BusinessImageHolder businessImageHolder = populateBizAndStoreImages(bizStore);
        if (StringUtils.isBlank(businessImageHolder.getBannerImage())) {
            LOG.warn("No Banner Image for bizName={} bizId={}", bizStore.getBizName().getBusinessName(), bizStore.getBizName().getId());
        }

        return new BizStoreElastic()
            .setId(bizStore.getId())
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setBusinessType(bizStore.getBusinessType())
            /*
             * Business Category below is replaced with text at a later stage in process by method
             * BizStoreElasticManagerImpl.replaceCategoryIdWithCategoryName(),
             * right before insert to Elastic.
             */
            .setBizCategoryName(null)
            .setBizCategoryId(bizStore.getBizCategoryId())
            .setAddress(bizStore.getAddress())
            .setArea(bizStore.getArea())
            .setTown(bizStore.getTown())
            .setDistrict(bizStore.getDistrict())
            .setState(bizStore.getState())
            .setStateShortName(bizStore.getStateShortName())
            .setPostalCode(bizStore.getPostalCode())
            .setCountry(bizStore.getCountry())
            .setCountryShortName(bizStore.getCountryShortName())
            .setPhone(bizStore.getPhone())
            .setPhoneRaw(bizStore.getPhoneRaw())
            .setGeoPointOfQ(bizStore.getGeoPointOfQ())
            .setPlaceId(bizStore.getPlaceId())
            .setPlaceType(bizStore.getPlaceType())
            .setRating(bizStore.getRating())
            .setRatingCount(bizStore.getReviewCount())
            .setBizNameId(bizStore.getBizName().getId())
            .setDisplayName(bizStore.getDisplayName())
            .setProductPrice(bizStore.getProductPrice())
            .setCodeQR(bizStore.getCodeQR())
            .setTimeZone(bizStore.getTimeZone())
            .setGeoHash(bizStore.getGeoPoint().getGeohash())
            .setWebLocation(bizStore.getWebLocation())
            .setFamousFor(bizStore.getFamousFor())
            .setDisplayImage(businessImageHolder.getBannerImage())
            .setStoreHourElasticList(getStoreHourElastics(storeHours))
            .setBizServiceImages(businessImageHolder.getServiceImages());
    }

    @Mobile
    public static List<StoreHourElastic> getStoreHourElastics(List<StoreHourEntity> storeHours) {
        List<StoreHourElastic> storeHourElastics = new LinkedList<>();
        for (StoreHourEntity storeHour : storeHours) {
            storeHourElastics.add(new StoreHourElastic()
                .setDayOfWeek(storeHour.getDayOfWeek())
                .setStartHour(storeHour.getStartHour())
                .setEndHour(storeHour.getEndHour())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setDayClosed(storeHour.isDayClosed() || storeHour.isTempDayClosed())
            );
        }
        return storeHourElastics;
    }

    @Mobile
    public static List<StoreHourElastic> getStoreHourElasticsWithClosedAsDefault(List<StoreHourEntity> storeHours) {
        List<StoreHourElastic> storeHourElastics = new LinkedList<>();
        for (StoreHourEntity storeHour : storeHours) {
            storeHourElastics.add(new StoreHourElastic()
                .setDayOfWeek(storeHour.getDayOfWeek())
                .setStartHour(storeHour.getStartHour())
                .setEndHour(storeHour.getEndHour())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setDayClosed(true)
            );
        }
        return storeHourElastics;
    }
}
