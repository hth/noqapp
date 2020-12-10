package com.noqapp.repository;

import com.noqapp.domain.OutGoingNotificationEntity;

/**
 * hitender
 * 12/9/20 5:32 PM
 */
public interface OutGoingNotificationManager extends RepositoryManager<OutGoingNotificationEntity> {

    OutGoingNotificationEntity findToSend(int weekYear);
}
