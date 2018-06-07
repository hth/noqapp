package com.noqapp.service;

import com.noqapp.domain.BusinessUserStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedMinorPerson;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.JsonToken;
import com.noqapp.domain.stats.HealthCareStat;
import com.noqapp.domain.stats.HealthCareStatList;
import com.noqapp.domain.stats.NewRepeatCustomers;
import com.noqapp.domain.stats.YearlyData;
import com.noqapp.domain.types.QueueStatusEnum;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.QueueManagerJDBC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private TokenQueueService tokenQueueService;
    private BusinessUserStoreManager businessUserStoreManager;

    @Autowired
    public QueueService(
            AccountService accountService,
            QueueManager queueManager,
            QueueManagerJDBC queueManagerJDBC,
            TokenQueueService tokenQueueService,
            BusinessUserStoreManager businessUserStoreManager
    ) {
        this.accountService = accountService;
        this.queueManager = queueManager;
        this.queueManagerJDBC = queueManagerJDBC;
        this.tokenQueueService = tokenQueueService;
        this.businessUserStoreManager = businessUserStoreManager;
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

            if (null != queue.getGuardianToQueueUserId() && !queue.getGuardianToQueueUserId().isEmpty()) {
                LOG.info("Is a guardian qid={}", queue.getQueueUserId());

                for (String qid : queue.getGuardianToQueueUserId()) {
                    UserProfileEntity userProfile = accountService.findProfileByQueueUserId(qid);
                    jsonQueuedPerson.addMinors(
                            new JsonQueuedMinorPerson()
                                    .setToken(queue.getTokenNumber())
                                    .setQueueUserId(qid)
                                    .setCustomerName(userProfile.getName())
                                    .setGuardianPhone(queue.getCustomerPhone())
                                    .setGuardianQueueUserId(queue.getQueueUserId())
                                    .setQueueUserState(queue.getQueueUserState())
                                    .setAge(userProfile.getAge())
                                    .setGender(userProfile.getGender()));
                }
            }

            queuedPeople.add(jsonQueuedPerson);
        }

        return new JsonQueuePersonList().setQueuedPeople(queuedPeople);
    }

    /**
     * When merchant has served a specific token.
     *
     * @param codeQR
     * @param servedNumber
     * @param queueUserState
     * @param goTo           - counter name
     * @param sid            - server device id
     * @param tokenService   - Invoked via Web or Device
     * @return
     */
    public JsonToken updateAndGetNextInQueue(
            String codeQR,
            int servedNumber,
            QueueUserStateEnum queueUserState,
            String goTo,
            String sid,
            TokenServiceEnum tokenService
    ) {
        LOG.info("Update and getting next in queue codeQR={} servedNumber={} queueUserState={} goTo={} sid={}",
                codeQR, servedNumber, queueUserState, goTo, sid);

        QueueEntity queue = queueManager.updateAndGetNextInQueue(codeQR, servedNumber, queueUserState, goTo, sid, tokenService);
        if (null != queue) {
            LOG.info("Found queue codeQR={} servedNumber={} queueUserState={} nextToken={}",
                    codeQR, servedNumber, queueUserState, queue.getTokenNumber());

            return tokenQueueService.updateServing(codeQR, QueueStatusEnum.N, queue.getTokenNumber(), goTo);
        }

        LOG.info("Reached condition of not having any more to serve");
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.D);
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
                /* Better to show last number than served number. This is to maintain consistent state. */
                .setToken(tokenQueue.getCurrentlyServing())
                .setServingNumber(tokenQueue.getCurrentlyServing())
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.D);
    }

    /**
     * Merchant when pausing to serve queue.
     */
    @Mobile
    public JsonToken pauseServingQueue(
            String codeQR,
            int servedNumber,
            QueueUserStateEnum queueUserState,
            String sid,
            TokenServiceEnum tokenService
    ) {
        LOG.info("Server person is now pausing for queue codeQR={} servedNumber={} queueUserState={} sid={}",
                codeQR, servedNumber, queueUserState, sid);

        boolean status = queueManager.updateServedInQueue(codeQR, servedNumber, queueUserState, sid, tokenService);
        LOG.info("Paused status={}", status);
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        return new JsonToken(codeQR, tokenQueue.getBusinessType())
                .setToken(tokenQueue.getLastNumber())
                .setServingNumber(servedNumber)
                .setDisplayName(tokenQueue.getDisplayName())
                .setQueueStatus(QueueStatusEnum.R);
    }

    /**
     * Merchant when starting or re-starting to serve token when QueueState has been either Start or Re-Start.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @return
     */
    @Mobile
    public JsonToken getNextInQueue(
            String codeQR,
            String goTo,
            String sid
    ) {
        LOG.info("Getting next in queue for codeQR={} goTo={} sid={}", codeQR, goTo, sid);

        QueueEntity queue = queueManager.getNext(codeQR, goTo, sid);
        if (null != queue) {
            LOG.info("Found queue codeQR={} token={}", codeQR, queue.getTokenNumber());

            JsonToken jsonToken = tokenQueueService.updateServing(
                    codeQR,
                    QueueStatusEnum.N,
                    queue.getTokenNumber(),
                    goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        /* When nothing is found, return DONE status for the queue. */
        TokenQueueEntity tokenQueue = tokenQueueService.findByCodeQR(codeQR);
        if (null != tokenQueue) {
            LOG.info("On next, found no one in queue, returning with DONE status");
            return new JsonToken(codeQR, tokenQueue.getBusinessType())
                    .setToken(tokenQueue.getLastNumber())
                    .setServingNumber(tokenQueue.getLastNumber())
                    .setDisplayName(tokenQueue.getDisplayName())
                    .setQueueStatus(QueueStatusEnum.D);
        }

        return null;
    }

    /**
     * Merchant when serving a specific token in queue. This is works for out of order request in queue.
     *
     * @param codeQR
     * @param goTo   counter name
     * @param sid    server device id
     * @param token  specific token being requested for next executorService
     * @return
     */
    @Mobile
    public JsonToken getThisAsNextInQueue(
            String codeQR,
            String goTo,
            String sid,
            int token
    ) {
        LOG.info("Getting specific token next in queue for codeQR={} goTo={} sid={} token={}",
                codeQR,
                goTo,
                sid,
                token);

        QueueEntity queue = queueManager.getThisAsNext(codeQR, goTo, sid, token);
        if (null != queue) {
            LOG.info("Found queue codeQR={} token={}", codeQR, queue.getTokenNumber());

            JsonToken jsonToken = tokenQueueService.updateThisServing(
                    codeQR,
                    QueueStatusEnum.N,
                    queue.getTokenNumber(),
                    goTo);
            //TODO(hth) call can be put in thread
            tokenQueueService.changeQueueStatus(codeQR, QueueStatusEnum.N);
            return jsonToken;
        }

        return null;
    }

    public void updateServiceBeginTime(String id) {
        queueManager.updateServiceBeginTime(id);
    }

    private List<YearlyData> lastTwelveMonthVisits(String codeQR) {
        Random rand = new Random();
        return new ArrayList<YearlyData>() {
            {
                add(new YearlyData().setYearMonth(1).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(2).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(3).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(4).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(5).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(6).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(7).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(8).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(9).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(10).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(11).setValue(rand.nextInt(50) + 1));
                add(new YearlyData().setYearMonth(12).setValue(rand.nextInt(50) + 1));
            }
        };
    }

    private NewRepeatCustomers repeatAndNewCustomers(String codeQR) {
        Random rand = new Random();
        return new NewRepeatCustomers().setCustomerNew(rand.nextInt(50) + 1).setCustomerRepeat(rand.nextInt(50) + 1);
    }

    @Mobile
    public HealthCareStatList healthCareStats(String qid) {
        HealthCareStatList healthCareStatList = new HealthCareStatList();
        BusinessUserStoreEntity businessUserStore = businessUserStoreManager.findUserManagingStoreWithUserLevel(qid, UserLevelEnum.S_MANAGER);
        healthCareStatList.addHealthCareStat(
                new HealthCareStat()
                        .setCodeQR(businessUserStore.getCodeQR())
                        .setRepeatCustomers(repeatAndNewCustomers(businessUserStore.getCodeQR()))
                        .setTwelveMonths(lastTwelveMonthVisits(businessUserStore.getCodeQR())));

        return healthCareStatList;
    }
}
