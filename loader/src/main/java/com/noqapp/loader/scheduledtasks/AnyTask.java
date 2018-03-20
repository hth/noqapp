package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.TokenQueueManager;
import com.noqapp.service.TokenQueueService;
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

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            TokenQueueManager tokenQueueManager,
            BizStoreManager bizStoreManager,
            QueueManager queueManager,
            Environment environment
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.tokenQueueManager = tokenQueueManager;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.environment = environment;
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
    }
}
