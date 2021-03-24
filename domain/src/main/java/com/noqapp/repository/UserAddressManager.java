package com.noqapp.repository;

import com.noqapp.domain.UserAddressEntity;

import java.util.List;

/**
 * hitender
 * 5/15/18 10:45 PM
 */
public interface UserAddressManager extends RepositoryManager<UserAddressEntity> {

    List<UserAddressEntity> getAll(String qid);

    long count(String qid);

    void updateLastUsedAddress(String id, String qid);

    void deleteAddress(String id, String qid);

    boolean doesAddressExists(String id, String qid);

    UserAddressEntity findById(String id);

    UserAddressEntity findByAddress(String qid, String address);

    /** If primary address exists then return primary or find any other address. */
    UserAddressEntity findPrimaryOrAnyExistingAddress(String qid);

    UserAddressEntity markAddressPrimary(String id, String qid);
}
