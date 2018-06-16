package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 3/16/18 1:44 PM
 */
public interface MedicalPhysicalManager extends RepositoryManager<MedicalPhysicalEntity> {

    List<MedicalPhysicalEntity> findByQid(String qid);
}
