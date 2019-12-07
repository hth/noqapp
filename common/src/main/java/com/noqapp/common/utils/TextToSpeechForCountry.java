package com.noqapp.common.utils;

/**
 * https://cloud.google.com/text-to-speech/docs/voices
 * User: hitender
 * Date: 12/8/19 4:02 AM
 */
public class TextToSpeechForCountry {

    public static String nationalLanguageCode(String countryShortName) {
        switch (countryShortName) {
            case "IN":
                return "hi-IN";
            case "US":
                return "en-US";
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
                return "en-US-Standard-D";
            default:
                return "en-GB-Wavenet-D";
        }
    }

    public static String nowServing(String languageCode, int currentlyServing, String displayName, String goTo) {
        switch(languageCode) {
            case "hi-IN":
                return "No Queue Token संख्या " + currentlyServing + " कृप्या " + displayName + " " + goTo + " पर जाएं";
            case "en-IN":
            case "en-US":
                return "No Queue Token number " + currentlyServing + ", please visit " + displayName + ", in " + goTo;
            default:
                throw new UnsupportedOperationException("Reached un-supported condition");

        }
    }
}
