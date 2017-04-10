package com.noqapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.NotificationEntity;
import com.noqapp.domain.types.NotificationGroupEnum;
import com.noqapp.domain.types.NotificationMarkerEnum;
import com.noqapp.domain.types.NotificationTypeEnum;
import com.noqapp.domain.types.PaginationEnum;
import com.noqapp.repository.NotificationManager;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 11/25/16 9:02 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private NotificationManager notificationManager;

    @Autowired
    public NotificationService(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    /**
     * Hide notification from user.
     *
     * @param message
     * @param notificationType
     * @param id
     * @param rid
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            NotificationGroupEnum notificationGroup,
            String id,
            String rid
    ) {
        NotificationEntity notification = NotificationEntity.newInstance(notificationType);
        notification.setMessage(message);
        notification.setReceiptUserId(rid);
        notification.setNotificationMarkerEnum(notificationType.notificationMarker);
        notification.setNotificationGroup(notificationGroup);
        if (notificationType.notificationMarker != NotificationMarkerEnum.P) {
            /** Defaults to success as its not going to be sent through Push Notification. */
            notification.setNotificationStateToSuccess();
        }
        notification.setReferenceId(id);

        try {
            notificationManager.save(notification);
        } catch (Exception exce) {
            LOG.error("Failed adding notification={}, with message={}, for user={}",
                    exce.getLocalizedMessage(),
                    message,
                    rid);
        }
    }

    /**
     * Show notification to the user.
     *
     * @param message
     * @param notificationType  either MESSAGE or PUSH_NOTIFICATION
     * @param notificationGroup to group notification in types for picking right icons
     * @param rid
     */
    public void addNotification(
            String message,
            NotificationTypeEnum notificationType,
            NotificationGroupEnum notificationGroup,
            String rid
    ) {
        switch (notificationType) {
            case PUSH_NOTIFICATION:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        null,
                        rid);
                break;
            case MESSAGE:
                addNotification(
                        message,
                        notificationType,
                        notificationGroup,
                        null,
                        rid);
                break;
            default:
                throw new UnsupportedOperationException("Incorrect method call for Notification Type");
        }
    }

    /**
     * List all the notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> getAllNotifications(String rid) {
        return getNotifications(rid, PaginationEnum.ALL.getLimit());
    }

    private List<NotificationEntity> getNotifications(String rid, int limit) {
        return notificationManager.getNotifications(rid, 0, limit);
    }

    /**
     * List last five notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> getNotifications(String rid) {
        return getNotifications(rid, PaginationEnum.FIVE.getLimit());
    }

    /**
     * List last five notification in descending order.
     *
     * @param rid
     * @return
     */
    public List<NotificationEntity> notificationsPaginated(String rid, int start) {
        return notificationManager.getNotifications(rid, start, PaginationEnum.FIVE.getLimit());
    }

    public long notificationCount(String rid) {
        return notificationManager.notificationCount(rid);
    }

    public int deleteInactiveNotification(Date sinceDate) {
        return notificationManager.deleteHardInactiveNotification(sinceDate);
    }

    public int setNotificationInactive(Date sinceDate) {
        return notificationManager.setNotificationInactive(sinceDate);
    }
}
