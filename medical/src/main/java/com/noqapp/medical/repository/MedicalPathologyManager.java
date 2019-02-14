package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.repository.RepositoryManager;

/**
 * hitender
 * 7/25/18 1:32 AM
 */
public interface MedicalPathologyManager extends RepositoryManager<MedicalPathologyEntity> {
    void deleteHard(String id);

    void updateWithTransactionId(String id, String transactionId);

    MedicalPathologyEntity findByTransactionId(String transactionId);

    MedicalPathologyEntity findById(String id);
}
