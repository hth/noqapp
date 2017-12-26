package com.noqapp.common.utils;

import com.google.maps.model.LatLng;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 11/18/16 6:09 PM
 */
public final class CommonUtil {
    private static final Logger LOG = LoggerFactory.getLogger(CommonUtil.class);

    private static Header dummyHeader = new BasicHeader("A", "A");
    public static final String CODE_QR_PREFIX = "SN_";

    public static final String AUTH_KEY_HIDDEN = "*********";
    public static final String UNAUTHORIZED = "Unauthorized";

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
        }

        return null;
    }

    public static String getCountryNameFromIsoCode(String isoCode) {
        Locale l = new Locale("", isoCode);
        return l.getDisplayCountry();
    }

    public static Header getMeSomeHeader() {
        return dummyHeader;
    }

    /**
     * SN is prefix when not on Prod. This is to distinguish from Prod QR code. SN means Sandbox.
     *
     * @param environment
     * @return
     */
    public static String generateCodeQR(String environment) {
        switch (environment) {
            case "dev":
            case "sandbox":
                return CODE_QR_PREFIX + ObjectId.get().toString();
            case "prod":
                return ObjectId.get().toString();
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
}
