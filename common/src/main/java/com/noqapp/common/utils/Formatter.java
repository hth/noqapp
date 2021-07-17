package com.noqapp.common.utils;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance;
import static com.noqapp.common.utils.Constants.TIME_SLOT_PATTERN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/** Define all singleton here */
enum FormatterSingleton {
    INSTANCE;

    protected ScriptEngine engine() {
        return new ScriptEngineManager().getEngineByName("JavaScript");
    }

    protected PhoneNumberUtil phoneInstance() {
        return getInstance();
    }

    protected NumberFormat currencyInstance() {
        return NumberFormat.getCurrencyInstance();
    }
}

/**
 * User: hitender
 * Date: 11/18/16 6:10 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public final class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

    /* Defaults to US. */
    private static final String FORMAT_TO_US = "US";

    public static DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HHmm");
    private static DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private static final PhoneNumberUtil PHONE_INSTANCE = FormatterSingleton.INSTANCE.phoneInstance();
    private static final NumberFormat CURRENCY_INSTANCE = FormatterSingleton.INSTANCE.currencyInstance();
    private static final ScriptEngine SCRIPT_INSTANCE = FormatterSingleton.INSTANCE.engine();
    private static Map<String, Locale> localeMap;

    private Formatter() {
    }

    /**
     * Helps format phone numbers.
     *
     * @param phone           Phone number
     * @param formatToCountry Format phone to a country type
     * @return Formatted phone string
     */
    public static String phoneNationalFormat(String phone, String formatToCountry) {
        try {
            if (StringUtils.isBlank(phone)) {
                LOG.debug("phone number blank");
                return "";
            }

            Phonenumber.PhoneNumber phoneNumber;
            if (StringUtils.isBlank(formatToCountry)) {
                phoneNumber = PHONE_INSTANCE.parse(phone, FORMAT_TO_US);
            } else {
                phoneNumber = PHONE_INSTANCE.parse(phone, formatToCountry);
            }
            return PHONE_INSTANCE.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            LOG.warn("Failed parsing phone number={} reason={}", phone, e.getLocalizedMessage(), e);
            return StringUtils.EMPTY;
        }
    }

    public static boolean isValidPhone(String phone, String countryShortName) {
        try {
            PHONE_INSTANCE.parse(phone, countryShortName);
            return true;
        } catch (NumberParseException e) {
            LOG.error("Error parsing phone={} countryShortName={}", phone, countryShortName);
            return false;
        }
    }

    /**
     * Strip all the characters other than number.
     *
     * @param phone
     * @return
     */
    public static String phoneCleanup(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            return phone.replaceAll("[^0-9]", "");
        }
        return phone;
    }

    /**
     * From country short code like "US" returns country dial code like "1".
     *
     * @param countryShortCode
     * @return
     */
    public static int findCountryCodeFromCountryShortCode(String countryShortCode) {
        return PHONE_INSTANCE.getCountryCodeForRegion(countryShortCode.toUpperCase());
    }

    public static String phoneFormatter(String phone, String countryShortName) {
        return phoneNationalFormat(phone, countryShortName);
    }

    public static String phoneNumberWithCountryCode(String phone, String countryShortName) {
        try {
            Phonenumber.PhoneNumber phoneNumber = PHONE_INSTANCE.parse(phone, countryShortName);
            LOG.info("PhoneNumber with phone={} countryShortName={} countryCode={} nationalNumber={} leadingZeros={}",
                phone,
                countryShortName,
                phoneNumber.getCountryCode(),
                phoneNumber.getNationalNumber(),
                phoneNumber.getNumberOfLeadingZeros());

            return phoneNumber.getCountryCode() + String.valueOf(phoneNumber.getNationalNumber());
        } catch (Exception e) {
            LOG.error("Failed to parse phone={} countryShortName={} reason={}", phone, countryShortName, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed parsing country code");
        }
    }

    public static boolean isValidCountryCode(String countryShortName) {
        return PHONE_INSTANCE.getCountryCodeForRegion(countryShortName) != 0;
    }

    /**
     * Removes country code from phone.
     * Note: Do not pass a Raw Phone as it will strip few digits.
     *
     * @param phone should begin with +
     * @return
     */
    public static String phoneStripCountryCode(String phone) {
        try {
            assertThat(phone, containsString("+"));
            /* Remove `+` sign with country code before sending the number back. */
            return RegExUtils.removeFirst(phone, "\\+" + findCountryCode(phone));
        } catch (AssertionError a) {
            LOG.warn("Phone number should begin with + phone={}", phone);
            try {
                return phoneStripCountryCode("+" + phone);
            } catch (Exception e) {
                LOG.error("Failed getting country code from phone={} reason={}", phone, e.getLocalizedMessage(), e);
                throw new RuntimeException("Failed finding country code from phone");
            }
        }
    }

    /**
     * Parse for country code from phone.
     *
     * @param phone should begin with +
     * @return
     */
    public static int findCountryCode(String phone) {
        try {
            assertThat(phone, containsString("+"));
            Phonenumber.PhoneNumber phoneNumber = PHONE_INSTANCE.parse(phone, "");
            return phoneNumber.getCountryCode();
        } catch (NumberParseException e) {
            LOG.error("Failed to parse phone={} reason={}", phone, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed parsing country code");
        }
    }

    /**
     * Format phoneNationalFormat to international.
     * Formats phone 14080000004 to +1 408-000-0004
     *
     * @param phone
     * @param formatToCountry
     * @return
     */
    public static String phoneInternationalFormat(String phone, String formatToCountry) {
        try {
            if (StringUtils.isBlank(phone)) {
                LOG.debug("phoneInternationalFormat number blank");
                return "";
            }

            Phonenumber.PhoneNumber phoneNumber;
            if (StringUtils.isBlank(formatToCountry)) {
                phoneNumber = PHONE_INSTANCE.parse(phone, FORMAT_TO_US);
            } else {
                phoneNumber = PHONE_INSTANCE.parse(phone, formatToCountry);
            }
            String internationalFormat = PHONE_INSTANCE.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            LOG.info("International phone format={}", internationalFormat);
            return internationalFormat;
        } catch (NumberParseException e) {
            LOG.warn("Failed parsing phoneInternationalFormat number={} reason={}", phone, e.getLocalizedMessage(), e);
            return "";
        }
    }

    public static String resetPhoneToRawFormat(String phone, String countryShortName) {
        String internationalFormat = Formatter.phoneInternationalFormat(phone, countryShortName);
        String withoutInternationalCode = Formatter.phoneStripCountryCode(internationalFormat);
        return Formatter.phoneCleanup(withoutInternationalCode);
    }

    /**
     * Gets ISO country short name for international number supplied.
     *
     * @param countryCode
     * @return
     */
    public static String getCountryShortNameFromCountryCode(int countryCode) {
        String countryShortName = PHONE_INSTANCE.getRegionCodeForCountryCode(countryCode);
        if ("ZZ".equalsIgnoreCase(countryShortName)) {
            LOG.error("Failed to find countryShortName for countryCode={}", countryCode);
            throw new RuntimeException("Cannot find Country ISO code");
        }

        return countryShortName;
    }

    /** Gets ISO country short name for international number supplied. */
    public static String getCountryShortNameFromInternationalPhone(String phone) {
        /* Added for making phone number as international. */
        return getCountryShortNameFromCountryCode(findCountryCode("+" + phone));
    }


    private static String convertMilitaryTo12HourFormat(String rawTimestamp) {
        TemporalAccessor temporalAccessor = inputFormatter.parse(rawTimestamp);
        return outputFormatter.format(temporalAccessor);
    }

    /** Converts military time like '0010' to 12:10 AM' and '1000' to '10:00 AM' and '2345' to '11:45 PM'. */
    public static String convertMilitaryTo12HourFormat(int rawTimestamp) {
        return convertMilitaryTo12HourFormat(String.format(Locale.US, "%04d", rawTimestamp));
    }

    /** Converts timeSlot from 14:00 - 15:00 to 1400 */
    public static int getStartTimeFromTimeSlot(String timeSlot) {
        if (TIME_SLOT_PATTERN.matcher(timeSlot).matches()) {
            return Integer.parseInt(timeSlot.split(" ")[0].replace(":", ""));
        }
        return 0;
    }
}
