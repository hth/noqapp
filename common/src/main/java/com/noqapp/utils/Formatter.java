package com.noqapp.utils;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class Formatter {
    private static final Logger LOG = LoggerFactory.getLogger(Formatter.class);

    /* Defaults to US. */
    private static final String FORMAT_TO_US = "US";
    private static final SimpleDateFormat SDF_SMALL = new SimpleDateFormat("MM-dd-yyyy");

    private static final PhoneNumberUtil PHONE_INSTANCE = FormatterSingleton.INSTANCE.phoneInstance();
    private static final NumberFormat CURRENCY_INSTANCE = FormatterSingleton.INSTANCE.currencyInstance();
    private static final ScriptEngine SCRIPT_INSTANCE = FormatterSingleton.INSTANCE.engine();
    private static Map<String, Locale> localeMap;

    private Formatter() {
    }

    //Refer bug #3
    //TODO(hth) may be change this method to support just item format and net format.
    //TODO(hth) Means have two method with scale of 2 and 4. 2 scale for total; and 4 scale for
    public static BigDecimal getCurrencyFormatted(String value) throws ParseException, NumberFormatException {
        BigDecimal d;
        try {
            try {
                Object object = SCRIPT_INSTANCE.eval(value);
                d = new BigDecimal(object.toString()).setScale(Maths.SCALE_FOUR, BigDecimal.ROUND_HALF_UP);
            } catch (ScriptException se) {
                LOG.warn("Failed parsing number value={} reason={}", value, se.getLocalizedMessage(), se);
                throw new NumberFormatException("Failed parsing number value: " + value + ", exception: " + se.getLocalizedMessage());
            }
            //d = new BigDecimal(value).setScale(Maths.SCALE_FOUR, BigDecimal.ROUND_HALF_UP);\
            return d;
        } catch (NumberFormatException nfe) {
            LOG.warn("Failed parsing number value={} reason={}", value, nfe.getLocalizedMessage(), nfe);
            throw new NumberFormatException("Failed parsing number value: " + value + ", exception: " + nfe);
        }
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

    public static String toSmallDate(Date date) {
        return SDF_SMALL.format(date);
    }

    /**
     * Strip all the characters other than number.
     *
     * @param phone
     * @return
     */
    public static String phoneCleanup(String phone) {
        if (StringUtils.isNotEmpty(phone)) {
            return phone.replaceAll("[^0-9]", "");
        }
        return phone;
    }

    public static String phoneFormatter(String phone, String countryShortName) {
        return phoneNationalFormat(phone, countryShortName);
    }

    public static String phoneNumberWithCountryCode(String phone, String countryShortName) {
        try {
            int countryCode = PHONE_INSTANCE.getCountryCodeForRegion(countryShortName);
            if (countryCode != 0) {
                return countryCode + phone;
            }
            throw new RuntimeException("Failed parsing country code");
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
     *
     * @param phone should begin with +
     * @return
     */
    public static String phoneStripCountryCode(String phone) {
        assertThat(phone, containsString("+"));
        return StringUtils.removeFirst(phone, String.valueOf(findCountryCode(phone)));
    }

    /**
     * Parse for country code from phone.
     *
     * @param phone should begin with +
     * @return
     */
    public static int findCountryCode(String phone) {
        assertThat(phone, containsString("+"));
        try {
            Phonenumber.PhoneNumber phoneNumber = PHONE_INSTANCE.parse(phone, "");
            return phoneNumber.getCountryCode();
        } catch (NumberParseException e) {
            LOG.error("Failed to parse phone={} reason={}", phone, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed parsing country code");
        }
    }

    /**
     * Format phoneNationalFormat to international.
     *
     * @param phone
     * @param formatToCountry
     * @return
     */
    public static String phoneInternationalFormat(String phone, String formatToCountry) {
        try {
            if (StringUtils.isBlank(phone)) {
                LOG.debug("phoneNationalFormat number blank");
                return "";
            }

            Phonenumber.PhoneNumber phoneNumber;
            if (StringUtils.isBlank(formatToCountry)) {
                phoneNumber = PHONE_INSTANCE.parse(phone, FORMAT_TO_US);
            } else {
                phoneNumber = PHONE_INSTANCE.parse(phone, formatToCountry);
            }
            return PHONE_INSTANCE.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            LOG.warn("Failed parsing phoneNationalFormat number={} reason={}", phone, e.getLocalizedMessage(), e);
            return StringUtils.EMPTY;
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

    /**
     * Gets ISO country short name for international number supplied.
     *
     * @param phone
     * @return
     */
    public static String getCountryShortNameFromInternationalPhone(String phone) {
        return getCountryShortNameFromCountryCode(findCountryCode(phone));
    }
}
