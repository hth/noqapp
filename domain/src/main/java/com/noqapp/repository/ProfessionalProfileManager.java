package com.noqapp.repository;

import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * hitender
 * 5/30/18 3:54 PM
 */
public interface ProfessionalProfileManager extends RepositoryManager<ProfessionalProfileEntity> {

    ProfessionalProfileEntity findOne(String qid);

    boolean existsQid(String qid);

    /** Remove soft delete when the person is added again. */
    void removeMarkedAsDeleted(String qid);

    ProfessionalProfileEntity findByWebProfileId(String webProfileId);
}
