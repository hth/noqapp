package com.noqapp.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://cloud.google.com/text-to-speech/docs/voices
 * User: hitender
 * Date: 12/8/19 4:02 AM
 */
public class TextToSpeechForCountry {
    private static final Logger LOG = LoggerFactory.getLogger(TextToSpeechForCountry.class);

    public static String nationalLanguageCode(String countryShortName) {
        switch (countryShortName) {
            case "IN":
                return "hi-IN";
            case "US":
                return "en-US";
            case "UK":
                return "en-GB";
            default:
                return null;
        }
    }

    public static String foreignLanguageCode(String countryShortName) {
        switch (countryShortName) {
            case "IN":
                return "en-IN";
            case "US":
                return "en-US";
            default:
                return "en-GB";
        }
    }

    public static String supportedVoice(String languageCode) {
        switch (languageCode) {
            case "hi-IN":
                return "hi-IN-Wavenet-A";
            case "en-IN":
                return "en-IN-Wavenet-C";
            case "en-US":
                return "en-US-Wavenet-D";
            case "en-UK":
            default:
                return "en-GB-Wavenet-D";
        }
    }
}
