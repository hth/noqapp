package com.noqapp.search.elastic.config;

import com.maxmind.geoip2.DatabaseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * hitender
 * 2/19/18 8:03 AM
 */
@Configuration
public class IPGeoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(IPGeoConfiguration.class);

    @Value("${geo.db.location:classpath:/geo_db/GeoLite2-City.mmdb}")
    private Resource dbLocation;

    @Bean
    public DatabaseReader getDatabaseReader() throws IOException {
        return new DatabaseReader.Builder(dbLocation.getFile()).build();
    }
}
