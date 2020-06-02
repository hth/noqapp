package com.noqapp.common.utils;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 11/18/16 6:09 PM
 */
public final class CommonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CommonUtil.class);

    public static final String AUTH_KEY_HIDDEN = "*********";
    public static final String UNAUTHORIZED = "Unauthorized";
    private static final Pattern p = Pattern.compile("\\{([^}]*)\\}");
    private static Random random;
    private static Map<String, String> languages;

    static {
        random = new Random();

        Map<String, String> temp = new HashMap<>();
        Arrays.stream(Locale.getISOLanguages()).map(Locale::new).forEachOrdered(loc -> temp.put(loc.getLanguage(), loc.getDisplayLanguage()));
        
        languages = temp.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private CommonUtil() {
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static List<ObjectId> convertStringArrayToObjectIdArray(List<String> ids) {
        List<ObjectId> objectIds = new ArrayList<>();
        if (null != ids) {
            objectIds.addAll(ids.stream().map(ObjectId::new).collect(Collectors.toList()));
        }
        return objectIds;
    }

    public static LatLng getLatLng(double[] coordinate) {
        return new LatLng(getLat(coordinate), getLng(coordinate));
    }

    private static double getLat(double[] coordinate) {
        if (null != coordinate) {
            return coordinate[1];
        } else {
            return 0.0;
        }
    }

    private static double getLng(double[] coordinate) {
        if (null != coordinate) {
            return coordinate[0];
        } else {
            return 0.0;
        }
    }

    /**
     * Gets next day of the week based on the day of the week supplied.
     *
     * @param dayOfWeek
     * @return
     */
    public static DayOfWeek getNextDayOfWeek(DayOfWeek dayOfWeek) {
        LOG.debug("Supplied with DayOfWeek={}", dayOfWeek);
        switch (dayOfWeek) {
            case MONDAY:
                return DayOfWeek.TUESDAY;
            case TUESDAY:
                return DayOfWeek.WEDNESDAY;
            case WEDNESDAY:
                return DayOfWeek.THURSDAY;
            case THURSDAY:
                return DayOfWeek.FRIDAY;
            case FRIDAY:
                return DayOfWeek.SATURDAY;
            case SATURDAY:
                return DayOfWeek.SUNDAY;
            case SUNDAY:
                return DayOfWeek.MONDAY;
            default:
                throw new UnsupportedOperationException("Reached Unsupported Condition");
        }
    }

    public static String getCountryNameFromIsoCode(String isoCode) {
        Locale l = new Locale("", isoCode);
        return l.getDisplayCountry();
    }

    /**
     * SN is prefix when not on Prod. This is to distinguish from Prod QR code. SN means Sandbox.
     */
    public static String generateCodeQR(String environment) {
        switch (environment) {
            case "dev":
            case "sandbox":
            case "prod":
                return generateHexFromObjectId();
            default:
                LOG.error("Failed finding Environment type={}", environment);
                throw new UnsupportedOperationException("Could not find Environment type " + environment);
        }
    }

    public static <T> Map<T, T> toMap(Set<T> set) {
        Map<T, T> map = new ConcurrentHashMap<>();
        set.forEach(t -> map.put(t, t)); //contains same key and value pair
        return map;
    }

    /**
     * Used when merchant is dispensing token for people without app.
     */
    public static String appendRandomToDeviceId(String did) {
        return did + "-" + RandomString.newInstance(6).nextString();
    }

    /**
     * Get time in 24 hour format.
     */
    public static int getTimeIn24HourFormat(ZonedDateTime zonedDateTime) {
        /* To make sure minute in time 11:06 AM is not represented as 116 but as 1106 hence string formatting. */
        return Integer.parseInt(zonedDateTime.getHour() + String.format(Locale.US, "%02d", zonedDateTime.getMinute()));
    }

    /**
     * This will get you system time in 24 hour format.
     */
    public static int getTimeIn24HourFormat() {
        return getTimeIn24HourFormat(ZonedDateTime.now());
    }

    public static String replaceLast(String string, String findString, String replacement) {
        int index = string.lastIndexOf(findString);
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + replacement + string.substring(index + findString.length());
    }

    public static String generateHexFromObjectId() {
        return ObjectId.get().toHexString();
    }

    public static String generateTransactionId(String storeId, int token) {
        return storeId.substring(18, 24)
            + "-" + token
            + "-" + (random.ints(0, 100).findFirst().getAsInt() + 100)
            + "-" + generateHexFromObjectId().substring(0, 8);
    }

    /**
     * For privacy reason, abbreviate user name on public forum.
     * First Name will become First N
     */
    public static String abbreviateName(String name) {
        try {
            String normalize = StringUtils.normalizeSpace(name);
            if (normalize.contains(" ")) {
                String[] splits = normalize.split(" ");
                if (splits.length >= 1) {
                    return splits[0] + " " + splits[1].substring(0, 1);
                }
            }
            return normalize;
        } catch (Exception e) {
            LOG.error("Abbreviate name {} reason={}", name, e.getLocalizedMessage());
            return name;
        }
    }

    /**
     * Parses error message to increase readability like { : "KUB", : "SONO" } to : "KUB", : "SONO"
     * E11000 duplicate key error collection: TABLE_NAME.COLLECTION NAME index: some_idx dup key: { : "KUB", : "SONO" }
     * @param errorMessage
     * @return
     */
    public static String parseForDuplicateException(String errorMessage) {
        Matcher m = p.matcher(errorMessage);
        if (m.find()) {
            return (m.group(1));
        }

        return null;
    }

    private static String currencyLocal(Locale locale) {
        return Currency.getInstance(locale).getSymbol();
    }

    public static String currencyLocal(String countryCode) {
        Assert.hasText(countryCode, "Country code cannot be empty");
        return currencyLocal(new Locale("en", countryCode));
    }
    
    public static String displayWithCurrencyCode(String orderPrice, String countryCode) {
        return currencyLocal(countryCode) + orderPrice;
    }

    public static Map<String, String> getLanguages() {
        return languages;
    }

    public static Map<String, String> localeToLanguage(Set<Locale> locales) {
        Map<String, String> a = new LinkedHashMap<>();
        for (Locale locale : locales) {
            a.put(locale.toLanguageTag(), locale.getDisplayCountry() + " (" + locale.getDisplayLanguage() + ")");
        }

        /* Sort by value. */
        return a.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static String retrieveIPV4(String fromDevice, String fromRequest) {
        LOG.info("Send ips are {} {}", fromDevice, fromRequest);
        try {
            InetAddress address = InetAddress.getByName(fromDevice);
            if (address instanceof Inet6Address) {
                return fromRequest;
            }
            return fromDevice;
        } catch (UnknownHostException e) {
            LOG.error("Failed on unknown host fromDevice={} fromRequest={} reason={}", fromDevice, fromRequest, e.getLocalizedMessage());
        }

        return fromDevice;
    }
}
