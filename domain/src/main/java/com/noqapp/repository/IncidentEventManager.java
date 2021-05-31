package com.noqapp.repository;

import com.noqapp.domain.IncidentEventEntity;

import java.util.stream.Stream;

/**
 * hitender
 * 5/17/21 4:12 PM
 */
public interface IncidentEventManager extends RepositoryManager<IncidentEventEntity> {

    Stream<IncidentEventEntity> findAllWithStream(int fetchUntilDays);
}
