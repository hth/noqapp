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
     * @param rid
     * @return
     */
    int getRemoteJoinCount(String rid);

    /**
     * Deducts remote join from available remote scans.
     *
     * @param rid
     * @return
     */
    boolean deductRemoteJoinCount(String rid);
}
