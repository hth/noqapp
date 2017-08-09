package com.noqapp.repository;

import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.QueueUserStateEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
public interface QueueManager extends RepositoryManager<QueueEntity> {

    void insert(QueueEntity queue);

    /**
     * Abort queue. Set QueueUserState to Abort.
     *
     * @param id
     */
    void abort(String id);

    /**
     * Find the one that has been queued.
     *
     * @param codeQR
     * @param did
     * @param qid
     * @return
     */
    QueueEntity findQueuedOne(String codeQR, String did, String qid);

    QueueEntity findOne(String codeQR, int tokenNumber);

    QueueEntity findToAbort(String codeQR, String did, String qid);

    @Mobile
    QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String goTo, String sid);

    @Mobile
    boolean updateServedInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String sid);

    @Mobile
    QueueEntity getNext(String codeQR, String goTo, String sid);

    /**
     * Find all based on device id, this is when user is not registered.
     *
     * @param did
     * @return
     */
    @Mobile
    List<QueueEntity> findAllQueuedByDid(String did);

    /**
     * Find all based on registered user.
     *
     * @param qid
     * @return
     */
    @Mobile
    List<QueueEntity> findAllQueuedByRid(String qid);

    @Mobile
    List<QueueEntity> findAllNotQueuedByDid(String did);

    @Mobile
    List<QueueEntity> findAllNotQueuedByRid(String qid);

    @Mobile
    boolean isQueued(int tokenNumber, String codeQR);

    /**
     * Find all clients serviced to send messages.
     *
     * @param attemptToSendNotificationCounts
     * @return
     */
    List<QueueEntity> findAllClientServiced(int attemptToSendNotificationCounts);

    List<QueueEntity> findByCodeQR(String codeQR);

    int deleteByCodeQR(String codeQR);

    void increaseAttemptToSendNotificationCount(String id);

    @Mobile
    boolean reviewService(String codeQR, int token, String did, String qid, int ratingCount, int hoursSaved);
}
