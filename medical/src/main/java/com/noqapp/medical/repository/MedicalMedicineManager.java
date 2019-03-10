package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 6/15/18 12:06 AM
 */
public interface MedicalMedicineManager extends RepositoryManager<MedicalMedicineEntity> {

    List<MedicalMedicineEntity> findByQid(String qid);

    List<MedicalMedicineEntity> findByMedicationRefId(String medicalMedicineReferenceId);

    void deleteByMedicationRefId(String medicalMedicineReferenceId);

    List<MedicalMedicineEntity> findByIds(List<String> ids);

    void changePatient(String medicalMedicineReferenceId, String queueUserId);
}
