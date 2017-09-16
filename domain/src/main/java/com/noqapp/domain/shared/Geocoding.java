package com.noqapp.domain.shared;

import com.google.maps.model.GeocodingResult;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * User: hitender
 * Date: 9/15/17 4:21 AM
 */
public class Geocoding {

    private GeocodingResult[] results;
    private String address;

    private HashMap<String, DecodedAddress> foundAddresses = new LinkedHashMap<>();
    private boolean addressMisMatch;

    private Geocoding(GeocodingResult[] results, String address) {
        this.results = results;
        this.address = address;

        if (null != results && results.length > 0) {
            for (int counter = 0; counter < results.length; counter++) {
                DecodedAddress decodedAddress = DecodedAddress.newInstance(results, counter);
                foundAddresses.put(decodedAddress.getPlaceId(), decodedAddress);
                if (!addressMisMatch) {
                    addressMisMatch = decodedAddress.getFormattedAddress().hashCode() != address.hashCode();
                }
            }
        }
    }

    public static Geocoding newInstance(GeocodingResult[] results, String address) {
        return new Geocoding(results, address);
    }

    public GeocodingResult[] getResults() {
        return results;
    }

    /**
     * Address entered, searched.
     * Example:
     *  Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur
     *  OR Tambo Airport Rd, Level 2, Domtex Building, OR Tambo International Airport, Johannesburg, 1627, South Africa
     *
     * External source could not locate these address. So to preserve whats entered (as un-altered) we save the address
     * entered by user.
     */
    public String getAddress() {
        return address;
    }

    public HashMap<String, DecodedAddress> getFoundAddresses() {
        return foundAddresses;
    }

    public boolean isAddressMisMatch() {
        return addressMisMatch;
    }
}
