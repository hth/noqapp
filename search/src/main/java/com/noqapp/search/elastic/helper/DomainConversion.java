package com.noqapp.search.elastic.helper;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.StoreHourElastic;

import java.util.LinkedList;
import java.util.List;

/**
 * Helps transform data for persisting in Elastic.
 * <p>
 * hitender
 * 11/21/17 6:01 PM
 */
public class DomainConversion {
    public static BizStoreElastic getAsBizStoreElastic(
            BizStoreEntity bizStore,
            List<StoreHourEntity> storeHours
    ) {
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
                .setRatingCount(bizStore.getRatingCount())
                .setBizNameId(bizStore.getBizName().getId())
                .setDisplayName(bizStore.getDisplayName())
                .setCodeQR(bizStore.getCodeQR())
                .setTimeZone(bizStore.getTimeZone())
                .setGeoHash(bizStore.getGeoPoint().getGeohash())
                .setWebLocation(bizStore.getWebLocation())
                .setDisplayImage(bizStore.getDisplayImage())
                .setStoreHourElasticList(getStoreHourElastics(storeHours))
                .setBizServiceImages(bizStore.getBizName().getBusinessServiceImages());
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
                    .setDayClosed(storeHour.isDayClosed())
            );
        }
        return storeHourElastics;
    }
}
