package com.noqapp.repository;

import com.noqapp.domain.InviteEntity;

/**
 * User: hitender
 * Date: 3/29/17 10:39 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {

    /** Lists total number of points available. */
    int computePoints(String qid);

    /** Deducts points. */
    boolean deductPoints(String qid);

    /** Increase points. */
    long increasePoints(int maxRemoteJoin);
}
