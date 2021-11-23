package com.noqapp.repository;

import com.noqapp.domain.PointEarnedEntity;

import java.util.stream.Stream;

/**
 * hitender
 * 6/24/21 7:05 AM
 */
public interface PointEarnedManager extends RepositoryManager<PointEarnedEntity> {

    /** Find records that are supposed to be changed. */
    Stream<String> findUniqueAllNotMarkedComputed();

    Stream<PointEarnedEntity> findAllNotMarkedComputed();

    void markComputedById(String id);

    long countReviewPoints(String qid);

    long countInvitePoints(String qid);

    long countInviteePoints(String qid);
}
