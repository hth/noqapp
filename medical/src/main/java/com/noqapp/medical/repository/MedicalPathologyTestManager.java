package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalPathologyTestEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 7/25/18 1:35 AM
 */
public interface MedicalPathologyTestManager extends RepositoryManager<MedicalPathologyTestEntity> {

    List<MedicalPathologyTestEntity> findPathologyTestByIds(String medicalPathologyReferenceId);

    void deleteByPathologyReferenceId(String medicalPathologyReferenceId);

    void changePatient(String medicalPathologyReferenceId, String queueUserId);
}
