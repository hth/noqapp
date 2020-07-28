package com.noqapp.service.utils;

import static com.noqapp.common.utils.Constants.MINUTES_15;
import static com.noqapp.common.utils.Constants.MINUTES_30;
import static com.noqapp.common.utils.Constants.MINUTES_45;
import static com.noqapp.common.utils.Constants.MINUTES_60;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.GetTimeAgoUtils;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.types.QueueStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * hitender
 * 6/3/20 8:48 PM
 */
public class ServiceUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceUtils.class);

    private static final boolean switchedOnHourSlot = true;

    /**
     * Calculate the estimated wait time
     *
     * @param avgServiceTime  Average handling time for a token
     * @param positionInQueue assigned token position in queue
     * @param queueStatus     current status of queue
     * @return the estimated wait time
     */
    public static String calculateEstimatedWaitTime(
        long avgServiceTime,
        int positionInQueue,
        QueueStatusEnum queueStatus,
        int startHour,
        String timeZone
    ) {
        if (avgServiceTime > 0 && positionInQueue > 0) {
            if (queueStatus == QueueStatusEnum.S) {
                long timeToStoreStartInMilli = computeTimeToStoreStart(startHour, timeZone);
                if (timeToStoreStartInMilli > 0) {
                    return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime + timeToStoreStartInMilli);
                } else {
                    return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime);
                }
            } else {
                return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime);
            }
        }
        return null;
    }

    public static long computeTimeToStoreStart(int storeStart, String timeZone) {
        ZoneId zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        int requesterTime = DateFormatter.getTimeIn24HourFormat(LocalTime.now(zoneId));
        return Duration.between(DateFormatter.getLocalTime(requesterTime), DateFormatter.getLocalTime(storeStart)).getSeconds() * 1000;
    }

    public static String timeSlot(Date date, String timeZone, StoreHourEntity storeHour) {
        ZonedDateTime zonedDateTime = DateUtil.convertToLocalDateTime(date, timeZone);

        LocalTime localTime = zonedDateTime.toLocalTime();
        int minutes = localTime.getMinute();

        if (Duration.between(storeHour.startHour(), localTime).toMinutes() < MINUTES_30) {
            LocalTime arrivalHour = storeHour.startHour();
            LOG.debug("Close to start {}", Duration.between(localTime, storeHour.startHour()).toMinutes());
            LocalTime after = arrivalHour.minusMinutes(arrivalHour.getMinute()).plusHours(1);

            return String.format(Locale.US, "%02d", arrivalHour.getHour()) + ":" + String.format(Locale.US, "%02d", arrivalHour.getMinute()) + " - "
                + String.format(Locale.US, "%02d", after.getHour()) + ":" + String.format(Locale.US, "%02d", after.getMinute());
        } else if (Duration.between(localTime, storeHour.endHour()).toMinutes() < MINUTES_60) {
            LOG.debug("Close to end {}", Duration.between(localTime, storeHour.endHour()).toMinutes());
            LocalTime arrivalHour = storeHour.endHour().minusHours(1);
            LocalTime departureHour = storeHour.endHour();
            return String.format(Locale.US, "%02d", arrivalHour.getHour()) + ":" + String.format(Locale.US, "%02d", arrivalHour.getMinute()) + " - "
                + String.format(Locale.US, "%02d", departureHour.getHour()) + ":" + String.format(Locale.US, "%02d", departureHour.getMinute()) + " (store closes)";
        }

        if (switchedOnHourSlot) {
            LocalTime before = localTime.minusMinutes(minutes);
            LocalTime after = before.plusHours(1);
            return String.format(Locale.US, "%02d", before.getHour()) + ":" + String.format(Locale.US, "%02d", before.getMinute()) + " - "
                + String.format(Locale.US, "%02d", after.getHour()) + ":" + String.format(Locale.US, "%02d", after.getMinute());
        } else {
            /* This code hardly is being used. */
            if (minutes >= MINUTES_45) {
                LocalTime before = localTime.minusMinutes(minutes).plusMinutes(MINUTES_30);
                LocalTime after = before.plusHours(1);
                return String.format(Locale.US, "%02d", before.getHour()) + ":" + String.format(Locale.US, "%02d", before.getMinute()) + " - "
                    + String.format(Locale.US, "%02d", after.getHour()) + ":" + String.format(Locale.US, "%02d", after.getMinute());
            } else if (minutes <= MINUTES_15) {
                LocalTime before = localTime.minusMinutes(minutes).minusMinutes(MINUTES_30);
                LocalTime after = before.plusHours(1);
                return String.format(Locale.US, "%02d", before.getHour()) + ":" + String.format(Locale.US, "%02d", before.getMinute()) + " - "
                    + String.format(Locale.US, "%02d", after.getHour()) + ":" + String.format(Locale.US, "%02d", after.getMinute());
            } else {
                LocalTime after = localTime.plusHours(1);
                return  localTime.getHour() + ":" + "00" + " - " + after.getHour() + ":" + "00";
            }
        }
    }
}
