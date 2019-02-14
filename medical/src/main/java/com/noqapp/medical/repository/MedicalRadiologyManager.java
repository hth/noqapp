package com.noqapp.medical.repository;

import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 8/2/18 6:36 PM
 */
public interface MedicalRadiologyManager  extends RepositoryManager<MedicalRadiologyEntity> {

    List<MedicalRadiologyEntity> findByIds(List<String> ids);

    MedicalRadiologyEntity findOne(List<String> ids, LabCategoryEnum labCategory);

    void updateWithTransactionId(String id, String transactionId);

    MedicalRadiologyEntity findByTransactionId(String transactionId);

    MedicalRadiologyEntity findById(String id);
}
