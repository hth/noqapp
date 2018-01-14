package com.noqapp.loader.scheduledtasks;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.repository.BizNameManager;
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

    private Environment environment;
    private BizNameManager bizNameManager;

    @Autowired
    public AnyTask(
            @Value("${oneTimeStatusSwitch:ON}")
            String oneTimeStatusSwitch,

            Environment environment,
            BizNameManager bizNameManager
    ) {
        this.oneTimeStatusSwitch = oneTimeStatusSwitch;

        this.environment = environment;
        this.bizNameManager = bizNameManager;

        LOG.info("AnyTask environment={}", environment.getProperty("build.env"));
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

        List<BizNameEntity> bizNames = bizNameManager.findAll(0, 100);
        for (BizNameEntity bizName : bizNames) {
            if (StringUtils.isBlank(bizName.getCodeQR())) {
                String codeQR = CommonUtil.generateCodeQR(environment.getProperty("build.env"));
                bizName.setCodeQR(codeQR);
                bizNameManager.save(bizName);
            }
        }
    }
}
