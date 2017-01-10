package com.token.repository;

import com.token.domain.BusinessUserStoreEntity;

/**
 * User: hitender
 * Date: 12/13/16 10:30 AM
 */
public interface BusinessUserStoreManager extends RepositoryManager<BusinessUserStoreEntity> {

    boolean hasAccess(String rid, String codeQR);
}
