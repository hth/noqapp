package com.noqapp.search.elastic.helper;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.MathUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.IncidentEventEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.search.elastic.domain.BizStoreElastic;
import com.noqapp.search.elastic.domain.IncidentEventElastic;
import com.noqapp.search.elastic.domain.MarketplaceElastic;
import com.noqapp.search.elastic.domain.StoreHourElastic;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.common.geo.GeoPoint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

        BizStoreElastic bizStoreElastic = new BizStoreElastic()
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
            .setReviewCount(bizStore.getReviewCount())
            .setBizNameId(bizStore.getBizName().getId())
            .setDisplayName(bizStore.getDisplayName())
            .setEnabledPayment(bizStore.isEnabledPayment())
            .setProductPrice(bizStore.getProductPrice())
            .setWalkInState(bizStore.getWalkInState())
            .setAppointmentState(bizStore.getAppointmentState())
            .setAppointmentDuration(bizStore.getAppointmentDuration())
            .setAppointmentOpenHowFar(bizStore.getAppointmentOpenHowFar())
            .setCodeQR(bizStore.getCodeQR())
            .setTimeZone(bizStore.getTimeZone())
            .setGeoHash(bizStore.getGeoPoint().getGeohash())
            .setWebLocation(bizStore.getWebLocation())
            .setFamousFor(bizStore.getFamousFor())
            .setDisplayImage(businessImageHolder.getBannerImage())
            .setStoreHourElasticList(getStoreHourElastics(storeHours))
            .setBizServiceImages(businessImageHolder.getServiceImages());

        switch (bizStore.getBusinessType()) {
            case CD:
            case CDQ:
                bizStoreElastic
                    .setTag(bizStore.getBizName().getTag())
                    .addTag("csd").addTag("esm").addTag("ems").addTag("echs").addTag("canteen").addTag("ex service");
            default:
                //Do nothing
        }

        return bizStoreElastic;
    }

    @Mobile
    public static List<StoreHourElastic> getStoreHourElastics(List<StoreHourEntity> storeHours) {
        List<StoreHourElastic> storeHourElastics = new LinkedList<>();
        for (StoreHourEntity storeHour : storeHours) {
            storeHourElastics.add(new StoreHourElastic()
                .setDayOfWeek(storeHour.getDayOfWeek())
                .setStartHour(storeHour.getStartHour())
                .setAppointmentStartHour(storeHour.getAppointmentStartHour())
                .setEndHour(storeHour.getEndHour())
                .setAppointmentEndHour(storeHour.getAppointmentEndHour())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom() )
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setLunchTimeStart(storeHour.getLunchTimeStart())
                .setLunchTimeEnd(storeHour.getLunchTimeEnd())
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
                .setAppointmentStartHour(storeHour.getAppointmentStartHour())
                .setEndHour(storeHour.getEndHour())
                .setAppointmentEndHour(storeHour.getAppointmentEndHour())
                .setTokenAvailableFrom(storeHour.getTokenAvailableFrom())
                .setTokenNotAvailableFrom(storeHour.getTokenNotAvailableFrom())
                .setLunchTimeStart(storeHour.getLunchTimeStart())
                .setLunchTimeEnd(storeHour.getLunchTimeEnd())
                .setDayClosed(true)
            );
        }
        return storeHourElastics;
    }

    public static MarketplaceElastic getAsMarketplaceElastic(MarketplaceEntity marketplace) {
        MarketplaceElastic marketplaceElastic = new MarketplaceElastic()
            .setId(marketplace.getId())
            .setBusinessType(marketplace.getBusinessType())
            .setProductPrice(MathUtil.displayPrice(marketplace.getProductPrice()))
            .setTitle(marketplace.getTitle())
            .setDescription(marketplace.getDescription())
            .setPostImages(List.copyOf(marketplace.getPostImages()))
            .setViewCount(marketplace.getViewCount())
            .setExpressedInterestCount(marketplace.getExpressedInterestCount())
            .setRating(marketplace.computeRating().toString())
            .setGeoPointOfQ(marketplace.getCoordinate() == null ? new GeoPointOfQ(0.0, 0.0) : marketplace.getGeoPointOfQ())
            .setGeoHash(marketplace.getCoordinate() == null ? new GeoPoint(0.0, 0.0).getGeohash() : marketplace.getGeoPoint().getGeohash())
            .setCity(marketplace.getCity())
            .setTown(marketplace.getTown())
            .setCountryShortName(marketplace.getCountryShortName());
        switch (marketplace.getBusinessType()) {
            case PR:
                marketplaceElastic
                    .setTag(marketplace.getFieldValueForTag())
                    .setFieldTags(marketplace.getFieldTags());
                break;
            case HI:
                marketplaceElastic
                    .setTag(marketplace.getFieldValueForTag())
                    .setFieldTags(marketplace.getFieldTags());
                break;
            default:
                LOG.warn("Reached un-reachable condition businessType={}", marketplace.getBusinessType());
                throw new UnsupportedOperationException("Reached unsupported condition " + marketplace.getBusinessType());
        }

        return marketplaceElastic;
    }

    public static IncidentEventElastic getAsIncidentEventElastic(IncidentEventEntity incidentEvent) {
        return new IncidentEventElastic()
            .setId(incidentEvent.getId())
            .setIncidentEvent(incidentEvent.getIncidentEvent())
            .setGeoHash(incidentEvent.getGeoPoint().getGeohash())
            .setTitle(incidentEvent.getTitle())
            .setCreated(incidentEvent.getCreated());
    }
}
