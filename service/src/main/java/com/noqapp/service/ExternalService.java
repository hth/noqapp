package com.noqapp.service;

import static com.noqapp.common.utils.DateUtil.DAY.TODAY;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.domain.shared.Geocode;
import com.noqapp.domain.types.AddressOriginEnum;
import com.noqapp.repository.BizStoreManager;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 11/23/16 4:24 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class ExternalService {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalService.class);

    private GeoApiContext context;
    private BizStoreManager bizStoreManager;

    @Autowired
    public ExternalService(
        @Value("${google-server-api-key}")
        String googleServerApiKey,

        @Value("${google-api-max-retries}")
        int maxRetries,

        BizStoreManager bizStoreManager
    ) {
        this.context = new GeoApiContext.Builder()
            .apiKey(googleServerApiKey)
            .maxRetries(maxRetries)
            .disableRetries()
            .build();

        this.bizStoreManager = bizStoreManager;
    }

    /**
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service to bizStore.
     */
    public void decodeAddress(BizStoreEntity bizStore) {
        try {
            Geocode geocode = Geocode.newInstance(getGeocodingResults(bizStore.getAddress()), bizStore.getAddress());
            DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);
            if (decodedAddress.isNotBlank()) {
                if (bizStore.getAddressOrigin() != AddressOriginEnum.S) {
                    bizStore.setAddress(decodedAddress.getFormattedAddress());
                } else {
                    bizStore.setAddress(geocode.getAddress());
                }
                bizStore.setArea(decodedAddress.getArea());
                bizStore.setTown(decodedAddress.getTown());
                bizStore.setDistrict(decodedAddress.getDistrict());
                bizStore.setState(decodedAddress.getState());
                bizStore.setStateShortName(decodedAddress.getStateShortName());
                bizStore.setPostalCode(decodedAddress.getPostalCode());
                bizStore.setCountry(decodedAddress.getCountry());
                bizStore.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizStore.setCoordinate(decodedAddress.getCoordinate());
                }
                bizStore.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    String[] types = getPlaceDetailTypes(placeDetails);
                    if (types.length > 0) {
                        bizStore.setPlaceType(types);
                    }

                    if (AddressOriginEnum.S != bizStore.getAddressOrigin()) {
                        /*
                         * When origin is other than self, then do not add rating from third party site like Google.
                         * This can be removed as third party site and our reviews can be independent
                         */
                        bizStore.setRating(placeDetails.rating);
                    }

                    if (StringUtils.isNotBlank(placeDetails.formattedPhoneNumber)) {
                        bizStore.setPhone(placeDetails.formattedPhoneNumber);
                    }
                }

                bizStore.setValidatedUsingExternalAPI(true);
            } else {
                LOG.warn("Geocode result from address is empty for bizStoreId={} bizStoreAddress={}",
                    bizStore.getId(), bizStore.getAddress());
            }
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service bizStoreId={} bizStoreAddress={} reason={}",
                bizStore.getId(), bizStore.getAddress(), e.getLocalizedMessage(), e);
        }
    }

    /**
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service to bizStore.
     */
    public void decodeAddress(BizNameEntity bizName) {
        try {
            Geocode geocode = Geocode.newInstance(getGeocodingResults(bizName.getAddress()), bizName.getAddress());
            DecodedAddress decodedAddress = DecodedAddress.newInstance(geocode.getResults(), 0);
            if (decodedAddress.isNotBlank()) {
                if (bizName.getAddressOrigin() != AddressOriginEnum.S) {
                    bizName.setAddress(decodedAddress.getFormattedAddress());
                } else {
                    bizName.setAddress(geocode.getAddress());
                }
                bizName.setArea(decodedAddress.getArea());
                bizName.setTown(decodedAddress.getTown());
                bizName.setDistrict(decodedAddress.getDistrict());
                bizName.setState(decodedAddress.getState());
                bizName.setStateShortName(decodedAddress.getStateShortName());
                bizName.setPostalCode(decodedAddress.getPostalCode());
                bizName.setCountry(decodedAddress.getCountry());
                bizName.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizName.setCoordinate(decodedAddress.getCoordinate());
                }
                bizName.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    String[] types = getPlaceDetailTypes(placeDetails);
                    if (types.length > 0) {
                        bizName.setPlaceType(types);
                    }

                    if (StringUtils.isBlank(bizName.getPhone()) && StringUtils.isNotBlank(placeDetails.formattedPhoneNumber)) {
                        /* Append country code to phone number. */
                        String phone = Formatter.phoneNumberWithCountryCode(placeDetails.formattedPhoneNumber, bizName.getCountryShortName());
                        LOG.info("Phone changed cs={} from {} to {}",
                            bizName.getCountryShortName(), placeDetails.formattedPhoneNumber, phone);

                        bizName.setPhone(phone);
                    }
                }

                bizName.setValidatedUsingExternalAPI(true);
            } else {
                LOG.warn("Geocode result from address is empty for bizStoreId={} bizStoreAddress={}",
                    bizName.getId(), bizName.getAddress());
            }
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service bizStoreId={} bizStoreAddress={} reason={}",
                bizName.getId(), bizName.getAddress(), e.getLocalizedMessage(), e);
        }
    }

    private String[] getPlaceDetailTypes(PlaceDetails placeDetails) {
        String[] types = new String[placeDetails.types.length];
        for (int i = 0; i < placeDetails.types.length; i++) {
            types[i] = placeDetails.types[i].toString();
        }
        return types;
    }

    /**
     * Keeps looking for a valid address and location until it finds one.
     */
    public GeocodingResult[] getGeocodingResults(String address) {
        try {
            LOG.info("Google GeoCodingResults API called address={}", address);
            if (StringUtils.isBlank(address)) {
                throw new RuntimeException("Blank address found");
            }

            GeocodingResult[] geocodingResults = GeocodingApi.geocode(context, address).await();
            if (0 != geocodingResults.length) {
                return geocodingResults;
            }

            int index;
            if (address.contains(",")) {
                index = address.indexOf(",") + 1;
            } else {
                index = address.indexOf(" ") + 1;
            }

            String shortenedAddress = address.substring(index, address.length()).trim();
            if (shortenedAddress.length() == address.length()) {
                LOG.warn("Could not find GeocodingResult for address={}", address);
                return null;
            }

            return getGeocodingResults(shortenedAddress);
        } catch (Exception e) {
            LOG.error("Failed fetching from google address={} reason={}", address, e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * External call to find types and rating for a particular store.
     */
    private PlaceDetails getPlaceDetails(String placeId) {
        try {
            LOG.info("Google Place API called placeId={}", placeId);
            Assert.hasText(placeId, "PlaceId is empty");
            return PlacesApi.placeDetails(context, placeId).await();
        } catch (Exception e) {
            LOG.error("Failed fetching from google placeId={} reason={}", placeId, e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * Updates Biz Store time zone based on the address of the store. Asynchronous call.
     */
    public void updateTimezone(BizStoreEntity bizStore) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());
            TimeZoneApi.getTimeZone(context, bizStore.getLatLng()).setCallback(
                new PendingResult.Callback<TimeZone>() {

                    @Override
                    public void onResult(TimeZone timeZone) {
                        String zoneId = timeZone.toZoneId().getId();
                        StoreHourEntity today = bizStore.getStoreHours().get(zonedDateTime.getDayOfWeek().getValue() - 1);
                        int hourOfDay = today.storeClosingHourOfDay();
                        int minuteOfDay = today.storeClosingMinuteOfDay();
                        ZonedDateTime archiveNextRun = DateUtil.computeNextRunTimeAtUTC(timeZone, hourOfDay, minuteOfDay, TODAY);
                        /* Converting to date remove everything to do with UTC, hence important to run server on UTC time. */
                        boolean status = bizStoreManager.updateNextRun(bizStore.getId(), zoneId, Date.from(archiveNextRun.toInstant()), bizStore.getQueueAppointment());
                        if (status) {
                            LOG.info("Successful first run set UTC time={} for store={} address={}",
                                archiveNextRun,
                                bizStore.getId(),
                                bizStore.getAddress());
                        } else {
                            LOG.error("Failed setting first run UTC time={} for store={} address={}",
                                archiveNextRun,
                                bizStore.getId(),
                                bizStore.getAddress());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        LOG.error("Failed getting timezone reason={}", e.getLocalizedMessage(), e);
                    }
                }
            );
        } catch (Exception e) {
            LOG.error("Failed fetching from google timezone reason={}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * No call back. Synchronous call.
     */
    public String findTimeZone(LatLng latLng) {
        try {
            TimeZone tz = TimeZoneApi.getTimeZone(context, latLng).await();
            Assert.notNull(tz, "TimeZone could not be found for " + latLng);
            return tz.getID();
        } catch (Exception e) {
            LOG.error("Failed fetching from google timezone reason={}", e.getLocalizedMessage(), e);
            return null;
        }
    }
}
