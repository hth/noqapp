package com.noqapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals("urc54asp", bizName.addBusinessNameWithoutSpaceToTag());

        bizName = new BizNameEntity()
            .setBusinessName("URC ");

        assertEquals("urc", bizName.addBusinessNameWithoutSpaceToTag());

        bizName = new BizNameEntity()
            .setBusinessName("URC 54, ASP");

        assertEquals("urc54asp", bizName.addBusinessNameWithoutSpaceToTag());
    }
}
