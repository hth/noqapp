package com.token.repository;

import com.token.domain.BrowserEntity;

/**
 * User: hitender
 * Date: 11/19/16 7:15 PM
 */
public interface BrowserManager extends RepositoryManager<BrowserEntity> {
    BrowserEntity getByCookie(String id);
}

