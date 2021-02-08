package com.noqapp.domain.neo4j.queryresult;

import org.springframework.data.neo4j.annotation.QueryResult;

import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

/**
 * hitender
 * 2/8/21 4:11 AM
 */
@QueryResult
public class BusinessDistanceFromUserLocation {

    @Property("travelDistance")
    double travelDistance;

    @Property("bizNameId")
    String bizNameId;

    public double getTravelDistance() {
        return travelDistance;
    }

    public BusinessDistanceFromUserLocation setTravelDistance(double travelDistance) {
        this.travelDistance = travelDistance;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BusinessDistanceFromUserLocation setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessDistanceFromUserLocation that = (BusinessDistanceFromUserLocation) o;
        return Double.compare(that.travelDistance, travelDistance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelDistance);
    }
}
