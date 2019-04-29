package com.noqapp.service.anomaly;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.repository.GenerateUserIdManager;
import com.noqapp.repository.UserAccountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    //This is little flawed as it stays within each instance of JVM
    public static final List<String> qidsThatWereMissed = new CopyOnWriteArrayList<>();

    private UserAccountManager userAccountManager;
    private GenerateUserIdManager generateUserIdManager;

    @Autowired
    public MissingGeneratedUserId(UserAccountManager userAccountManager, GenerateUserIdManager generateUserIdManager) {
        this.userAccountManager = userAccountManager;
        this.generateUserIdManager = generateUserIdManager;

        populateWithMissingQID();
    }

    public void populateWithMissingQID() {
        long lastNumber = generateUserIdManager.getLastGenerateUserId();
        for (long i = lastNumber - 100; i <= lastNumber; i++) {
            UserAccountEntity userAccount = userAccountManager.findByQueueUserId(String.valueOf(i));
            if (null == userAccount) {
                qidsThatWereMissed.add(String.valueOf(i));
                LOG.warn("Found missed QID={}", i);
            }
        }
    }
}
