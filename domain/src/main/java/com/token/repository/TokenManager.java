package com.token.repository;

import com.token.domain.TokenEntity;

/**
 * User: hitender
 * Date: 12/16/16 8:50 AM
 */
public interface TokenManager extends RepositoryManager<TokenEntity> {

    TokenEntity findByCodeQR(String codeQR);
}
