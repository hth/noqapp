package com.noqapp.medical.repository;

import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.repository.RepositoryManager;

import java.util.List;

/**
 * hitender
 * 3/16/18 1:22 PM
 */
public interface MedicalRecordManager extends RepositoryManager<MedicalRecordEntity> {

    List<MedicalRecordEntity> historicalRecords(String qid, int limit);

    MedicalRecordEntity findById(String id);

    List<MedicalRecordEntity> findByFollowUpWithoutNotificationSent(int afterHour, int beforeHour);

    /** Finds followup between two dates i.e now and three days from now. */
    List<MedicalRecordEntity> findAllFollowUp(String codeQR);

    //TODO remove me
    MedicalRecordEntity findOne();

    void addTransactionId(String recordReferenceId, String transactionId);

    void addMedicalMedicationId(String recordReferenceId, String medicalMedicationId);

    void addMedicalLaboratoryId(String recordReferenceId, String medicalLaboratoryId);

    void addMedicalRadiologiesId(String recordReferenceId, String medicalRadiologyId);
    void unsetMedicalRadiology(String recordReferenceId);
}
