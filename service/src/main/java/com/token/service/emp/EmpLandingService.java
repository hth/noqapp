package com.token.service.emp;

import org.apache.commons.lang3.StringUtils;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.BizStoreEntity;
import com.token.domain.BusinessUserEntity;
import com.token.domain.UserAccountEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.types.BusinessUserRegistrationStatusEnum;
import com.token.domain.types.UserLevelEnum;
import com.token.service.AccountService;
import com.token.service.BizService;
import com.token.service.BusinessUserService;
import com.token.service.FirebaseService;
import com.token.service.TokenQueueService;

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
    private FirebaseService firebaseService;

    @Autowired
    public EmpLandingService(
            BusinessUserService businessUserService,
            AccountService accountService,
            BizService bizService,
            TokenQueueService tokenQueueService,
            FirebaseService firebaseService) {
        this.businessUserService = businessUserService;
        this.accountService = accountService;
        this.bizService = bizService;
        this.tokenQueueService = tokenQueueService;
        this.firebaseService = firebaseService;
    }

    public void approveBusiness(String businessUserId, String rid) {
        BusinessUserEntity businessUser = businessUserService.fingById(businessUserId);
        businessUser
                .setValidateByRid(rid)
                .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
        businessUserService.save(businessUser);

        UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(businessUser.getReceiptUserId());
        userProfile.setLevel(UserLevelEnum.BIZ_ADMIN);
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                userProfile.getLevel()
        );
        accountService.save(userAccount);

        List<BizStoreEntity> bizStores = bizService.getAllBizStores(businessUser.getBizName().getId());
        for (BizStoreEntity bizStore : bizStores) {
            if (StringUtils.isBlank(bizStore.getCodeQR())) {
                bizStore.setCodeQR(ObjectId.get().toString());
                bizService.saveStore(bizStore);

                //TODO remove me as this as to be done by cron job. Temp way of creating token
                tokenQueueService.create(bizStore.getCodeQR(), bizStore.getTopic());
                boolean success = firebaseService.registerTopic(bizStore.getTopic(), "Say Hi to " + bizStore.getDisplayName());
                bizStore.setRegistered(success);
                bizService.saveStore(bizStore);
                //End cron job code

                LOG.info("added QR for rid={} bizName={} bizStore={}",
                        rid, businessUser.getBizName().getBusinessName(), bizStore.getId());
            }

            if (1 < bizStores.size()) {
                LOG.warn("Found stores more than 1, rid={} bizName={}", rid, businessUser.getBizName().getBusinessName());
            }
        }
    }

    public void rejectBusiness() {

    }
}
