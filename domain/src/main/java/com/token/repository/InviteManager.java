package com.token.repository;

import com.token.domain.InviteEntity;

/**
 * User: hitender
 * Date: 3/29/17 10:39 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {

    int getRemoteScanCount(String rid);
}
