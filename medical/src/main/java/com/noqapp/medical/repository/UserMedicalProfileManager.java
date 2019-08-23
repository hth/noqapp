package com.noqapp.medical.repository;

import com.noqapp.medical.domain.UserMedicalProfileEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * hitender
 * 5/30/18 5:11 AM
 */
public interface UserMedicalProfileManager extends RepositoryManager<UserMedicalProfileEntity> {

    UserMedicalProfileEntity findOne(String qid);

    void updateDentalAnatomy(String qid, String dentalAnatomy, String diagnosedById);
}
