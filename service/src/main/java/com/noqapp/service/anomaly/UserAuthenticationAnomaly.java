package com.noqapp.service.anomaly;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.UserAuthenticationEntity;
import com.noqapp.repository.UserAccountManager;
import com.noqapp.repository.UserAuthenticationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
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
        List<UserAuthenticationEntity> userAuthentications = userAuthenticationManager.listAll(DateUtil.getDateMinusDay(30));
        try (Stream<UserAuthenticationEntity> stream = userAuthentications.stream()) {
            stream.iterator().forEachRemaining(userAuthenticationEntity -> {
                boolean exists = userAccountManager.existWithAuth(userAuthenticationEntity.getId());
                if (!exists) {
                    LOG.warn("{} created {} not being used", userAuthenticationEntity.getId(), userAuthenticationEntity.getCreated());
                }
            });
        }
    }
}
