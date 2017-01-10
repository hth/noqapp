package com.token.repository;

import com.token.domain.QueueEntity;
import com.token.domain.annotation.Mobile;
import com.token.domain.types.QueueStateEnum;

/**
 * User: hitender
 * Date: 1/2/17 8:32 PM
 */
public interface QueueManager extends RepositoryManager<QueueEntity> {
    QueueEntity findOne(String codeQR, String did, String rid);

    @Mobile
    QueueEntity updateAndGetNextInQueue(String codeQR, int tokenNumber, QueueStateEnum queueState);
}
