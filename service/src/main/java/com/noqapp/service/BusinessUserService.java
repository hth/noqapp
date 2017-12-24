package com.noqapp.service;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BusinessUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 11/23/16 5:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class BusinessUserService {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessUserService.class);

    private BusinessUserManager businessUserManager;

    @Autowired
    public BusinessUserService(BusinessUserManager businessUserManager) {
        this.businessUserManager = businessUserManager;
    }

    /**
     * Create, update business user.
     *
     * @param qid
     * @param userLevel
     * @param active
     */
    void saveUpdateBusinessUser(String qid, UserLevelEnum userLevel, boolean active) {
        BusinessUserEntity businessUser = businessUserManager.findByQid(qid);
        switch (userLevel) {
            case M_ADMIN:
                if (null == businessUser) {
                    businessUser = BusinessUserEntity.newInstance(qid, UserLevelEnum.M_ADMIN);
                }

                if (active) {
                    businessUser.active();
                } else {
                    businessUser.inActive();
                }

                if (!businessUser.isDeleted()) {
                    save(businessUser);
                }
                break;
            default:
                if (null != businessUser && !businessUser.isDeleted()) {
                    businessUser.inActive();
                    save(businessUser);
                }
                break;
        }
    }

    public BusinessUserEntity findBusinessUser(String qid) {
        return businessUserManager.findBusinessUser(qid);
    }

    public BusinessUserEntity findById(String id) {
        return businessUserManager.findById(id);
    }

    public boolean doesBusinessUserExists(String qid, String bizId) {
        return businessUserManager.doesBusinessUserExists(qid, bizId);
    }

    public void save(BusinessUserEntity businessUser) {
        businessUserManager.save(businessUser);
    }

    public void deleteHard(BusinessUserEntity businessUser) {
        businessUserManager.deleteHard(businessUser);
    }

    public long awaitingBusinessApprovalCount() {
        return businessUserManager.awaitingBusinessApprovalCount();
    }

    public List<BusinessUserEntity> awaitingBusinessApprovals() {
        return businessUserManager.awaitingBusinessApprovals();
    }

    public List<BusinessUserEntity> getAllNonAdminForBusiness(String bizNameId) {
        return businessUserManager.getAllNonAdminForBusiness(bizNameId);
    }
}
