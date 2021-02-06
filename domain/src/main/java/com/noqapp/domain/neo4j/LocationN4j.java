package com.noqapp.domain.neo4j;

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

    private LocationN4j(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static LocationN4j newInstance(double longitude, double latitude) {
        return new LocationN4j(longitude, latitude);
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
