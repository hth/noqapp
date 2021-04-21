package com.noqapp.repository;

import com.noqapp.domain.UserSearchEntity;

import java.util.List;

/**
 * hitender
 * 2/13/21 2:34 PM
 */
public interface UserSearchManager extends RepositoryManager<UserSearchEntity> {
    List<String> lastFewSearches(String qid, int limit);
}
