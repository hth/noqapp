package com.noqapp.repository;

import com.noqapp.domain.UserAuthenticationEntity;

/**
 * User: hitender
 * Date: 11/19/16 1:48 AM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
    UserAuthenticationEntity getById(String id);

}
