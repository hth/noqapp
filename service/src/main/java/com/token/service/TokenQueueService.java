package com.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.TokenQueueEntity;
import com.token.repository.TokenQueueManager;

/**
 * User: hitender
 * Date: 12/16/16 9:42 AM
 */
@Service
public class TokenQueueService {
    private TokenQueueManager tokenQueueManager;

    @Autowired
    public TokenQueueService(TokenQueueManager tokenQueueManager) {
        this.tokenQueueManager = tokenQueueManager;
    }

    //TODO has to create by cron job
    public void create(String codeQR) {
        TokenQueueEntity token = new TokenQueueEntity();
        token.setId(codeQR);
        tokenQueueManager.save(token);
    }

    public TokenQueueEntity findByCodeQR(String codeQR) {
        return tokenQueueManager.findByCodeQR(codeQR);
    }
}
