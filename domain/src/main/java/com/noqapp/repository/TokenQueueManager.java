package com.noqapp.repository;

import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 12/16/16 8:50 AM
 */
public interface TokenQueueManager extends RepositoryManager<TokenQueueEntity> {

    TokenQueueEntity findByCodeQR(String codeQR);

    TokenQueueEntity getNextToken(String codeQR, int availableTokenCount);

    TokenQueueEntity updateServing(String codeQR, int serving, QueueStatusEnum queueStatus);

    List<TokenQueueEntity> getTokenQueues(String[] ids);

    void changeQueueStatus(String codeQR, QueueStatusEnum queueStatus);

    void resetForNewDay(String codeQR);

    boolean updateDisplayNameAndBusinessType(
        String codeQR,
        String topic,
        String displayName,
        BusinessTypeEnum businessType,
        String appendPrefix,
        String bizCategoryId
    );

    void changeStoreBusinessType(String codeQR, BusinessTypeEnum existingBusinessType, BusinessTypeEnum migrateToBusinessType);
}
