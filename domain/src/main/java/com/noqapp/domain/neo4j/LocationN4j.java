package com.noqapp.domain.neo4j;

import org.elasticsearch.common.geo.GeoPoint;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * hitender
 * 2/5/21 8:44 PM
 */
@NodeEntity("Location")
public class LocationN4j {

    @Id
    @GeneratedValue
    private Long id;

    @Property("lng")
    private double longitude;

    @Property("lat")
    private double latitude;

    @Property("geoPoint")
    private String geoPoint;

    private LocationN4j(double longitude, double latitude, String geoPoint) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.geoPoint = geoPoint;
    }

    public static LocationN4j newInstance(double longitude, double latitude) {
        return new LocationN4j(longitude, latitude, new GeoPoint(latitude, longitude).geohash());
    }

    public double getLongitude() {
        return longitude;
    }

    public LocationN4j setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public LocationN4j setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getGeoPoint() {
        return geoPoint;
    }

    public LocationN4j setGeoPoint(String geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }
}
