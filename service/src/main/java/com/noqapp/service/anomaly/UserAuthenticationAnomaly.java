package com.noqapp.service.anomaly;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 2019-04-28 06:02
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Service
public class UserAuthenticationAnomaly {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationAnomaly.class);

    private UserAuthenticationManager userAuthenticationManager;
    private UserAccountManager userAccountManager;

    public UserAuthenticationAnomaly(UserAuthenticationManager userAuthenticationManager, UserAccountManager userAccountManager) {
        this.userAuthenticationManager = userAuthenticationManager;
        this.userAccountManager = userAccountManager;
    }

    public void listOrphanData() {
        AtomicInteger countOrphan = new AtomicInteger();
        try (Stream<UserAuthenticationEntity> stream = userAuthenticationManager.listAll(DateUtil.minusDays(15))) {
            stream.iterator().forEachRemaining(userAuthenticationEntity -> {
                boolean exists = userAccountManager.existWithAuth(userAuthenticationEntity.getId());
                if (!exists) {
                    LOG.error("Orphan {} created {} not being used", userAuthenticationEntity.getId(), userAuthenticationEntity.getCreated());
                    countOrphan.getAndIncrement();
                }
            });
        }
        LOG.warn("Orphan UserAuthenticationEntity record count={}", countOrphan);
    }
}
