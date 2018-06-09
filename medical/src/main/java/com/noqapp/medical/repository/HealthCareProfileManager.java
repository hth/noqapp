package com.noqapp.medical.repository;

import com.noqapp.medical.domain.HealthCareProfileEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * hitender
 * 5/30/18 3:54 PM
 */
public interface HealthCareProfileManager extends RepositoryManager<HealthCareProfileEntity> {

    HealthCareProfileEntity findOne(String qid);

    boolean existsQid(String qid);

    /** Remove soft delete when the person is added again. */
    void removeMarkedAsDeleted(String qid);

    HealthCareProfileEntity findByWebProfileId(String webProfileId);
}
