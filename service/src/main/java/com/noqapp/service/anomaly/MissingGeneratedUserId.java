package com.noqapp.service.anomaly;

import com.noqapp.common.utils.Constants;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.repository.GenerateUserIdManager;
import com.noqapp.repository.UserAccountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * User: hitender
 * Date: 2019-04-28 11:40
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class MissingGeneratedUserId {
    private static final Logger LOG = LoggerFactory.getLogger(MissingGeneratedUserId.class);

    private int lookBackForQids;

    private UserAccountManager userAccountManager;
    private GenerateUserIdManager generateUserIdManager;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public MissingGeneratedUserId(
        Environment environment,
        UserAccountManager userAccountManager,
        GenerateUserIdManager generateUserIdManager,
        StringRedisTemplate stringRedisTemplate
    ) {
        this.userAccountManager = userAccountManager;
        this.generateUserIdManager = generateUserIdManager;
        this.stringRedisTemplate = stringRedisTemplate;

        this.lookBackForQids = Objects.requireNonNull(environment.getProperty("build.env")).equalsIgnoreCase("prod") ? 2_000 : 10;
    }

    /**
     * Note: Since list is associated to JVM instance. It needs to run for each JVM. Make sure this is temp called in each JVM instance.
     * This should primarily reside in loader JVM and should be invoked in just that..
     */
    public void populateWithMissingQID() {
        List<String> qidsThatWereMissed = new LinkedList<>();
        long lastNumber = generateUserIdManager.getLastGenerateUserId();
        for (long i = lastNumber - lookBackForQids; i <= lastNumber; i++) {
            UserAccountEntity userAccount = userAccountManager.findByQueueUserId(String.valueOf(i));
            if (null == userAccount) {
                qidsThatWereMissed.add(String.valueOf(i));
                LOG.error("Found missed QID={}", i);
            }
        }

        if (!qidsThatWereMissed.isEmpty()) {
            stringRedisTemplate.opsForValue().set(Constants.MISSING_QUEUE_IDS, qidsThatWereMissed.toString());
        }
    }
}
