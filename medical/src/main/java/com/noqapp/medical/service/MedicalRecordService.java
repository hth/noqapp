package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
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
import com.noqapp.service.BizService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 3/16/18 1:17 PM
 */
@Service
public class MedicalRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordService.class);

    private MedicalRecordManager medicalRecordManager;
    private MedicalPhysicalManager medicalPhysicalManager;
    private MedicalPhysicalExaminationManager medicalPhysicalExaminationManager;
    private PhysicalManager physicalManager;
    private BizService bizService;

    @Autowired
    public MedicalRecordService(
            MedicalRecordManager medicalRecordManager,
            MedicalPhysicalManager medicalPhysicalManager,
            MedicalPhysicalExaminationManager medicalPhysicalExaminationManager,
            PhysicalManager physicalManager,
            BizService bizService) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalPhysicalExaminationManager = medicalPhysicalExaminationManager;
        this.physicalManager = physicalManager;
        this.bizService = bizService;
    }

    public void addMedicalRecord(MedicalRecordForm medicalRecordForm, String diagnosedById, String codeQR) {
        LOG.info("Add medical record");
        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);

        MedicalRecordEntity medicalRecord = new MedicalRecordEntity(medicalRecordForm.getQueueUserId());
        /* Setting its own ObjectId. */
        medicalRecord.setId(CommonUtil.generateHexFromObjectId());
        medicalRecord
                .setBusinessType(medicalRecordForm.getBusinessType())
                .setChiefComplain(StringUtils.capitalize(medicalRecordForm.getChiefComplain().trim()))
                .setPastHistory(StringUtils.capitalize(medicalRecordForm.getPastHistory().trim()))
                .setFamilyHistory(StringUtils.capitalize(medicalRecordForm.getFamilyHistory().trim()))
                .setKnownAllergies(StringUtils.capitalize(medicalRecordForm.getKnownAllergies().trim()))
                .setClinicalFinding(StringUtils.capitalize(medicalRecordForm.getClinicalFinding().trim()))
                .setProvisionalDifferentialDiagnosis(
                        StringUtils.capitalize(medicalRecordForm.getProvisionalDifferentialDiagnosis().trim()))
                .setDiagnosedById(diagnosedById)
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBizCategoryId(bizStore.getBizCategoryId());

        populateWithMedicalPhysical(medicalRecordForm, medicalRecord);

        //TODO remove this temp code below for record access
        medicalRecord.addRecordAccessed(new Date(), diagnosedById);
        medicalRecordManager.save(medicalRecord);
        LOG.info("Saved medical record");
    }

    private void populateWithMedicalPhysical(MedicalRecordForm medicalRecordForm, MedicalRecordEntity medicalRecord) {
        try {
            LOG.info("Populate medical physical qid={}", medicalRecordForm.getQueueUserId());

            if (medicalRecordForm.getMedicalPhysicalForms() != null) {
                MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(medicalRecordForm.getQueueUserId());
                /* Setting its own ObjectId. */
                medicalPhysical.setId(CommonUtil.generateHexFromObjectId());

                Set<String> medicalPhysicalExaminationIds = new LinkedHashSet<>();
                for (MedicalPhysicalForm medicalPhysicalForm : medicalRecordForm.getMedicalPhysicalForms()) {
                    MedicalPhysicalExaminationEntity medicalPhysicalExamination = new MedicalPhysicalExaminationEntity();
                    /* Setting its own ObjectId. */
                    medicalPhysicalExamination.setId(CommonUtil.generateHexFromObjectId());
                    medicalPhysicalExamination
                            .setMedicalPhysicalReferenceId(medicalPhysical.getId())
                            .setPhysicalReferenceId(medicalPhysicalForm.getPhysicalReferenceId())
                            .setQueueUserId(medicalRecordForm.getQueueUserId())
                            .setName(medicalPhysicalForm.getName())
                            .setTestResult(StringUtils.capitalize(medicalPhysicalForm.getValue().trim()));

                    medicalPhysicalExaminationManager.save(medicalPhysicalExamination);
                    medicalPhysicalExaminationIds.add(medicalPhysicalExamination.getId());
                }

                medicalPhysical.setMedicalPhysicalExaminationIds(medicalPhysicalExaminationIds);
                medicalPhysicalManager.save(medicalPhysical);

                /* Add the Medical Physical to Medical Record. */
                medicalRecord.setMedicalPhysical(medicalPhysical);
            }
            LOG.info("Populate medical physical complete");
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
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
