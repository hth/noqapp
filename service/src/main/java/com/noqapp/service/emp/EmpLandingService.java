package com.noqapp.service.emp;

import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BillingPlanEnum;
import com.noqapp.domain.types.BillingStatusEnum;
import com.noqapp.domain.types.BusinessUserRegistrationStatusEnum;
import com.noqapp.domain.types.MessageOriginEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.TokenQueueService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Approve new business after validating all the details.
     */
    public void approveBusiness(String businessUserId, String qid) {
        LOG.info("Approve Business Clicked businessUserId={} qid={}", businessUserId, qid);
        BusinessUserEntity businessUser = businessUserService.findById(businessUserId);
        businessUser
                .setValidateByQid(qid)
                .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
        businessUserService.save(businessUser);

        BizNameEntity bizName = businessUser.getBizName();
        startWithPromotionalPlan(bizName);
        notifyInviteeWhenBusinessIsApproved(bizName.getInviteeCode(), bizName.getBusinessName());

        /* Change profile user level on approval of business. */
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(businessUser.getQueueUserId());
        userProfile.setLevel(UserLevelEnum.M_ADMIN);
        userProfile.setBusinessType(bizName.getBusinessType());
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getQueueUserId(),
                userProfile.getLevel()
        );
        accountService.save(userAccount);

        List<BizStoreEntity> bizStores = bizService.getAllBizStores(bizName.getId());
        for (BizStoreEntity bizStore : bizStores) {
            //TODO remove me as this as to be done by cron job. Temp way of creating
            //For all registered false run job
            if (StringUtils.isNotBlank(bizStore.getCountryShortName())) {
                tokenQueueService.createUpdate(bizStore);
            }

            /* Create relation for easy access. */
            BusinessUserStoreEntity businessUserStore = new BusinessUserStoreEntity(
                    businessUser.getQueueUserId(),
                    bizStore.getId(),
                    bizName.getId(),
                    bizStore.getCodeQR(),
                    userProfile.getLevel());
            businessUserStoreService.save(businessUserStore);
            //End cron job code

            LOG.info("added QR for qid={} bizName={} queueName={} topic={} bizStore={} ",
                    qid,
                    bizName.getBusinessName(),
                    bizStore.getDisplayName(),
                    bizStore.getTopic(),
                    bizStore.getId());
        }

        if (1 < bizStores.size()) {
            LOG.warn("Found stores more than 1, qid={} bizName={}", qid, bizName.getBusinessName());
        }
    }

    private void startWithPromotionalPlan(BizNameEntity bizName) {
        bizName
            .setBillingPlan(BillingPlanEnum.P)
            .setBillingStatus(BillingStatusEnum.C);
        bizService.saveName(bizName);
    }

    private void notifyInviteeWhenBusinessIsApproved(String inviteeCode, String businessName) {
        if (StringUtils.isNotBlank(inviteeCode)) {
            UserProfileEntity userProfile = accountService.findProfileByInviteCode(inviteeCode);

            //TODO remove UserLevel Client as Level is updated during registration
            if (UserLevelEnum.CLIENT == userProfile.getLevel() || UserLevelEnum.Q_SUPERVISOR == userProfile.getLevel()) {
                String title = businessName + " joined NoQueue.";
                String body = "Your invitee code was used during registration by " + businessName + ". "
                    + "We are proud that you have helped " + businessName + " to join new movement of no more queues. "
                    + "You will soon receive an email with more details. "
                    + "This detail would also be available in your web account under Rewards.";
                tokenQueueService.sendMessageToSpecificUser(title, body, userProfile.getQueueUserId(), MessageOriginEnum.D);
            } else {
                LOG.warn("This facility is avail to just users with userLevel={} or userLevel={} and not userLevel={}",
                        UserLevelEnum.CLIENT, UserLevelEnum.Q_SUPERVISOR, userProfile.getLevel());
            }
        }
    }

    /**
     * Decline approval of business when validation fails.
     */
    public void declineBusiness(String businessUserId, String qid) {
        LOG.info("Decline Business Clicked businessUserId={} qid={}", businessUserId, qid);
        BusinessUserEntity businessUser = businessUserService.findById(businessUserId);
        businessUser
                .setValidateByQid(qid)
                .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.N);
        businessUserService.save(businessUser);
    }
}
