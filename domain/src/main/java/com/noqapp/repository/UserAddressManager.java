package com.noqapp.repository;

import com.noqapp.domain.UserAddressEntity;

import java.util.List;

/**
 * hitender
 * 5/15/18 10:45 PM
 */
public interface UserAddressManager extends RepositoryManager<UserAddressEntity> {

    List<UserAddressEntity> getAll(String qid);

    void updateLastUsedAddress(String address, String qid);

    void deleteAddress(String id, String qid);

    boolean doesAddressExists(String id, String qid);
}
