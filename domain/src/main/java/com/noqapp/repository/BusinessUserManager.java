package com.noqapp.repository;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.types.UserLevelEnum;

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
     * Gets self.
     * //TODO this will return a list of self. So better to add BusinessNameId in user session/context.
     *
     * @deprecated  As of release 1.4.0, replaced by {@link #findBusinessUser(String, String)}
     * Implement multiple account support.
     */
    @Deprecated
    BusinessUserEntity loadBusinessUser();

    /**
     * Load business user for a specific business. As same user can be registered in different businesses.
     */
    BusinessUserEntity findBusinessUser(String qid, String bizId);

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
     * There is a limit specified for approval.
     *
     * @return
     */
    List<BusinessUserEntity> awaitingBusinessApprovals();

    List<BusinessUserEntity> getAllNonAdminForBusiness(String bizNameId);

    List<BusinessUserEntity> getAllForBusiness(String bizNameId);

    List<BusinessUserEntity> getAllForBusiness(String bizNameId, UserLevelEnum userLevel);

    long updateUserLevel(String qid, UserLevelEnum userLevel);

    boolean hasAccess(String qid, String bizNameId);
}
