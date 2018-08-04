package com.noqapp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * hitender
 * 8/4/18 5:56 PM
 */
class RandomStringTest {

    @Test
    void generateEmailAddressWithDomain() {
        assertEquals(
            "my.name.yourname.1@mail.noqapp.com",
            RandomString.generateEmailAddressWithDomain(
                new ScrubbedInput("My Name"),
                new ScrubbedInput("YourName"),
                "1001"));

        assertEquals(
            "my.name.your.name.1@mail.noqapp.com",
            RandomString.generateEmailAddressWithDomain(
                new ScrubbedInput("My Name"),
                new ScrubbedInput("Your Name"),
                "1001"));

        assertEquals(
            "my.name.your.name.1@mail.noqapp.com",
            RandomString.generateEmailAddressWithDomain(
                new ScrubbedInput("My Name"),
                new ScrubbedInput("Your     Name   "),
                "1001"));

        assertEquals(
            "my.name.yourname.1@mail.noqapp.com",
            RandomString.generateEmailAddressWithDomain(
                new ScrubbedInput(" My Name "),
                new ScrubbedInput("  YourName  "),
                "1001"));

        assertEquals(
            "my.name.yourname.1@mail.noqapp.com",
            RandomString.generateEmailAddressWithDomain(
                new ScrubbedInput("My     Name"),
                new ScrubbedInput("YourName"),
                "1001"));
    }
}