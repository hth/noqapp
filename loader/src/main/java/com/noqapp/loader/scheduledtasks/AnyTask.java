package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizCategoryEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.repository.BizCategoryManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.ProfessionalProfileManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserStoreService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
    private BizCategoryManager bizCategoryManager;
    private ProfessionalProfileManager professionalProfileManager;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:OFF}")
        String oneTimeStatusSwitch,

        TokenQueueManager tokenQueueManager,
        BizStoreManager bizStoreManager,
        QueueManager queueManager,
        Environment environment,
        BusinessUserStoreService businessUserStoreService,
        AccountService accountService,
        BizCategoryManager bizCategoryManager,
        ProfessionalProfileManager professionalProfileManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.tokenQueueManager = tokenQueueManager;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.environment = environment;
        this.businessUserStoreService = businessUserStoreService;
        this.accountService = accountService;
        this.bizCategoryManager = bizCategoryManager;
        this.professionalProfileManager = professionalProfileManager;

        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    //@Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
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
        int roleCount = 0;
        List<UserProfileEntity> userProfiles = accountService.findAll();
        for (UserProfileEntity userProfile : userProfiles) {
            List<BusinessUserStoreEntity> businessUserStores = businessUserStoreService.findAllStoreQueueAssociated(userProfile.getQueueUserId());
            for (BusinessUserStoreEntity businessUserStore : businessUserStores) {
                BusinessTypeEnum businessType = bizStoreManager.getAllBizStores(businessUserStore.getBizNameId()).get(0).getBizName().getBusinessType();
                if (userProfile.getBusinessType() == null) {
                    userProfile.setBusinessType(businessType);
                    if (businessType == BusinessTypeEnum.DO) {
                        if (!userProfile.getEmail().equalsIgnoreCase("vinay.wagh1982@gmail.com")
                                && !userProfile.getEmail().equalsIgnoreCase("vinay_wagh1982@yahoo.in")
                                && !userProfile.getEmail().equalsIgnoreCase("ssdhospital@gmail.com")) {
                            userProfile.setLevel(UserLevelEnum.S_MANAGER);
                            accountService.changeAccountRolesToMatchUserLevel(userProfile.getQueueUserId(), UserLevelEnum.S_MANAGER);

                            ProfessionalProfileEntity professionalProfile = professionalProfileManager.findOne(userProfile.getQueueUserId());
                            if (null == professionalProfile) {
                                professionalProfile = new ProfessionalProfileEntity(userProfile.getQueueUserId(), CommonUtil.generateHexFromObjectId());
                                professionalProfileManager.save(professionalProfile);
                            }

                            roleCount ++;
                        }
                    }
                    accountService.save(userProfile);

                } else {
                    LOG.info("Found userProfile with businessType={} updating with {}", userProfile.getBusinessType(), businessType);
                }
            }
        }
        LOG.info("Changed role count {}", roleCount);

        /* Update Business Category. */
        List<BizStoreEntity> bizStores = bizStoreManager.getAll(0, 1000);
        for (BizStoreEntity bizStore : bizStores) {
            if (StringUtils.isNotBlank(bizStore.getBizCategoryId()) && bizStore.getBizCategoryId().length() > 3) {
                BizCategoryEntity bizCategory = bizCategoryManager.findById(bizStore.getBizCategoryId());
                for (MedicalDepartmentEnum medicalDepartment : MedicalDepartmentEnum.values()) {
                    if (medicalDepartment.getDescription().equalsIgnoreCase(bizCategory.getCategoryName())) {
                        bizStore.setBizCategoryId(medicalDepartment.getName());
                        bizStoreManager.save(bizStore);
                    }
                }
            }
        }
    }
}
