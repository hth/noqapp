package com.noqapp.repository;

import com.noqapp.domain.BusinessUserEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 5:10 PM
 */
public interface BusinessUserManager extends RepositoryManager<BusinessUserEntity> {
    /**
     * Finds business user with any status like active or inactive.
     *
     * @param qid
     * @return
     */
    BusinessUserEntity findByRid(String qid);

    /**
     * Finds active business user.
     *
     * @param qid
     * @return
     */
    BusinessUserEntity findBusinessUser(String qid);

    BusinessUserEntity findById(String id);

    boolean doesBusinessUserExists(String qid, String bizId);

    long awaitingApprovalCount();

    List<BusinessUserEntity> awaitingApprovals();
}
