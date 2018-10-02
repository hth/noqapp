package com.noqapp.loader.scheduledtasks;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManagerJDBC;

import org.apache.commons.lang3.StringUtils;

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
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Component
public class AnyTask {
    private static final Logger LOG = LoggerFactory.getLogger(AnyTask.class);

    private String oneTimeStatusSwitch;

    private Environment environment;
    private QueueManagerJDBC queueManagerJDBC;
    private BizStoreManager bizStoreManager;

    @Autowired
    public AnyTask(
        @Value("${oneTimeStatusSwitch:ON}")
        String oneTimeStatusSwitch,

        Environment environment,
        QueueManagerJDBC queueManagerJDBC,
        BizStoreManager bizStoreManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.queueManagerJDBC = queueManagerJDBC;
        this.bizStoreManager = bizStoreManager;
        this.environment = environment;
        LOG.info("AnyTask environment={}", this.environment.getProperty("build.env"));
    }

    /**
     * Runs any requested task underneath.
     * Make sure there are proper locks, limits and or conditions to prevent re-run.
     */
    @SuppressWarnings("all")
    @Scheduled(fixedDelayString = "${loader.MailProcess.sendMail}")
    public void someTask() {
        if ("OFF".equalsIgnoreCase(oneTimeStatusSwitch)) {
            return;
        }

        oneTimeStatusSwitch = "OFF";
        LOG.info("Run someTask in AnyTask");

        /* Write your method after here. Un-comment @Scheduled. */
        List<QueueEntity> queues = queueManagerJDBC.findAllWhereBizNameIdIsNull();
        int found = queues.size();
        int count = 0;
        for (QueueEntity queue : queues) {
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(queue.getCodeQR());

            if (bizStore != null) {
                if (bizStore.getBizName() != null) {
                    if (StringUtils.isNotBlank(bizStore.getBizName().getId())) {
                        queue.setBizNameId(bizStore.getBizName().getId());
                        boolean updatedStatus = queueManagerJDBC.update(queue);
                        if (updatedStatus) {
                            count++;
                            LOG.info("Update BizNameId for id={}", queue.getId());
                        } else {
                            LOG.info("Failed update BizNameId for id={}", queue.getId());
                        }
                    } else {
                        LOG.warn("No BizNameId found for id={}", queue.getId());
                    }
                } else {
                    LOG.error("BizName is null codeQR={}", queue.getCodeQR());
                }
            } else {
                LOG.error("BizStore is null codeQR={}", queue.getCodeQR());
            }
        }

        LOG.info("Updated records={} total={}", count, found);
    }
}
