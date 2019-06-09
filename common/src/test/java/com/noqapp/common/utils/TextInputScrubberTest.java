package com.noqapp.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * User: hitender
 * Date: 2019-06-09 09:19
 */
class TextInputScrubberTest {

    @Test
    void sanitize() {
        Assertions.assertEquals("", TextInputScrubber.sanitize("<html></html>"));
        Assertions.assertEquals("", TextInputScrubber.sanitize("<script></script>"));
        Assertions.assertEquals(
            "\n" +
                "Here is some \"Text\" that I'd like to be \"escaped\" for HTML\n" +
                "& here is some Swedish: Tack. Vars?god.\n",
            TextInputScrubber.sanitize("<sometext>\n" +
                "Here is some \"Text\" that I'd like to be \"escaped\" for HTML\n" +
                "& here is some Swedish: Tack. Vars?god.\n" +
                "</sometext>"));
        Assertions.assertEquals("MyName", TextInputScrubber.sanitize("<p>MyName<p>"));
    }

    @Test
    void decode() {
        Assertions.assertEquals("https://mywebsite/docs/english/site/mybook.do?request_type", TextInputScrubber.decode("https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type"));
        Assertions.assertEquals("https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type", TextInputScrubber.sanitize("https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type"));
        Assertions.assertEquals("https://mywebsite/docs/english/site/mybook.do?request_type", TextInputScrubber.sanitize("https://mywebsite/docs/english/site/mybook.do?request_type"));
    }

    @Test
    void decodeAndSanitize() {
        Assertions.assertEquals("https://mywebsite/docs/english/site/mybook.do?request_type", TextInputScrubber.sanitize(TextInputScrubber.decode("https://mywebsite/docs/english/site/mybook.do?request_type")));
        Assertions.assertEquals("https://mywebsite/docs/english/site/mybook.do?request_type", TextInputScrubber.sanitize(TextInputScrubber.decode("https%3A%2F%2Fmywebsite%2Fdocs%2Fenglish%2Fsite%2Fmybook.do%3Frequest_type")));
    }
}