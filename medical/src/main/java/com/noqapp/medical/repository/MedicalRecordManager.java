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

    List<MedicalRecordEntity>  findByFollowUpWithoutNotificationSent(int pastHour);
}
