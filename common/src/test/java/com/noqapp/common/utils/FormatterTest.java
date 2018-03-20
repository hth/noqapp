package com.noqapp.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * hitender
 * 12/26/17 8:00 PM
 */
class FormatterTest {

    @ParameterizedTest
    @ValueSource(strings = {"08602662666", "918602662666"})
    void phoneNationalFormat(String phoneRaw) {
        String formattedPhone = Formatter.phoneNationalFormat(phoneRaw, "IN");
        Assertions.assertEquals("086026 62666", formattedPhone);
    }

    @Test
    void isValidPhone() {
    }

    @Test
    void toSmallDate() {
    }

    @Test
    void phoneCleanup() {
    }

    @Test
    void findCountryCodeFromCountryShortCode() {
    }

    @Test
    void phoneFormatter() {
    }

    @Test
    void phoneNumberWithCountryCode() {
    }

    @Test
    void isValidCountryCode() {
    }

    @Test
    void phoneStripCountryCode() {
    }

    @Test
    void findCountryCode() {
    }

    @Test
    void phoneInternationalFormat() {
    }

    @Test
    void resetPhoneToRawFormat() {
    }

    @Test
    void getCountryShortNameFromCountryCode() {
    }

    @Test
    void getCountryShortNameFromInternationalPhone() {
    }

    @Test
    void convertMilitaryTo12HourFormat() {
    }
}
