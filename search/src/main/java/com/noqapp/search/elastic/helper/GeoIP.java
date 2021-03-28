package com.noqapp.search.elastic.helper;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * hitender
 * 2/19/18 11:16 PM
 */
public class GeoIP {
    private String ipAddress;
    private String area;
    private String town;
    private String cityName;
    private double latitude;
    private double longitude;

    @Transient
    private GeoPoint geoPoint;

    public GeoIP() {
    }

    public GeoIP(String ipAddress, String cityName, double latitude, double longitude) {
        this.ipAddress = ipAddress;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;

        this.geoPoint = new GeoPoint(latitude, longitude);
    }

    public GeoIP(String ipAddress, String area, String town, String cityName, double latitude, double longitude) {
        this.area = area;
        this.town = town;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;

        this.geoPoint = new GeoPoint(latitude, longitude);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public String getCityName() {
        return cityName;
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
                "ip='" + ipAddress + '\'' +
                ", cityName='" + cityName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", geoPoint=" + geoPoint +
                '}';
    }
}
