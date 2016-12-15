package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.BusinessUserEntity;
import com.token.domain.types.UserLevelEnum;
import com.token.repository.BizStoreManager;
import com.token.repository.BusinessUserManager;

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
    private BizStoreManager bizStoreManager;

    @Autowired
    public BusinessUserService(
            BusinessUserManager businessUserManager,
            BizStoreManager bizStoreManager
    ) {
        this.businessUserManager = businessUserManager;
        this.bizStoreManager = bizStoreManager;
    }

    /**
     * Create, update business user.
     *
     * @param rid
     * @param userLevel
     * @param active
     */
    void saveUpdateBusinessUser(String rid, UserLevelEnum userLevel, boolean active) {
        BusinessUserEntity businessUser = businessUserManager.findByRid(rid);
        switch (userLevel) {
            //TODO add Accountant and Enterprise
            case BIZ_ADMIN:
                if (null == businessUser) {
                    businessUser = BusinessUserEntity.newInstance(rid, UserLevelEnum.BIZ_ADMIN);
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

    public BusinessUserEntity findBusinessUser(String rid) {
        return businessUserManager.findBusinessUser(rid);
    }

    public BusinessUserEntity fingById(String id) {
        return businessUserManager.findById(id);
    }

    public boolean doesBusinessUserExists(String rid, String bizId) {
        return businessUserManager.doesBusinessUserExists(rid, bizId);
    }

    public void save(BusinessUserEntity businessUser) {
        businessUserManager.save(businessUser);
    }

    public long awaitingApprovalCount() {
        return businessUserManager.awaitingApprovalCount();
    }

    public List<BusinessUserEntity> awaitingApprovals() {
        return businessUserManager.awaitingApprovals();
    }
}
