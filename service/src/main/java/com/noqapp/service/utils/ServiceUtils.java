package com.noqapp.service.utils;

import com.noqapp.common.utils.DateFormatter;
import com.noqapp.common.utils.GetTimeAgoUtils;
import com.noqapp.domain.types.QueueStatusEnum;

import org.joda.time.Seconds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * hitender
 * 6/3/20 8:48 PM
 */
public class ServiceUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceUtils.class);

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
}
