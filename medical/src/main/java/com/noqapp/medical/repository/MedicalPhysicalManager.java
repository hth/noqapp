package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 3/16/18 1:44 PM
 */
public interface MedicalPhysicalManager extends RepositoryManager<MedicalPhysicalEntity> {

    MedicalPhysicalEntity findOne(String id);

    List<MedicalPhysicalEntity> findByQid(String qid);

    void deleteHard(String id);

    void changePatient(String medicalPhysicalId, String queueUserId);
}
