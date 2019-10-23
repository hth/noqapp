package com.noqapp.domain.shared;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import org.elasticsearch.common.geo.GeoPoint;

import java.io.Serializable;
import java.util.Arrays;

/**
 * User: hitender
 * Date: 11/23/16 4:30 PM
 */
public class DecodedAddress implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(DecodedAddress.class);

    private String formattedAddress;
    private String area;
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

    /* Naming convention is similar to StringUtils isBlank. */
    private boolean blank = true;

    /* Based on size of the address, the bigger address is selected. */
    private DecodedAddress(GeocodingResult[] results, int counter) {
        if (null != results && results.length > 0) {
            blank = false;
            Assert.notNull(results[counter].geometry, "Address is null hence geometry is null");
            Assert.notNull(results[counter].geometry.location, "Geometry is null hence location is null");

            formattedAddress = results[counter].formattedAddress;

            for (AddressComponent addressComponent : results[counter].addressComponents) {
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
                        case SUBLOCALITY_LEVEL_1:
                            LOG.info("area code={}", addressComponent.longName);
                            area = addressComponent.longName;
                            break;
                        default:
                            LOG.debug("{} city code={}", addressComponentType.name(), addressComponent.longName);
                    }
                }
            }

            if (null != results[counter].geometry) {
                this.coordinate = new double[]{
                        /* Mongo: Specify coordinates in this order: “longitude, latitude.” */
                        results[counter].geometry.location.lng,
                        results[counter].geometry.location.lat
                };
            }

            placeId = results[counter].placeId;
        }
    }

    public static DecodedAddress newInstance(GeocodingResult[] results, int counter) {
        return new DecodedAddress(results, counter);
    }

    /* Address sourced from third party. External source. */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getArea() {
        return area;
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

    public boolean isBlank() {
        return blank;
    }

    public boolean isNotBlank() {
        return !blank;
    }

    @Transient
    public GeoPoint getGeoPoint() {
        if (null != coordinate && coordinate.length == 2) {
            return new GeoPoint(coordinate[1], coordinate[0]);
        }

        return null;
    }

    @Override
    public String toString() {
        return "DecodedAddress{" +
            "formattedAddress='" + formattedAddress + '\'' +
            ", area='" + area + '\'' +
            ", town='" + town + '\'' +
            ", district='" + district + '\'' +
            ", state='" + state + '\'' +
            ", stateShortName='" + stateShortName + '\'' +
            ", postalCode='" + postalCode + '\'' +
            ", country='" + country + '\'' +
            ", countryShortName='" + countryShortName + '\'' +
            ", coordinate=" + Arrays.toString(coordinate) +
            ", placeId='" + placeId + '\'' +
            ", blank=" + blank +
            '}';
    }
}

