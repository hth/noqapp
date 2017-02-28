package com.token.repository;

import com.token.domain.QueueEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.types.QueueStateEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
public interface QueueManager extends RepositoryManager<QueueEntity> {
    QueueEntity findOne(String codeQR, String did, String rid);
    QueueEntity findToAbort(String codeQR, String did, String rid);

    @Mobile
    QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueStateEnum queueState);

    /**
     * Find all based on device id, this is when user is not registered.
     *
     * @param did
     * @return
     */
    @Mobile
    List<QueueEntity> findAllByDid(String did);

    /**
     * Find all based on registered user.
     *
     * @param rid
     * @return
     */
    @Mobile
    List<QueueEntity> findAllByRid(String rid);
}
