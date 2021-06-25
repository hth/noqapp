package com.noqapp.repository;

import com.noqapp.domain.PointEarnedEntity;

import java.util.stream.Stream;

/**
 * hitender
 * 6/24/21 7:05 AM
 */
public interface PointEarnedManager extends RepositoryManager<PointEarnedEntity> {

    Stream<PointEarnedEntity> findAllNotMarkedComputed();

    void markComputedById(String id);
}
