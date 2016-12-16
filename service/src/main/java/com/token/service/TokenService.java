package com.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.token.domain.TokenEntity;
import com.token.repository.TokenManager;

/**
 * User: hitender
 * Date: 12/16/16 9:42 AM
 */
@Service
public class TokenService {
    private TokenManager tokenManager;

    @Autowired
    public TokenService(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    //TODO has to create by cron job
    public void create(String codeQR) {
        TokenEntity token = new TokenEntity(codeQR);
        tokenManager.save(token);
    }

    public TokenEntity findByCodeQR(String codeQR) {
        return tokenManager.findByCodeQR(codeQR);
    }
}
