package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * hitender
 * 6/15/18 12:06 AM
 */
public interface MedicalMedicationManager extends RepositoryManager<MedicalMedicationEntity> {

    MedicalMedicationEntity findOneById(String id);

    void deleteHard(String id);

    void changePatient(String id, String queueUserId);
}
