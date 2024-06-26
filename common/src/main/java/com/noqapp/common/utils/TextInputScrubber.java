package com.noqapp.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * User: hitender
 * Date: 11/18/16 6:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
class TextInputScrubber {
    private static final Logger LOG = LoggerFactory.getLogger(TextInputScrubber.class);

    private static final PolicyFactory policyFactory = new HtmlPolicyBuilder().toFactory();

    private TextInputScrubber() {
    }

    /**
     * Takes the input and removes all script and html tags it finds, returning what is left.
     * This also converts any HTML character entities to their UTF-8 character equivalents.
     *
     * @param input The input text to scrub
     * @return the scrubbed text or just a blank string if nothing is left
     */
    static String sanitize(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        int preSanitizeLength;
        String sanitizedText = input;
        while (true) {
            preSanitizeLength = sanitizedText.length();
            sanitizedText = StringEscapeUtils.unescapeHtml4(policyFactory.sanitize(sanitizedText));

            if (sanitizedText.length() > preSanitizeLength) {
                LOG.warn("input grew: [{}]", input);
                return "";
            } else if (preSanitizeLength == sanitizedText.length()) {
                return sanitizedText;
            }
        }
    }

    static String decode(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        try {
            return URLDecoder.decode(input, ScrubbedInput.UTF_8);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to decode the input={}, next trying replacing text.", input);
            String replacedInput = input.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            replacedInput = replacedInput.replaceAll("\\+", "%2B");
            try {
                return URLDecoder.decode(replacedInput, ScrubbedInput.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                LOG.error("Unable to decode the input={}", input, e);
                return "";
            }
        } catch (Exception e) {
            LOG.error("Unable to decode the input={}", input, e);
            return "";
        }
    }
}

