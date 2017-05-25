package com.noqapp.repository;

import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.QueueStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/16/16 8:50 AM
 */
public interface TokenQueueManager extends RepositoryManager<TokenQueueEntity> {

    /**
     * This query prefers to read from primary.
     *
     * @param codeQR
     * @return
     */
    TokenQueueEntity findByCodeQR(String codeQR);

    /**
     * This query prefers to read and write from primary.
     *
     * @param codeQR
     * @return
     */
    TokenQueueEntity getNextToken(String codeQR);

    TokenQueueEntity updateServing(String codeQR, int serving, QueueStatusEnum queueStatus);

    List<TokenQueueEntity> getTokenQueues(String[] ids);

    void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus);

    void resetForNewDay(String codeQR);
}
