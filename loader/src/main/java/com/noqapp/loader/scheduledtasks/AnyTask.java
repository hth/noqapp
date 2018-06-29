package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.*;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mostly used one time to update, modify any data.
 *
 * hitender
 * 1/13/18 6:17 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private TokenQueueManager tokenQueueManager;
    private BizStoreManager bizStoreManager;
    private QueueManager queueManager;
    private Environment environment;
    private BusinessUserStoreService businessUserStoreService;
    private AccountService accountService;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            TokenQueueManager tokenQueueManager,
            BizStoreManager bizStoreManager,
            QueueManager queueManager,
            Environment environment,
            BusinessUserStoreService businessUserStoreService,
            AccountService accountService
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.tokenQueueManager = tokenQueueManager;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.environment = environment;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void someTask() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Run someTask in AnyTask");

        /* Write your method after here. Un-comment @Scheduled. */
        List<TokenQueueEntity> tokenQueues = tokenQueueManager.findAll();
        LOG.info("Found TokenQueue size={}", tokenQueues.size());
        for (TokenQueueEntity tokenQueue : tokenQueues) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(tokenQueue.getId());
            tokenQueue.setBusinessType(bizStore.getBusinessType());
            tokenQueueManager.save(tokenQueue);

            List<QueueEntity> queues = queueManager.findByCodeQR(tokenQueue.getId());
            LOG.info("Found Queue size={}", queues.size());
            for (QueueEntity queue : queues) {
                queue.setBusinessType(bizStore.getBusinessType());
                queueManager.save(queue);
            }
        }

        List<BusinessUserStoreEntity> a = businessUserStoreService.findAll();
        for (BusinessUserStoreEntity businessUserStoreEntity : a) {
            UserProfileEntity userProfileEntity = accountService.findProfileByQueueUserId(businessUserStoreEntity.getQueueUserId());
            businessUserStoreEntity.setUserLevel(userProfileEntity.getLevel());
            businessUserStoreService.save(businessUserStoreEntity);
        }

        /* Add Business Type to UserProfile. */
        List<UserProfileEntity> userProfiles = accountService.findAll();
        for (UserProfileEntity userProfile : userProfiles) {
            List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(userProfile.getQueueUserId());
            for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
                BusinessTypeEnum businessType = bizStoreManager.getAllBizStores(businessUserStore.getBizNameId()).get(0).getBizName().getBusinessType();
                if (userProfile.getBusinessType() == null) {
                    userProfile.setBusinessType(businessType);
                    accountService.save(userProfile);
                } else {
                    LOG.info("Found userProfile with businessType={} updating with {}", userProfile.getBusinessType(), businessType);
                }
            }
        }
    }
}
