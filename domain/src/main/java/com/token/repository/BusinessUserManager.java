package com.token.repository;

import com.token.domain.BusinessUserEntity;

/**
 * User: hitender
 * Date: 11/23/16 5:10 PM
 */
public interface BusinessUserManager extends RepositoryManager<BusinessUserEntity> {
    /**
     * Finds business user with any status like active or inactive.
     *
     * @param rid
     * @return
     */
    BusinessUserEntity findByRid(String rid);

    /**
     * Finds active business user.
     *
     * @param rid
     * @return
     */
    BusinessUserEntity findBusinessUser(String rid);
    boolean doesBusinessUserExists(String rid, String bizId);
}
