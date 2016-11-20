package com.token.repository.social;

import com.token.domain.RememberMeTokenEntity;
import com.token.repository.RepositoryManager;

/**
 * User: hitender
 * Date: 11/18/16 3:17 PM
 */
public interface RememberMeTokenManager extends RepositoryManager<RememberMeTokenEntity> {
    RememberMeTokenEntity findBySeries(String series);

    boolean existsBySeries(String series);

    void deleteTokensWithUsername(String username);

    void updateToken(String series, String tokenValue);
}