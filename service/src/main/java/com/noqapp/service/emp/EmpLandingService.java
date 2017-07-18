package com.noqapp.service.emp;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.TokenQueueService;

import java.util.List;

/**
 * User: hitender
 * Date: 12/14/16 12:24 PM
 */
@Service
public class EmpLandingService {
    private static final Logger LOG = LoggerFactory.getLogger(EmpLandingService.class);

    private BusinessUserService businessUserService;
    private AccountService accountService;
    private BizService bizService;
    private TokenQueueService tokenQueueService;
    private BusinessUserStoreService businessUserStoreService;

    @Autowired
    public EmpLandingService(
            BusinessUserService businessUserService,
            AccountService accountService,
            BizService bizService,
            TokenQueueService tokenQueueService,
            BusinessUserStoreService businessUserStoreService) {
        this.businessUserService = businessUserService;
        this.accountService = accountService;
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.businessUserStoreService = businessUserStoreService;
    }

    public void approveBusiness(String businessUserId, String rid) {
        BusinessUserEntity businessUser = businessUserService.findById(businessUserId);
        businessUser
                .setValidateByRid(rid)
                .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
        businessUserService.save(businessUser);

        UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(businessUser.getReceiptUserId());
        userProfile.setLevel(UserLevelEnum.M_ADMIN);
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                userProfile.getLevel()
        );
        accountService.save(userAccount);

        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        for (BizStoreEntity bizStore : bizStores) {
            //TODO remove me as this as to be done by cron job. Temp way of creating
            //For all registered false run job
            if (StringUtils.isNotBlank(bizStore.getCountryShortName())) {
                tokenQueueService.create(bizStore.getCodeQR(), bizStore.getTopic(), bizStore.getDisplayName());
            }

            /* Create relation for easy access. */
            BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
                    businessUser.getReceiptUserId(),
                    bizStore.getId(),
                    bizStore.getBizName().getId(),
                    bizStore.getCodeQR());
            businessUserStoreService.save(businessUserStore);
            //End cron job code

            LOG.info("added QR for rid={} bizName={} queueName={} topic={} bizStore={} ",
                    rid,
                    businessUser.getBizName().getBusinessName(),
                    bizStore.getDisplayName(),
                    bizStore.getTopic(),
                    bizStore.getId());
        }

        if (1 < bizStores.size()) {
            LOG.warn("Found stores more than 1, rid={} bizName={}", rid, businessUser.getBizName().getBusinessName());
        }
    }

    //TODO
    public void rejectBusiness() {

    }
}
