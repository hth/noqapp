package com.noqapp.repository;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;

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
    BusinessUserEntity findByQid(String qid);

    /**
     * Finds active business user.
     *
     * @param qid
     * @return
     */
    BusinessUserEntity findBusinessUser(String qid);

    BusinessUserEntity findById(String id);

    boolean doesBusinessUserExists(String qid, String bizId);

    /**
     * Only Business Admins are approved. As they are the ones creating businesses. That's the only time they get the
     * first level as Business Admin. Otherwise everyone migrates from Q_SUPERVISOR to S_MANAGER to M_ADMIN.
     *
     * @return
     */
    long awaitingBusinessApprovalCount();

    /**
     * Only Business Admins are approved. As they are the ones creating businesses. That's the only time they get the
     * first level as Business Admin. Otherwise everyone migrates from Q_SUPERVISOR to S_MANAGER to M_ADMIN.
     *
     * @return
     */
    List<BusinessUserEntity> awaitingBusinessApprovals();

    List<BusinessUserEntity> getAllNonAdminForBusiness(String bizNameId);
}
