package com.token.repository;

import com.token.domain.TokenQueueEntity;
import com.token.domain.types.QueueStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/16/16 8:50 AM
 */
public interface TokenQueueManager extends RepositoryManager<TokenQueueEntity> {

    TokenQueueEntity findByCodeQR(String codeQR);

    TokenQueueEntity getNextToken(String codeQR);

    TokenQueueEntity updateServing(String codeQR, int serving);

    List<TokenQueueEntity> getTokenQueues(String[] ids);

    void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus);
}
