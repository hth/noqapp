package com.noqapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.noqapp.domain.shared.DecodedAddress;
import com.noqapp.repository.BizStoreManager;

import com.google.maps.model.GeocodingResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * User: hitender
 * Date: 6/16/17 9:36 PM
 */
class ExternalServiceTest {

    @Mock private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        /* Its IP protected. */
        externalService = new ExternalService("AIzaSyDUM3yIIrwrx3ciwZ57O9YamC4uISWAlAk", 0, bizStoreManager);
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

    @Disabled
    void getGeocodingResults_InvalidAddress_With_Commas() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("1234 Test Circuit, Sunnyvale, CA 94089");
        assertEquals(1, geocodingResults.length);
    }

    @Disabled
    @DisplayName("Break of a loop if the address could not be shortened")
    void getGeocodingResults_InvalidAddress_Break_Loop() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("1234TestCircuitSunnyvaleCA94089");
        assertNull(geocodingResults);
    }

    @Test
    @DisplayName("Test with valid address")
    void getGeocodingResults_ValidAddress() {
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults("665 W Olive Ave, Sunnyvale, CA 94086, USA");
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, 0);
        assertEquals("665 W Olive Ave, Sunnyvale, CA 94086, USA", decodedAddress.getFormattedAddress(), "Address should match");
    }
}
