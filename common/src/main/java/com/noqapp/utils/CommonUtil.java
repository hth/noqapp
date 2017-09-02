package com.noqapp.utils;

import com.google.maps.model.LatLng;

import org.bson.types.ObjectId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 11/18/16 6:09 PM
 */
public final class CommonUtil {
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
    public DayOfWeek getNextDayOfWeek(DayOfWeek dayOfWeek) {
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
}
