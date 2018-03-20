package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 3/16/18 2:20 PM
 */
public interface MedicalPhysicalExaminationManager extends RepositoryManager<MedicalPhysicalExaminationEntity> {
    List<MedicalPhysicalExaminationEntity> findByRefId(String referenceId);

    void updateWithMedicalPhysicalReferenceId(String id, String referenceId);
}
