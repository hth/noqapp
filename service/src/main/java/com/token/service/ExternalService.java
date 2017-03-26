package com.token.service;

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

import com.token.domain.BizNameEntity;
import com.token.domain.BizStoreEntity;
import com.token.domain.shared.DecodedAddress;
import com.token.repository.BizStoreManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 11/23/16 4:24 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ExternalService {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm");
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private GeoApiContext context;
    private BizStoreManager bizStoreManager;

    @Autowired
    public ExternalService(
            @Value ("${google-server-api-key}")
            String googleServerApiKey,
            
            @Value("${google-api-max-retries}")
            int maxRetries,        

            BizStoreManager bizStoreManager
    ) {
        this.context = new GeoApiContext()
                .setApiKey(googleServerApiKey)
                .setMaxRetries(maxRetries)
                .disableRetries();

        this.bizStoreManager = bizStoreManager;
    }

    /**
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service to bizStore.
     *
     * @param bizStore
     */
    public void decodeAddress(BizStoreEntity bizStore) {
        try {
            DecodedAddress decodedAddress = DecodedAddress.newInstance(getGeocodingResults(bizStore.getAddress()), bizStore.getAddress());
            if (decodedAddress.isNotEmpty()) {
                bizStore.setAddress(decodedAddress.getAddress());
                bizStore.setFormattedAddress(decodedAddress.getFormattedAddress());
                bizStore.setTown(decodedAddress.getTown());
                bizStore.setDistrict(decodedAddress.getDistrict());
                bizStore.setState(decodedAddress.getState());
                bizStore.setPostalCode(decodedAddress.getPostalCode());
                bizStore.setCountry(decodedAddress.getCountry());
                bizStore.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizStore.setCoordinate(decodedAddress.getCoordinate());
                }
                bizStore.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    bizStore.setPlaceType(placeDetails.types);
                    bizStore.setRating(placeDetails.rating);
                    if (StringUtils.isNotEmpty(placeDetails.formattedPhoneNumber)) {
                        bizStore.setPhone(placeDetails.formattedPhoneNumber);
                    }
                }

                bizStore.setValidatedUsingExternalAPI(true);
            } else {
                LOG.warn("Geocoding result from address is empty for bizStoreId={} bizStoreAddress={}",
                        bizStore.getId(), bizStore.getAddress());
            }
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service bizStoreId={} bizStoreAddress={} reason={}",
                    bizStore.getId(), bizStore.getAddress(), e.getLocalizedMessage(), e);
        }
    }

    /**
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service to bizStore.
     *
     * @param bizName
     */
    public void decodeAddress(BizNameEntity bizName) {
        try {
            DecodedAddress decodedAddress = DecodedAddress.newInstance(getGeocodingResults(bizName.getAddress()), bizName.getAddress());
            if (decodedAddress.isNotEmpty()) {
                bizName.setAddress(decodedAddress.getAddress());
                bizName.setFormattedAddress(decodedAddress.getFormattedAddress());
                bizName.setTown(decodedAddress.getTown());
                bizName.setDistrict(decodedAddress.getDistrict());
                bizName.setState(decodedAddress.getState());
                bizName.setPostalCode(decodedAddress.getPostalCode());
                bizName.setCountry(decodedAddress.getCountry());
                bizName.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizName.setCoordinate(decodedAddress.getCoordinate());
                }
                bizName.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    bizName.setPlaceType(placeDetails.types);
                    if (StringUtils.isNotEmpty(placeDetails.formattedPhoneNumber)) {
                        bizName.setPhone(placeDetails.formattedPhoneNumber);
                    }
                }

                bizName.setValidatedUsingExternalAPI(true);
            } else {
                LOG.warn("Geocoding result from address is empty for bizStoreId={} bizStoreAddress={}",
                        bizName.getId(), bizName.getAddress());
            }
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service bizStoreId={} bizStoreAddress={} reason={}",
                    bizName.getId(), bizName.getAddress(), e.getLocalizedMessage(), e);
        }
    }

    /**
     * Keeps looking for a valid address and location until it finds one.
     *
     * @param address
     * @return
     */
    public GeocodingResult[] getGeocodingResults(String address) {
        try {
            LOG.info("Google GeoCodingResults API called address={}", address);
            Assert.hasText(address, "Address is empty");
            GeocodingResult[] geocodingResults = GeocodingApi.geocode(context, address).await();
            if (geocodingResults.length != 0) {
                return geocodingResults;
            }

            int index = address.indexOf(",") + 1;
            return getGeocodingResults(address.substring(index, address.length()).trim());
        } catch (Exception e) {
            LOG.error("Failed fetching from google address={} reason={}", address, e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * External call to find types and rating for a particular store.
     *
     * @param placeId
     * @return
     * @throws Exception
     */
    PlaceDetails getPlaceDetails(String placeId) {
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
     * 
     * @param bizStore
     */
    public void updateTimezone(BizStoreEntity bizStore) {
        try {
            TimeZoneApi.getTimeZone(context, bizStore.getLatLng()).setCallback(
                    new PendingResult.Callback<TimeZone>() {

                        @Override
                        public void onResult(TimeZone timeZone) {
                            String zoneId = timeZone.toZoneId().getId();
                            Date queueHistory = computeNextRunTimeAtUTC(
                                    timeZone,
                                    bizStore.storeClosingHourOfDay(),
                                    bizStore.storeClosingMinuteOfDay());

                            boolean status = bizStoreManager.setZoneIdAndQueueHistory(bizStore.getId(), zoneId, queueHistory);
                            if (status) {
                                LOG.info("Update UTC time for store={} address={}", bizStore.getId(), bizStore.getAddress());
                            } else {
                                LOG.error("Update UTC time for store={} address={}", bizStore.getId(), bizStore.getAddress());
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
     * 
     * @param latLng
     * @return
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

    /**
     * Compute local time with zone id at UTC.
     *
     * @param timeZone
     * @param hourOfDay
     * @param minuteOfDay
     * @return
     */
    public Date computeNextRunTimeAtUTC(TimeZone timeZone, int hourOfDay, int minuteOfDay) {
        Assert.notNull(timeZone, "TimeZone should not be null");
        LocalDateTime currentLocalDateTime = LocalDateTime.now(Clock.system(timeZone.toZoneId()));
        currentLocalDateTime.plusDays(1);
        Instant futureInstant = currentLocalDateTime.toInstant(ZoneOffset.ofHours(0));
        Date futureDate = Date.from(futureInstant);

        String str = df.format(futureDate) + String.format(" %02d", hourOfDay) + String.format(":%02d", minuteOfDay);
        LocalDateTime localDateTime = LocalDateTime.parse(str, formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, timeZone.toZoneId());
        LOG.debug("Current date and time in a particular timezone={}", zonedDateTime);

        ZonedDateTime utcDate = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        LOG.debug("Current date and time in UTC={}", utcDate);
        return Date.from(utcDate.toInstant());
    }
}
