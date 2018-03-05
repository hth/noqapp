package com.noqapp.service;

import java.util.ArrayList;
import java.util.List;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 8/27/17 11:49 AM
 */
@Service
public class QueueService {
    private static final Logger LOG = LoggerFactory.getLogger(QueueService.class);

    private AccountService accountService;
    private QueueManager queueManager;
    private QueueManagerJDBC queueManagerJDBC;

    @Autowired
    public QueueService(
            AccountService accountService,
            QueueManager queueManager,
            QueueManagerJDBC queueManagerJDBC
    ) {
        this.accountService = accountService;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
    }

    @Mobile
    public List<QueueEntity> findAllQueuedByQid(String qid) {
        return queueManager.findAllQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByQid(String qid) {
        return queueManager.findAllNotQueuedByQid(qid);
    }

    @Mobile
    public List<QueueEntity> findAllNotQueuedByDid(String did) {
        return queueManager.findAllNotQueuedByDid(did);
    }

    @Mobile
    public List<QueueEntity> getByQid(String qid) {
        return queueManagerJDBC.getByQid(qid);
    }

    @Mobile
    public List<QueueEntity> getByDid(String did) {
        return queueManagerJDBC.getByDid(did);
    }

    public List<QueueEntity> findAllHistoricalQueue(String qid) {
        List<QueueEntity> queues = findAllNotQueuedByQid(qid);
        queues.addAll(getByQid(qid));
        return queues;
    }

    public long deleteByCodeQR(String codeQR) {
        return queueManager.deleteByCodeQR(codeQR);
    }

    public long getPreviouslyVisitedClientCount(String codeQR) {
        return queueManager.previouslyVisitedClientCount(codeQR);
    }

    public long getNewVisitClientCount(String codeQR) {
        return queueManager.newVisitClientCount(codeQR);
    }

    public void addPhoneNumberToExistingQueue(int token, String codeQR, String did, String phone) {
        queueManager.addPhoneNumberToExistingQueue(token, codeQR, did, phone);
    }

    public QueueEntity findQueuedOne(String codeQR, String did, String qid) {
        return queueManager.findQueuedOne(codeQR, did, qid);
    }

    /** Finds clients who are yet to be serviced. */
    public JsonQueuePersonList findAllClientQueuedOrAborted(String codeQR) {
        List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

        List<QueueEntity> queues = queueManager.findAllClientQueuedOrAborted(codeQR);
        for (QueueEntity queue : queues) {
            JsonQueuedPerson jsonQueuedPerson = new JsonQueuedPerson()
                    .setQueueUserId(queue.getQueueUserId())
                    .setCustomerName(queue.getCustomerName())
                    .setCustomerPhone(queue.getCustomerPhone())
                    .setQueueUserState(queue.getQueueUserState())
                    .setToken(queue.getTokenNumber())
                    .setServerDeviceId(queue.getServerDeviceId());

            if (!queue.getGuardianToQueueUserId().isEmpty()) {
                LOG.info("Is a guardian qid={}", queue.getQueueUserId());

                for (String qid : queue.getGuardianToQueueUserId()) {
                    UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
                    jsonQueuedPerson.addMinors(
                            new JsonQueuedPerson()
                                    .setQueueUserId(qid)
                                    .setCustomerName(userProfile.getName())
                                    .setToken(queue.getTokenNumber()));
                }
            }

            queuedPeople.add(jsonQueuedPerson);
        }

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }
}
