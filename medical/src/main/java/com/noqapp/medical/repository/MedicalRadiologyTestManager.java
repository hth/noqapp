package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalRadiologyTestEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 8/2/18 6:36 PM
 */
public interface MedicalRadiologyTestManager extends RepositoryManager<MedicalRadiologyTestEntity> {

    List<MedicalRadiologyTestEntity> findRadiologyTestByIds(String medicalRadiologyReferenceId);

    void deleteByRadiologyReferenceId(String medicalRadiologyReferenceId);

    void changePatient(String medicalRadiologyReferenceId, String queueUserId);
}
