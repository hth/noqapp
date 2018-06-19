package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
public interface QueueManager extends RepositoryManager<QueueEntity> {

    void insert(QueueEntity queue);

    /** Abort queue. Set QueueUserState to Abort. */
    void abort(String id);

    /** Find the one that has been queued. */
    List<QueueEntity> findAllQueuedByQid(String codeQR, String did, String qid);

    QueueEntity findOne(String codeQR, int tokenNumber);

    QueueEntity findToAbort(String codeQR, String did, String qid);

    @Mobile
    QueueEntity updateAndGetNextInQueue(
            String codeQR,
            int tokenNumber,
            QueueUserStateEnum queueUserState,
            String goTo,
            String sid,
            TokenServiceEnum tokenService);

    @Mobile
    boolean updateServedInQueue(
            String codeQR,
            int tokenNumber,
            QueueUserStateEnum queueUserState,
            String sid,
            TokenServiceEnum tokenService);

    /**
     * Gets next token. By default this gets the next token in sequence/order of ascending .
     *
     * @param codeQR
     * @param goTo   go tp counter name
     * @param sid    server device id
     * @return
     */
    @Mobile
    QueueEntity getNext(String codeQR, String goTo, String sid);

    /**
     * Gets a specific token as next. Used when serving NOT in sequence.
     *
     * @param codeQR
     * @param goTo        go to counter name
     * @param sid         server device id
     * @param tokenNumber asking for serving a specific token in queue
     * @return
     */
    @Mobile
    QueueEntity getThisAsNext(String codeQR, String goTo, String sid, int tokenNumber);

    /** Find all based on device id, this is when user is not registered. */
    @Mobile
    List<QueueEntity> findAllQueuedByDid(String did);

    /** Find all based on registered user. */
    List<QueueEntity> findAllQueuedByQid(String qid);

    /** Get all the queues that have been serviced for today by DID. */
    @Mobile
    List<QueueEntity> findAllNotQueuedByDid(String did);

    /** Get all the queues that have been serviced for today by QID. */
    @Mobile
    List<QueueEntity> findAllNotQueuedByQid(String qid);

    @Mobile
    boolean isQueued(int tokenNumber, String codeQR);

    /** Find all clients serviced to send messages. */
    List<QueueEntity> findAllClientServiced(int numberOfAttemptsToSendFCM);

    List<QueueEntity> findByCodeQR(String codeQR);

    long deleteByCodeQR(String codeQR);

    void increaseAttemptToSendNotificationCount(String id);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved);

    @Mobile
    List<QueueEntity> findAllClientQueuedOrAborted(String codeQR);

    @Mobile
    long countAllQueued(String codeQR);

    long previouslyVisitedClientCount(String codeQR);
    long newVisitClientCount(String codeQR);

    /** Mostly when client is from Web. */
    QueueEntity findQueuedByPhone(String codeQR, String phone);

    void addPhoneNumberToExistingQueue(int token, String codeQR, String did, String customerPhone);

    @Mobile
    long markAllAbortWhenQueueClosed(String codeQR, String serverDeviceId);

    void updateServiceBeginTime(String id);
}
