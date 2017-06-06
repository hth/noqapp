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
     * @param rid
     * @return
     */
    QueueEntity findQueuedOne(String codeQR, String did, String rid);

    QueueEntity findOne(String codeQR, int tokenNumber);

    QueueEntity findToAbort(String codeQR, String did, String rid);

    @Mobile
    QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState, String goTo);

    @Mobile
    boolean updateServedInQueue(String codeQR, int tokenNumber, QueueUserStateEnum queueUserState);

    @Mobile
    QueueEntity getNext(String codeQR, String goTo);

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
     * @param rid
     * @return
     */
    @Mobile
    List<QueueEntity> findAllQueuedByRid(String rid);

    @Mobile
    List<QueueEntity> findAllNotQueuedByDid(String did);

    @Mobile
    List<QueueEntity> findAllNotQueuedByRid(String rid);

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
    boolean reviewService(String codeQR, int token, String did, String rid, int ratingCount, int hoursSaved);
}
