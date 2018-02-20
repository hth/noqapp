package com.noqapp.search.elastic.helper;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.search.elastic.domain.BizStoreElastic;

/**
 * hitender
 * 11/21/17 6:01 PM
 */
public class DomainConversion {

    public static BizStoreElastic getAsBizStoreElastic(BizStoreEntity bizStore) {
        return new BizStoreElastic()
                .setId(bizStore.getId())
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBusinessType(bizStore.getBusinessType())
                .setAddress(bizStore.getAddress())
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
                .setWebLocation(bizStore.getWebLocation());
    }
}
