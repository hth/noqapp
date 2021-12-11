package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 12/11/21 8:06 PM
 */
class BizNameEntityTest {

    @Test
    void getAddressWrapped() {
        BizNameEntity bizName = new BizNameEntity()
            .setBusinessName("URC 54 ASP");

        assertEquals("urc 54 asp", bizName.computeTag());
    }
}
