package com.noqapp.search.elastic.helper;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.annotation.Transient;

/**
 * hitender
 * 2/19/18 11:16 PM
 */
public class GeoIP {
    private String ipAddress;
    private String city;
    private double latitude;
    private double longitude;

    @Transient
    private GeoPoint geoPoint;

    public GeoIP() {
    }

    public GeoIP(String ipAddress, String city, double latitude, double longitude) {
        this.ipAddress = ipAddress;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;

        this.geoPoint = new GeoPoint(latitude, longitude);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    @Transient
    public String getGeoHash() {
        if (StringUtils.isBlank(ipAddress)) {
            return "";
        }

        return geoPoint.getGeohash();
    }

    @Override
    public String toString() {
        return "GeoIP{" +
                "ipAddress='" + ipAddress + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", geoPoint=" + geoPoint +
                '}';
    }
}
