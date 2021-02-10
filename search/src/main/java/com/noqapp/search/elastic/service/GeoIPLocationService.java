package com.noqapp.search.elastic.service;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.search.elastic.helper.GeoIP;
import com.noqapp.search.elastic.helper.IpCoordinate;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 2/19/18 11:14 PM
 */
@Service
public class GeoIPLocationService {
    private static final Logger LOG = LoggerFactory.getLogger(GeoIPLocationService.class);
    private DatabaseReader dbReader;

    @Autowired
    public GeoIPLocationService(DatabaseReader dbReader) {
        this.dbReader = dbReader;
    }

    public GeoIP getLocation(String ip) {
        CityResponse response = cityResponse(ip);
        if (null == response) {
            return new GeoIP();
        }

        String cityName = response.getCity().getName();
        double latitude = response.getLocation().getLatitude();
        double longitude = response.getLocation().getLongitude();
        return new GeoIP(ip, cityName, latitude, longitude);
    }

    @Mobile
    public double[] getLocationAsDouble(List<String> ips) {
        for (String ip : ips) {
            CityResponse response = cityResponse(ip);
            if (null != response) {
                double latitude = response.getLocation().getLatitude();
                double longitude = response.getLocation().getLongitude();
                return new double[]{longitude, latitude};
            }
        }
        return null;
    }

    @Mobile
    public IpCoordinate computeIpCoordinate(Set<String> ips) {
        for (String ip : ips) {
            CityResponse response = cityResponse(ip);
            if (null != response) {
                double latitude = response.getLocation().getLatitude();
                double longitude = response.getLocation().getLongitude();
                new IpCoordinate()
                    .setCoordinate(new double[]{longitude, latitude})
                    .setIp(ip);
            }
        }
        return null;
    }

    private CityResponse cityResponse(String ip) {
        LOG.debug("From ip={}", ip);

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return dbReader.city(ipAddress);
        } catch (IOException e) {
            LOG.warn("Failed parsing ip={} reason={}", ip, e.getLocalizedMessage());
        } catch (GeoIp2Exception e) {
            LOG.warn("Failed fetching geoIP for ip={} reason={}", ip, e.getLocalizedMessage());
        }

        return null;
    }

    public String getTimeZone(String ip) {
        LOG.debug("From ip={}", ip);

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);
            return response.getLocation().getTimeZone();
        } catch (IOException e) {
            LOG.warn("Failed parsing ip={} reason={}", ip, e.getLocalizedMessage());
        } catch (GeoIp2Exception e) {
            LOG.warn("Failed fetching geoIP for ip={} reason={}", ip, e.getLocalizedMessage());
        }

        return null;
    }

    public int requestOriginatorTime(String ipAddress) {
        String requestOriginatorTimeZone = getTimeZone(ipAddress);
        LocalTime localTime = DateUtil.getTimeAtTimeZone(requestOriginatorTimeZone);
        LOG.info("Web requester originator time ip={} requestOriginatorTimeZone={} localTime={}",
            ipAddress,
            requestOriginatorTimeZone,
            localTime);
        return DateFormatter.getTimeIn24HourFormat(localTime);
    }
}
