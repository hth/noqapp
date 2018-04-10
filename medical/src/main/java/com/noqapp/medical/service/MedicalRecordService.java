package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.PhysicalEntity;
import com.noqapp.medical.form.MedicalPhysicalForm;
import com.noqapp.medical.form.MedicalRecordForm;
import com.noqapp.medical.repository.MedicalPhysicalExaminationManager;
import com.noqapp.medical.repository.MedicalPhysicalManager;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.medical.repository.PhysicalManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 3/16/18 1:17 PM
 */
@Service
public class MedicalRecordService {

    private MedicalRecordManager medicalRecordManager;
    private MedicalPhysicalManager medicalPhysicalManager;
    private MedicalPhysicalExaminationManager medicalPhysicalExaminationManager;
    private PhysicalManager physicalManager;

    @Autowired
    public MedicalRecordService(
            MedicalRecordManager medicalRecordManager,
            MedicalPhysicalManager medicalPhysicalManager,
            MedicalPhysicalExaminationManager medicalPhysicalExaminationManager,
            PhysicalManager physicalManager) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalPhysicalExaminationManager = medicalPhysicalExaminationManager;
        this.physicalManager = physicalManager;
    }

    public void addMedicalRecord(MedicalRecordForm medicalRecordForm, String diagnosedById) {
        MedicalRecordEntity medicalRecord = new MedicalRecordEntity(medicalRecordForm.getQueueUserId());
        medicalRecord.setBusinessType(medicalRecordForm.getBusinessType())
                .setChiefComplain(StringUtils.capitalize(medicalRecordForm.getChiefComplain().trim()))
                .setPastHistory(StringUtils.capitalize(medicalRecordForm.getPastHistory().trim()))
                .setFamilyHistory(StringUtils.capitalize(medicalRecordForm.getFamilyHistory().trim()))
                .setKnownAllergies(StringUtils.capitalize(medicalRecordForm.getKnownAllergies().trim()))
                .setClinicalFinding(StringUtils.capitalize(medicalRecordForm.getClinicalFinding().trim()))
                .setProvisionalDifferentialDiagnosis(StringUtils.capitalize(medicalRecordForm.getProvisionalDifferentialDiagnosis().trim()))
                .setDiagnosedById(diagnosedById);

        if (medicalRecordForm.getMedicalPhysicalForms() != null) {
            Set<String> medicalPhysicalExaminationIds = new LinkedHashSet<>();

            for (MedicalPhysicalForm medicalPhysicalForm : medicalRecordForm.getMedicalPhysicalForms()) {
                MedicalPhysicalExaminationEntity medicalPhysicalExamination = new MedicalPhysicalExaminationEntity();
                medicalPhysicalExamination.setId(CommonUtil.generateHexFromObjectId());
                medicalPhysicalExamination
                        .setPhysicalReferenceId(medicalPhysicalForm.getPhysicalReferenceId())
                        .setQueueUserId(medicalRecordForm.getQueueUserId())
                        .setName(medicalPhysicalForm.getName())
                        .setTestResult(StringUtils.capitalize(medicalPhysicalForm.getValue().trim()));

                medicalPhysicalExaminationManager.save(medicalPhysicalExamination);
                medicalPhysicalExaminationIds.add(medicalPhysicalExamination.getId());
            }
            MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(medicalRecordForm.getQueueUserId());
            medicalPhysical.setMedicalPhysicalExaminationIds(medicalPhysicalExaminationIds);
            medicalRecord.setMedicalPhysical(medicalPhysical);

            medicalPhysicalManager.save(medicalPhysical);
            for (String medicalPhysicalExaminationId : medicalPhysicalExaminationIds) {
                medicalPhysicalExaminationManager.updateWithMedicalPhysicalReferenceId(medicalPhysicalExaminationId, medicalPhysical.getId());
            }
        }

        medicalRecordManager.save(medicalRecord);
    }

    public List<MedicalRecordEntity> historicalRecords(String qid) {
        return medicalRecordManager.historicalRecords(qid, 5);
    }

    public List<MedicalPhysicalExaminationEntity> findByRefId(String referenceId) {
        return medicalPhysicalExaminationManager.findByRefId(referenceId);
    }

    public List<PhysicalEntity> findAll() {
        return physicalManager.findAll();
    }
}
