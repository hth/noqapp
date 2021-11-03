package com.noqapp.common.utils;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.google.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.util.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

    /* Will keep out bob @ aol.com (spaces in emails) or steve (no domain at all) or mary@aolcom (no period before .com), I use */
    private static final Pattern mailPattern = Pattern.compile("^\\S+@\\S+\\.\\S+$");
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

    public static double[] getCoordinates(String longitude, String latitude) {
        return CommonUtil.getCoordinates(Double.parseDouble(longitude), Double.parseDouble(latitude));
    }

    public static double[] getCoordinates(double longitude, double latitude) {
        return new double[]{longitude, latitude};
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

    /** SN is prefix when not on Prod. This is to distinguish from Prod QR code. SN means Sandbox. */
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

    /** Used when business is dispensing token for people without app. */
    public static String appendRandomToDeviceId(String did) {
        return did + "-" + RandomString.newInstance(6).nextString();
    }

    /** Get time in 24-hour format. */
    public static int getTimeIn24HourFormat(ZonedDateTime zonedDateTime) {
        /* To make sure minute in time 11:06 AM is not represented as 116 but as 1106 hence string formatting. */
        return Integer.parseInt(zonedDateTime.getHour() + String.format(Locale.US, "%02d", zonedDateTime.getMinute()));
    }

    /** This will get you system time in 24-hour format. */
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
     * For privacy reason, abbreviate username on public forum.
     * First Name will become First N
     */
    public static String abbreviateName(String name) {
        try {
            String normalize = StringUtils.normalizeSpace(name);
            if (normalize.contains(" ")) {
                String[] splits = normalize.split(" ");
                if (splits.length >= 1) {
                    return splits[0] + " " + splits[1].charAt(0);
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
     *
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

    /** Converts 10000 to ₹10,000 value where defaults to India. */
    public static String displayWithCurrencyCodeWithFormatting(long orderPrice) {
        return currencyLocal("IN") + NumberFormat.getInstance(new Locale("en", "IN")).format(orderPrice);
    }

    /** Converts 10000 to ₹10,000 based on the country code supplied. */
    public static String displayWithCurrencyCodeWithFormatting(String orderPrice, String countryCode) {
        return currencyLocal(countryCode) + NumberFormat.getInstance(new Locale("en", countryCode)).format(new BigDecimal(orderPrice));
    }

    public static String displayWithCurrencyCode(String orderPrice, String countryCode) {
        return currencyLocal(countryCode) + orderPrice;
    }

    public static String displayWithCurrencyCode(int orderPrice, String countryCode) {
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

    @SuppressWarnings("unused")
    public static Set<String> retrieveIPV4(String fromDevice, String fromRequest) {
        LOG.info("Send ips are {} {}", fromDevice, fromRequest);
        Set<String> ips = new HashSet<>();

        try {
            InetAddress address = InetAddress.getByName(fromDevice);
            if (!(address instanceof Inet6Address)) {
                ips.add(fromDevice);
            }

            address = InetAddress.getByName(fromRequest);
            if (!(address instanceof Inet6Address)) {
                ips.add(fromRequest);
            }
            return ips;
        } catch (UnknownHostException e) {
            LOG.error("Failed on unknown host fromDevice={} fromRequest={} reason={}", fromDevice, fromRequest, e.getLocalizedMessage());
        }

        return ips;
    }

    public static boolean validateMail(String mail) {
        Matcher matcher = mailPattern.matcher(mail);
        return matcher.matches();
    }

    public static <T> Collection<List<T>> partitionBasedOnSize(List<T> inputList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return inputList.stream()
            .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size))
            .values();
    }

    public static String appendBusinessNameToNotificationMessage(String body, String businessName) {
        return body + "\n" + "Sender: " + businessName;
    }

    public static String buildTopic(String topicName, String deviceType) {
        return "/topics/" + topicName + UNDER_SCORE + deviceType;
    }

    public static double distanceInMeters(GeoJsonPoint from, GeoJsonPoint to) {
        return distanceInMeters(from.getY(), to.getY(), from.getX(), to.getX(), 0, 0);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @return double Distance in Meters
     */
    public static double distanceInMeters(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    /**
     * Y Combinator Hacker News
     *
     * (p - 1) / (t + 2)^1.5
     *
     * p = votes (points) from users
     * t = time since submission in hours
     */
    public static BigDecimal computeHotnessBasedOnYCombinator(int expressedInterestCount, long postedHowLongBackInHours) {
        BigDecimal computed = new BigDecimal(expressedInterestCount - 1).divide(BigDecimal.valueOf(Math.pow(postedHowLongBackInHours + 2, 1.5)), MathContext.DECIMAL64).setScale(1, RoundingMode.HALF_UP);
        if (computed.compareTo(new BigDecimal("5.0")) > 0) {
            return new BigDecimal("5.0").setScale(1, RoundingMode.HALF_UP);
        } else {
            return computed;
        }
    }
}
