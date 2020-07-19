package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * User: hitender
 * Date: 7/22/18 11:27 AM
 */
class BizStoreEntityTest {

    private BizStoreEntity bizStore1 = BizStoreEntity.newInstance();
    private BizStoreEntity bizStore2 = BizStoreEntity.newInstance();
    private BizStoreEntity bizStore3 = BizStoreEntity.newInstance();
    private BizStoreEntity bizStore4 = BizStoreEntity.newInstance();

    @BeforeEach
    void setUp() {
        bizStore1.setAddress("665 W Olive Ave, Sunnyvale, CA 94086, USA")
            .setArea("Sunnyvale")
            .setTown("Sunnyvale")
            .setDistrict("Santa Clara County")
            .setState("California")
            .setStateShortName("CA");

        bizStore2.setAddress("Plot 12-13, Sector 20, Kopar Khairane, Navi Mumbai, Maharashtra 400709, India")
            .setArea("Kopar Khairane")
            .setTown("Navi Mumbai")
            .setDistrict("Thane")
            .setState("Maharashtra")
            .setStateShortName("MH");

        bizStore3.setAddress("Near PHED Office, Ladnun, Nagaur, Rajasthan 341306, India")
            .setArea("Shaheria Bass")
            .setTown("Ladnun")
            .setDistrict("Nagaur")
            .setState("Rajasthan")
            .setStateShortName("RJ");

        bizStore4.setAddress("123, Sahid Bhagat Singh Colony, Jb Nagar, Chakala, Andheri (E), Mumbai East 400059")
            .setArea("Jb Nagar")
            .setTown("Chakala")
            .setDistrict("Mumbai")
            .setState("Maharashtra")
            .setStateShortName("MH");
    }

    @Test
    void getAddressWrapped() {
        String address1 = bizStore1.getAddressWrapped();
        assertEquals("665 W Olive Ave<br/> Sunnyvale, CA 94086, USA", address1);

        String address2 = bizStore2.getAddressWrapped();
        assertEquals("Plot 12-13<br/> Sector 20, Kopar Khairane, Navi Mumbai, Maharashtra 400709, India", address2);

        String address3 = bizStore3.getAddressWrapped();
        assertEquals("Near PHED Office<br/> Ladnun, Nagaur, Rajasthan 341306, India", address3);
    }

    @Test
    void getAddressWrappedMore() {
        String address1 = bizStore1.getAddressWrappedMore();
        assertEquals("665 W Olive Ave<br/> Sunnyvale<br/> CA 94086, USA", address1);

        String address2 = bizStore2.getAddressWrappedMore();
        assertEquals("Plot 12-13<br/> Sector 20<br/> Kopar Khairane, Navi Mumbai, Maharashtra 400709, India", address2);

        String address3 = bizStore3.getAddressWrappedMore();
        assertEquals("Near PHED Office<br/> Ladnun<br/> Nagaur, Rajasthan 341306, India", address3);
    }

    @Test
    void getAddressWrappedFunky() {
        String address1 = bizStore1.getAddressWrappedFunky();
        assertEquals("665 W Olive Ave, <br/>Sunnyvale,<br/>CA 94086, USA", address1);

        String address2 = bizStore2.getAddressWrappedFunky();
        assertEquals("Plot 12-13, Sector 20, <br/>Kopar Khairane, Navi Mumbai,<br/>Maharashtra 400709 India", address2);

        String address3 = bizStore3.getAddressWrappedFunky();
        assertEquals("Near PHED Office<br/> Ladnun<br/> Nagaur, Rajasthan 341306, India", address3);

        String address4 = bizStore4.getAddressWrappedFunky();
        assertEquals("123, Sahid Bhagat Singh Colony, <br/>Jb Nagar, Chakala,<br/>Andheri (E) Mumbai East 400059", address4);
    }
}
