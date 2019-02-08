package com.noqapp.repository;

import com.noqapp.domain.PreferredBusinessEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 8/12/18 3:28 PM
 */
public interface PreferredBusinessManager extends RepositoryManager<PreferredBusinessEntity> {

    List<PreferredBusinessEntity> findAll(String bizNameId);

    void deleteById(String id);

    boolean exists(String bizNameId, String preferredBizNameId);
}
