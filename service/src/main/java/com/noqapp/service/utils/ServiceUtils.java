package com.noqapp.service.utils;

import static com.noqapp.common.utils.Constants.MINUTES_15;
import static com.noqapp.common.utils.Constants.MINUTES_30;
import static com.noqapp.common.utils.Constants.MINUTES_45;
import static com.noqapp.common.utils.Constants.MINUTES_59;
import static com.noqapp.common.utils.Constants.MINUTES_60;
import static com.noqapp.common.utils.Constants.PREVENT_JOINING_BEFORE_CLOSING;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.GetTimeAgoUtils;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreHourEntity;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.types.QueueJoinDeniedEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        if (avgServiceTime > 0) {
            if (queueStatus == QueueStatusEnum.S) {
                long timeToStoreStartInMilli = computeTimeToStoreStart(startHour, timeZone);
                if (timeToStoreStartInMilli > 0) {
                    return GetTimeAgoUtils.getTimeAgo((0 == positionInQueue ? 1 : positionInQueue) * avgServiceTime + timeToStoreStartInMilli);
                } else {
                    return GetTimeAgoUtils.getTimeAgo((0 == positionInQueue ? 1 : positionInQueue) * avgServiceTime);
                }
            } else {
                return GetTimeAgoUtils.getTimeAgo((0 == positionInQueue ? 1 : positionInQueue) * avgServiceTime);
            }
        }
        return null;
    }

    public static long computeTimeToStoreStart(int storeStart, String timeZone) {
        ZoneId zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        int requesterTime = DateFormatter.getTimeIn24HourFormat(LocalTime.now(zoneId));
        return Duration.between(DateFormatter.getLocalTime(requesterTime), DateFormatter.getLocalTime(storeStart)).getSeconds() * 1000;
    }

    public static String timeSlot(ZonedDateTime zonedDateTimeUTC, ZoneId zoneId, StoreHourEntity storeHour) {
        ZonedDateTime zonedDateTime = zonedDateTimeUTC.withZoneSameInstant(zoneId);

        LocalTime localTime = zonedDateTime.toLocalTime();
        int minutes = localTime.getMinute();

        /*
         * Note: Changing MINUTES_59 to lower number will reduce number of token issued during the store open hours.
         * These numbers will change distribution of token over the hours.
         */
        if (Duration.between(storeHour.startHour(), localTime).toMinutes() < MINUTES_59) {
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
                + String.format(Locale.US, "%02d", departureHour.getHour()) + ":" + String.format(Locale.US, "%02d", departureHour.getMinute()) + " (closing)";
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

    public static JsonToken blankJsonToken(String codeQR, QueueJoinDeniedEnum queueJoinDenied, BizStoreEntity bizStore) {
        return new JsonToken(codeQR, bizStore.getBusinessType())
            .setToken(0)
            .setDisplayToken(String.valueOf(0))
            .setServingNumber(0)
            .setDisplayServingNumber(String.valueOf(0))
            .setDisplayName(bizStore.getDisplayName())
            .setQueueJoinDenied(queueJoinDenied)
            .setExpectedServiceBegin(DateUtil.getZonedDateTimeAtUTC());
    }

    public static long computeAverageServiceTime(StoreHourEntity storeHour, int availableTokenCount) {
        if (0 == availableTokenCount) {
            return 0;
        }

        long seconds = availableStoreOpenDurationInSeconds(storeHour);
        return new BigDecimal(seconds)
            .divide(new BigDecimal(availableTokenCount), MathContext.DECIMAL64)
            .multiply(new BigDecimal(GetTimeAgoUtils.SECOND_MILLIS)).longValue();
    }

    public static long availableStoreOpenDurationInSeconds(StoreHourEntity storeHour) {
        return (storeHour.storeOpenDurationInMinutes() - PREVENT_JOINING_BEFORE_CLOSING) * DateUtil.MINUTE_IN_SECONDS;
    }
}
