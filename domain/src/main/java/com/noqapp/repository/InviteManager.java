package com.noqapp.repository;

import com.noqapp.domain.InviteEntity;

/**
 * User: hitender
 * Date: 3/29/17 10:39 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {

    /**
     * Lists total number of remote join available.
     *
     * @param qid
     * @return
     */
    int getRemoteJoinCount(String qid);

    /**
     * Deducts remote join from available remote scans.
     *
     * @param qid
     * @return
     */
    boolean deductRemoteJoinCount(String qid);
}
