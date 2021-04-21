package com.noqapp.service;

import com.noqapp.domain.UserSearchEntity;
import com.noqapp.repository.UserSearchManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * hitender
 * 2/13/21 3:09 PM
 */
@Service
public class UserSearchService {
    private static final Logger LOG = LoggerFactory.getLogger(UserSearchService.class);

    private UserSearchManager userSearchManager;

    @Autowired
    public UserSearchService(UserSearchManager userSearchManager) {
        this.userSearchManager = userSearchManager;
    }

    @Async
    public void save(UserSearchEntity userSearch) {
        userSearchManager.save(userSearch);
    }

    public List<String> lastFewSearches(String qid, int limit) {
        return userSearchManager.lastFewSearches(qid, limit);
    }
}
