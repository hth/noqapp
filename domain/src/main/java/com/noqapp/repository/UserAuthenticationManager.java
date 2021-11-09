package com.noqapp.repository;

import com.noqapp.domain.UserAuthenticationEntity;

import java.util.Date;
import java.util.stream.Stream;

/**
 * User: hitender
 * Date: 11/19/16 1:48 AM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
    UserAuthenticationEntity getById(String id);

    /**
     * For security sake update authenticationKey after every OTP/Login. This eliminates duplicate login.
     *
     * @param id
     * @param authenticationKey
     */
    void updateAuthenticationKey(String id, String authenticationKey);

    Stream<UserAuthenticationEntity> listAll(Date sinceThen);
}
