package com.noqapp.repository;

import com.noqapp.domain.InviteEntity;

/**
 * User: hitender
 * Date: 3/29/17 10:39 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {

    /**
     * Lists total number of remote join available.
     */
    @Deprecated
    int getRemoteJoinCount(String qid);

    /**
     * Deducts remote join from available remote scans.
     */
    @Deprecated
    boolean deductRemoteJoinCount(String qid);

    /**
     * Increase max remote join by a specific count.
     */
    @Deprecated
    long increaseRemoteJoin(int maxRemoteJoin);
}
