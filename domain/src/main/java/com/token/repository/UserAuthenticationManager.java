package com.token.repository;

import com.token.domain.UserAuthenticationEntity;

/**
 * User: hitender
 * Date: 11/19/16 1:48 AM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
    UserAuthenticationEntity getById(String id);

}
