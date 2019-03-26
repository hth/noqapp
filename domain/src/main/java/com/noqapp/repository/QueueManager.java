package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.QueueUserStateEnum;
import com.noqapp.domain.types.SentimentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
public interface QueueManager extends RepositoryManager<QueueEntity> {

    void insert(QueueEntity queue);

    /** Abort queue. Set QueueUserState to Abort. */
    void abort(String id);

    /** Find just the one with qid that has been queued. */
    QueueEntity findQueuedOne(String codeQR, String did, String qid);

    /** Find with qid and all its dependents that has been queued. */
    QueueEntity findAllQueuedOne(String codeQR, String did, String qid);

    QueueEntity findOne(String codeQR, int tokenNumber);
    QueueEntity findByTransactionId(String codeQR, String transactionId);
    void deleteReferenceToTransactionId(String codeQR, String transactionId);

    boolean doesExistsByQid(String codeQR, int tokenNumber, String qid);

    /**
     * Note: No DID used here as user should be able to abort from any where.
     * When registered from Web there is no device id. Hence allow abort from any logged in device.
     */
    QueueEntity findToAbort(String codeQR, String qid);

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

    @Mobile
    boolean onPaymentChangeToQueue(String id, int tokenNumber, Date expectedServiceBegin);

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

    /** Find queued in a specific queue. */
    List<QueueEntity> findInAQueueByQid(String qid, String codeQR);

    /** Find with any queued state in a specific queue. */
    List<QueueEntity> findInAQueueByQidWithAnyQueueState(String qid, String codeQR);

    /** Finds one, no matter whats the state. We are finding the one where QID is of the Client and not guardian. */
    QueueEntity findOneQueueByQid(String qid, String codeQR);

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
    List<QueueEntity> findByCodeQRSortedByToken(String codeQR);

    long deleteByCodeQR(String codeQR);

    void increaseAttemptToSendNotificationCount(String id);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved, String review, SentimentTypeEnum sentimentType);

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

    QueueEntity changeUserInQueue(String codeQR, int tokenNumber, String existingQueueUserId, String changeToQueueUserId);

    /* Limit to top 10. */
    List<QueueEntity> findYetToBeServed(String codeQR);

    List<QueueEntity> findReviews(String codeQR);

    List<QueueEntity> findLevelUpReviews(String bizNameId);

    QueueEntity findOneByRecordReferenceId(String codeQR, String recordReferenceId);

    void updateWithTransactionId(String codeQR, String qid, String transactionId);
}
