package com.noqapp.repository;

import com.noqapp.domain.NotificationEntity;
import com.noqapp.domain.annotation.Mobile;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/25/16 9:40 AM
 */
public interface NotificationManager extends RepositoryManager<NotificationEntity> {
    @Mobile
    List<NotificationEntity> getNotifications(String rid, int start, int limit);

    long notificationCount(String rid);

    /**
     * Delete notification older than sinceDate.
     *
     * @param sinceDate
     * @return
     */
    int deleteHardInactiveNotification(Date sinceDate);

    /**
     * Set the notification inactive when older than sinceDate.
     *
     * @param sinceDate
     * @return
     */
    int setNotificationInactive(Date sinceDate);

    /**
     * Gets all the Notification that are marked as push notification.
     *
     * @param notificationRetryCount beyond this number ignore the notification to be pushed.
     * @return
     */
    List<NotificationEntity> getAllPushNotifications(int notificationRetryCount);

    /**
     * Mark all the notification id as read.
     *
     * @param notificationIds
     */
    @Mobile
    void markNotificationRead(List<String> notificationIds, String rid);
}

