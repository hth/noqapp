package com.noqapp.repository;

import com.noqapp.domain.InviteEntity;

/**
 * User: hitender
 * Date: 3/29/17 10:39 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {

    /**
     * Lists total number of remote scan available.
     *
     * @param rid
     * @return
     */
    int getRemoteScanCount(String rid);

    /**
     * Deducts remote scan from available remote scans.
     *
     * @param rid
     * @return
     */
    boolean deductRemoteScanCount(String rid);
}
