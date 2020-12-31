package com.noqapp.repository;

import com.noqapp.domain.NotificationMessageEntity;

/**
 * hitender
 * 6/5/20 2:02 PM
 */
public interface NotificationMessageManager extends RepositoryManager<NotificationMessageEntity> {

    boolean increaseViewClientCount(String id);

    boolean increaseViewUnregisteredCount(String id);

    boolean increaseViewBusinessCount(String id);
}
