package com.noqapp.domain.neo4j;

import org.elasticsearch.common.geo.GeoPoint;
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
    private String id;

    @Property("lng")
    private double longitude;

    @Property("lat")
    private double latitude;

    public static LocationN4j newInstance(double longitude, double latitude) {
        return new LocationN4j()
            .setLongitude(longitude)
            .setLatitude(latitude)
            .setId(new GeoPoint(latitude, longitude).getGeohash());
    }

    public String getId() {
        return id;
    }

    public LocationN4j setId(String id) {
        this.id = id;
        return this;
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
}
