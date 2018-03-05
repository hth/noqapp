package com.noqapp.repository;

import com.noqapp.domain.BrowserEntity;

/**
 * User: hitender
 * Date: 11/19/16 7:15 PM
 */
public interface BrowserManager extends RepositoryManager<BrowserEntity> {
    BrowserEntity getByCookie(String id);

    void update(String id);
}

