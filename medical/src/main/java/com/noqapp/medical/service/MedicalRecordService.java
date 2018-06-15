package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.PhysicalEntity;
import com.noqapp.medical.domain.json.JsonMedicalPhysicalExamination;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.domain.json.JsonMedicalMedicine;
import com.noqapp.medical.form.MedicalPhysicalForm;
import com.noqapp.medical.form.MedicalRecordForm;
import com.noqapp.medical.repository.MedicalMedicationManager;
import com.noqapp.medical.repository.MedicalMedicineManager;
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

import java.time.Instant;
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
    private MedicalMedicationManager medicalMedicationManager;
    private MedicalMedicineManager medicalMedicineManager;
    private MedicalPhysicalExaminationManager medicalPhysicalExaminationManager;
    private PhysicalManager physicalManager;
    private BizService bizService;

    @Autowired
    public MedicalRecordService(
            MedicalRecordManager medicalRecordManager,
            MedicalPhysicalManager medicalPhysicalManager,
            MedicalMedicationManager medicalMedicationManager,
            MedicalMedicineManager medicalMedicineManager,
            MedicalPhysicalExaminationManager medicalPhysicalExaminationManager,
            PhysicalManager physicalManager,
            BizService bizService
    ) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalMedicationManager = medicalMedicationManager;
        this.medicalMedicineManager = medicalMedicineManager;
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
                .setBusinessType(bizStore.getBusinessType())
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

        if (null != medicalRecordForm.getMedicalPhysicalForms()) {
            populateWithMedicalPhysical(medicalRecordForm, medicalRecord);
        }

        if (null != medicalRecordForm.getMedicalMedicines()) {
            populateWithMedicalMedicine(medicalRecordForm, medicalRecord);
        }

        //TODO remove this temp code below for record access
        medicalRecord.addRecordAccessed(
                Instant.now().toEpochMilli(),
                diagnosedById);
        medicalRecordManager.save(medicalRecord);
        LOG.info("Saved medical record={}", medicalRecord);
    }

    @Mobile
    public void addMedicalRecord(JsonMedicalRecord jsonMedicalRecord) {
        LOG.info("Add medical record");
        BizStoreEntity bizStore = bizService.findByCodeQR(jsonMedicalRecord.getCodeQR());

        MedicalRecordEntity medicalRecord = new MedicalRecordEntity(jsonMedicalRecord.getQueueUserId());
        /* Setting its own ObjectId. */
        medicalRecord.setId(CommonUtil.generateHexFromObjectId());
        medicalRecord
                .setBusinessType(bizStore.getBusinessType())
                .setChiefComplain(StringUtils.capitalize(jsonMedicalRecord.getChiefComplain().trim()))
                .setPastHistory(StringUtils.capitalize(jsonMedicalRecord.getPastHistory().trim()))
                .setFamilyHistory(StringUtils.capitalize(jsonMedicalRecord.getFamilyHistory().trim()))
                .setKnownAllergies(StringUtils.capitalize(jsonMedicalRecord.getKnownAllergies().trim()))
                .setClinicalFinding(StringUtils.capitalize(jsonMedicalRecord.getClinicalFinding().trim()))
                .setProvisionalDifferentialDiagnosis(
                        StringUtils.capitalize(jsonMedicalRecord.getProvisionalDifferentialDiagnosis().trim()))
                .setDiagnosedById(jsonMedicalRecord.getDiagnosedById())
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBizCategoryId(bizStore.getBizCategoryId());

        if (null != jsonMedicalRecord.getMedicalPhysicalExaminations()) {
            populateWithMedicalPhysical(jsonMedicalRecord, medicalRecord);
        }

        if (null != jsonMedicalRecord.getMedicines()) {
            populateWithMedicalMedicine(jsonMedicalRecord, medicalRecord);
        }

        //TODO remove this temp code below for record access
        medicalRecord.addRecordAccessed(
                Instant.now().toEpochMilli(),
                jsonMedicalRecord.getDiagnosedById());
        medicalRecordManager.save(medicalRecord);
        LOG.info("Saved medical record={}", medicalRecord);
    }

    private void populateWithMedicalMedicine(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        MedicalMedicationEntity medicalMedication = new MedicalMedicationEntity();
        medicalMedication
                .setQueueUserId(jsonMedicalRecord.getQueueUserId())
                .setId(CommonUtil.generateHexFromObjectId());

        for (JsonMedicalMedicine jsonMedicalMedicine : jsonMedicalRecord.getMedicines()) {
            MedicalMedicineEntity medicalMedicine = new MedicalMedicineEntity();
            medicalMedicine
                    .setName(jsonMedicalMedicine.getName())
                    .setStrength(jsonMedicalMedicine.getStrength())
                    .setDailyFrequency(jsonMedicalMedicine.getDailyFrequency())
                    .setCourse(jsonMedicalMedicine.getCourse())
                    .setMedicationWithFood(jsonMedicalMedicine.getMedicationWithFood())
                    .setMedicationType(jsonMedicalMedicine.getMedicationType())
                    .setMedicalMedicationReferenceId(medicalMedication.getId())
                    .setPharmacyReferenceId("")             //TODO(hth) with store id
                    .setQueueUserId(jsonMedicalRecord.getQueueUserId())
                    .setId(CommonUtil.generateHexFromObjectId());

            medicalMedicineManager.save(medicalMedicine);
            medicalMedication.addMedicineId(medicalMedicine.getId());
        }

        medicalMedicationManager.save(medicalMedication);
        medicalRecord.setMedicalMedication(medicalMedication);
    }

    //TODO(hth) not tested web logic
    private void populateWithMedicalMedicine(MedicalRecordForm medicalRecordForm, MedicalRecordEntity medicalRecord) {
        MedicalMedicationEntity medicalMedication = new MedicalMedicationEntity();
        medicalMedication.setQueueUserId(medicalRecordForm.getQueueUserId());

        for (MedicalMedicineEntity medicalMedicine : medicalRecordForm.getMedicalMedicines()) {
            medicalMedicineManager.save(medicalMedicine);
            medicalMedication.addMedicineId(medicalMedicine.getId());
        }

        medicalMedicationManager.save(medicalMedication);
        medicalRecord.setMedicalMedication(medicalMedication);
    }

    private void populateWithMedicalPhysical(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        try {
            LOG.info("Populate medical physical qid={}", jsonMedicalRecord.getQueueUserId());

            if (jsonMedicalRecord.getMedicalPhysicalExaminations() != null) {
                MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(jsonMedicalRecord.getQueueUserId());
                /* Setting its own ObjectId. */
                medicalPhysical.setId(CommonUtil.generateHexFromObjectId());

                Set<String> medicalPhysicalExaminationIds = new LinkedHashSet<>();
                for (JsonMedicalPhysicalExamination jsonMedicalPhysicalExamination : jsonMedicalRecord.getMedicalPhysicalExaminations()) {
                    MedicalPhysicalExaminationEntity medicalPhysicalExamination = new MedicalPhysicalExaminationEntity();
                    /* Setting its own ObjectId. */
                    medicalPhysicalExamination.setId(CommonUtil.generateHexFromObjectId());
                    medicalPhysicalExamination
                            .setMedicalPhysicalReferenceId(medicalPhysical.getId())
                            .setPhysicalReferenceId(medicalPhysicalExamination.getPhysicalReferenceId())
                            .setQueueUserId(medicalPhysicalExamination.getQueueUserId())
                            .setName(jsonMedicalPhysicalExamination.getName())
                            .setTestResult(StringUtils.capitalize(jsonMedicalPhysicalExamination.getValue().trim()));

                    medicalPhysicalExaminationManager.save(medicalPhysicalExamination);
                    medicalPhysicalExaminationIds.add(medicalPhysicalExamination.getId());
                }

                medicalPhysical.setMedicalPhysicalExaminationIds(medicalPhysicalExaminationIds);
                LOG.info("Before save of MedicalPhysical={}", medicalPhysical);
                medicalPhysicalManager.save(medicalPhysical);

                /* Add the Medical Physical to Medical Record. */
                medicalRecord.setMedicalPhysical(medicalPhysical);
            }
            LOG.info("Populate medical physical complete medicalPhysical={}", medicalRecord.getMedicalPhysical());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
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
                LOG.info("Before save of MedicalPhysical={}", medicalPhysical);
                medicalPhysicalManager.save(medicalPhysical);

                /* Add the Medical Physical to Medical Record. */
                medicalRecord.setMedicalPhysical(medicalPhysical);
            }
            LOG.info("Populate medical physical complete medicalPhysical={}", medicalRecord.getMedicalPhysical());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    public List<MedicalRecordEntity> historicalRecords(String qid) {
        return medicalRecordManager.historicalRecords(qid, 5);
    }

    public List<MedicalPhysicalExaminationEntity> findByPhysicalRefId(String referenceId) {
        return medicalPhysicalExaminationManager.findByPhysicalRefId(referenceId);
    }

    public List<PhysicalEntity> findAll() {
        return physicalManager.findAll();
    }

    public List<MedicalMedicineEntity> findByIds(String... ids) {
        return medicalMedicineManager.findByIds(ids);
    }

    public List<MedicalMedicineEntity> findByMedicationRefId(String referenceId) {
        return medicalMedicineManager.findByMedicationRefId(referenceId);
    }
}
