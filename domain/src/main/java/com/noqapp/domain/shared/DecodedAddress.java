package com.noqapp.domain.shared;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 11/23/16 4:30 PM
 */
public class DecodedAddress {
    private static final Logger LOG = LoggerFactory.getLogger(DecodedAddress.class);

    private String address;
    private String formattedAddress;
    private String town;
    private String district;
    private String state;
    private String stateShortName;
    private String postalCode;
    private String country;
    private String countryShortName;

    /* Format Longitude and then Latitude. */
    private double[] coordinate;
    private String placeId;
    private boolean empty = true;

    /* Based on size of the address, the bigger address is selected. */
    private DecodedAddress(GeocodingResult[] results, String address) {
        if (null != results && results.length > 0) {
            empty = false;
            Assert.notNull(results[0].geometry, "Address is null hence geometry is null");
            Assert.notNull(results[0].geometry.location, "Geometry is null hence location is null");

            this.address = address;
            formattedAddress = results[0].formattedAddress;

            for (AddressComponent addressComponent : results[0].addressComponents) {
                for (AddressComponentType addressComponentType : addressComponent.types) {
                    switch (addressComponentType) {
                        case COUNTRY:
                            LOG.debug("country code={}", addressComponent.shortName);
                            countryShortName = addressComponent.shortName;
                            country = addressComponent.longName;
                            break;
                        case LOCALITY:
                            LOG.debug("town code={}", addressComponent.shortName);
                            town = addressComponent.longName;
                            break;
                        case POSTAL_CODE:
                            LOG.debug("postal code={}", addressComponent.longName);
                            postalCode = addressComponent.longName;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_1:
                            LOG.debug("state code={}", addressComponent.longName);
                            state = addressComponent.longName;
                            stateShortName = addressComponent.shortName;
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_2:
                            LOG.debug("district code={}", addressComponent.longName);
                            district = addressComponent.longName;
                            break;
                        default:
                            LOG.debug("{} city code={}", addressComponentType.name(), addressComponent.longName);
                    }
                }
            }

            if (null != results[0].geometry) {
                this.coordinate = new double[]{
                        /** Mongo: Specify coordinates in this order: “longitude, latitude.” */
                        results[0].geometry.location.lng,
                        results[0].geometry.location.lat
                };
            }

            placeId = results[0].placeId;
        }
    }

    public static DecodedAddress newInstance(GeocodingResult[] results, String address) {
        return new DecodedAddress(results, address);
    }

    /**
     * Address entered, searched or as on receipt.
     * Example:
     *  Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur
     *  OR Tambo Airport Rd, Level 2, Domtex Building, OR Tambo International Airport, Johannesburg, 1627, South Africa
     *
     * External source could not locate these address. So to preserve whats entered (as un-altered) we save the address.
     */
    public String getAddress() {
        return address;
    }

    /* Address sourced from third party. External source. */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getTown() {
        return town;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public String getStateShortName() {
        return stateShortName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public String getPlaceId() {
        return placeId;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isNotEmpty() {
        return !empty;
    }
}

