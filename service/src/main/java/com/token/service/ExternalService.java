package com.token.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.GeocodingResult;
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

    private GeoApiContext context;

    @Autowired
    public ExternalService(
            @Value ("${google-server-api-key}")
                    String googleServerApiKey
    ) {
        this.context = new GeoApiContext()
                .setApiKey(googleServerApiKey)
                .setMaxRetries(0)
                .disableRetries();
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
                bizStore.setPostalCode(decodedAddress.getPostalCode());
                bizStore.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizStore.setCoordinate(decodedAddress.getCoordinate());
                }
                bizStore.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    bizStore.setPlaceType(placeDetails.types);
                    bizStore.setPlaceRating(placeDetails.rating);
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
                bizName.setPostalCode(decodedAddress.getPostalCode());
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
}
