package com.noqapp.service;

import com.google.maps.model.GeocodingResult;
import com.noqapp.repository.BizStoreManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * User: hitender
 * Date: 6/16/17 9:36 PM
 */
class ExternalServiceTest {

    @Mock private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        /* Its IP protected. */
        externalService = new ExternalService("AIzaSyDUM3yIIrwrx3ciwZ57O9YamC4uISWAlAk", 0, bizStoreManager);
    }

    @Test
    void computeNextRunTimeAtUTC_Match_Time() {
        ZonedDateTime nyc = externalService.computeNextRunTimeAtUTC(TimeZone.getTimeZone("America/New_York"), 20, 0);
        ZonedDateTime pst = externalService.computeNextRunTimeAtUTC(TimeZone.getTimeZone("PST"), 17, 0);
        assertEquals(nyc, pst, "Both dates should be same");
    }

    @Test
    @DisplayName ("Null when address supplied is empty")
    void getGeocodingResults_EmptyAddress() {
        assertNull(externalService.getGeocodingResults(""), "Null when address is empty");
    }                                           

    @Disabled
    void getGeocodingResults_InvalidAddress_Without_Commas() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("1234 Test Circuit Sunnyvale CA 94089");
        assertEquals(1, geocodingResults.length);
    }

    @Test
    void getGeocodingResults_InvalidAddress_With_Commas() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("1234 Test Circuit, Sunnyvale, CA 94089");
        assertEquals(1, geocodingResults.length);
    }

    @Test
    @DisplayName("Break of a loop if the address could not be shortened")
    void getGeocodingResults_InvalidAddress_Break_Loop() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("1234TestCircuitSunnyvaleCA94089");
        assertNull(geocodingResults);
    }
}