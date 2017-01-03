package com.token.repository;

import com.token.domain.TokenQueueEntity;

/**
 * User: hitender
 * Date: 12/16/16 8:50 AM
 */
public interface TokenQueueManager extends RepositoryManager<TokenQueueEntity> {

    TokenQueueEntity findByCodeQR(String codeQR);

    TokenQueueEntity getNextToken(String codeQR);
}
