package com.noqapp.repository;

import com.noqapp.domain.ProfessionalProfileEntity;

/**
 * hitender
 * 5/30/18 3:54 PM
 */
public interface ProfessionalProfileManager extends RepositoryManager<ProfessionalProfileEntity> {

    ProfessionalProfileEntity findOne(String qid);

    boolean existsQid(String qid);

    /** Remove soft delete when the person is added again. */
    ProfessionalProfileEntity removeMarkedAsDeleted(String qid);

    ProfessionalProfileEntity findByWebProfileId(String webProfileId);

    ProfessionalProfileEntity findByStoreCodeQR(String codeQR);
}
