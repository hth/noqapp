package com.token.repository;

import com.token.domain.RegisteredDeviceEntity;
import com.token.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 3/1/17 12:26 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    @Mobile
    RegisteredDeviceEntity find(String did, String token);

    String findFCMToken(String rid, String did);
}
