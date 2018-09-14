package com.noqapp.repository;

import com.noqapp.domain.ScheduledTaskEntity;

/**
 * hitender
 * 9/10/18 8:12 PM
 */
public interface ScheduledTaskManager extends RepositoryManager<ScheduledTaskEntity> {

    ScheduledTaskEntity findOneById(String id);

    void inActive(String id);
}
