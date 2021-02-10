package com.noqapp.search.elastic.helper;

/**
 * hitender
 * 2/10/21 11:41 AM
 */
public class IpCoordinate {

    private String ip;
    private double[] coordinate;

    public String getIp() {
        return ip;
    }

    public IpCoordinate setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public IpCoordinate setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }
}
