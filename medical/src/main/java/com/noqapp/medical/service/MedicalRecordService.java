package com.noqapp.medical.service;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.json.JsonMedicalMedicine;
import com.noqapp.medical.domain.json.JsonMedicalPhysical;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.domain.json.JsonMedicalRecordList;
import com.noqapp.medical.domain.json.JsonRecordAccess;
import com.noqapp.medical.form.MedicalRecordForm;
import com.noqapp.medical.repository.MedicalMedicationManager;
import com.noqapp.medical.repository.MedicalMedicineManager;
import com.noqapp.medical.repository.MedicalPhysicalManager;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private UserProfileManager userProfileManager;

    @Autowired
    public MedicalRecordService(
            MedicalRecordManager medicalRecordManager,
            MedicalPhysicalManager medicalPhysicalManager,
            MedicalMedicationManager medicalMedicationManager,
            MedicalMedicineManager medicalMedicineManager,
            BizService bizService,
            BusinessUserStoreService businessUserStoreService,
            UserProfileManager userProfileManager
    ) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalMedicationManager = medicalMedicationManager;
        this.medicalMedicineManager = medicalMedicineManager;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.userProfileManager = userProfileManager;
    }

    //TODO add check for qid
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

        if (null != medicalRecordForm.getMedicalPhysicalHistoricals()) {
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

        /* Check if user has proper role to allow adding of medical record. */
        if (!businessUserStoreService.hasAccessWithUserLevel(jsonMedicalRecord.getDiagnosedById(), jsonMedicalRecord.getCodeQR(), UserLevelEnum.S_MANAGER)) {
            LOG.info("Your are not authorized to add medical record mail={}", jsonMedicalRecord.getDiagnosedById());
            return;
        }

        /* Check if business type is of Hospital or Doctor to allow adding record. */
        BizStoreEntity bizStore = bizService.findByCodeQR(jsonMedicalRecord.getCodeQR());
        if (bizStore.getBusinessType() != BusinessTypeEnum.DO && bizStore.getBizName().getBusinessType() != BusinessTypeEnum.DO) {
            LOG.error("Failed as its not a Doctor or Hospital business type, found store={} biz={}",
                    bizStore.getBusinessType(),
                    bizStore.getBizName().getBusinessType());
            return;
        }

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

        if (null != jsonMedicalRecord.getMedicalPhysical()) {
            populateWithMedicalPhysical(jsonMedicalRecord, medicalRecord);
        }

        if (null != jsonMedicalRecord.getMedicalMedicines()) {
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

        for (JsonMedicalMedicine jsonMedicalMedicine : jsonMedicalRecord.getMedicalMedicines()) {
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

            if (jsonMedicalRecord.getMedicalPhysical() != null) {
                MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(jsonMedicalRecord.getQueueUserId());
                /* Setting its own ObjectId. */
                medicalPhysical.setId(CommonUtil.generateHexFromObjectId());
                medicalPhysical
                        .setBloodPressure(jsonMedicalRecord.getMedicalPhysical().getBloodPressure())
                        .setPluse(jsonMedicalRecord.getMedicalPhysical().getPluse())
                        .setWeight(jsonMedicalRecord.getMedicalPhysical().getWeight());

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

            if (medicalRecordForm.getMedicalPhysical() != null) {
                MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(medicalRecordForm.getQueueUserId());
                /* Setting its own ObjectId. */
                medicalPhysical.setId(CommonUtil.generateHexFromObjectId());
                medicalPhysical
                        .setBloodPressure(medicalPhysical.getBloodPressure())
                        .setPluse(medicalPhysical.getPluse())
                        .setWeight(medicalPhysical.getWeight());

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

    public List<MedicalPhysicalEntity> findByQid(String qid) {
        return medicalPhysicalManager.findByQid(qid);
    }

    @Mobile
    public List<MedicalMedicineEntity> findByIds(List<String> ids) {
        return medicalMedicineManager.findByIds(ids);
    }

    public List<MedicalMedicineEntity> findByMedicationRefId(String referenceId) {
        return medicalMedicineManager.findByMedicationRefId(referenceId);
    }

    @Mobile
    public JsonMedicalRecordList populateMedicalHistory(String qid) {
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        List<UserProfileEntity> dependentUserProfiles = userProfileManager.findMinorProfiles(qid);
        List<String> queueUserIds = new LinkedList<String>() {{
            add(qid);
        }};

        for (UserProfileEntity userProfile : dependentUserProfiles) {
            queueUserIds.add(userProfile.getQueueUserId());
        }

        for (String queueUserId : queueUserIds) {
            List<MedicalRecordEntity> medicalRecords = historicalRecords(queueUserId);
            for (MedicalRecordEntity medicalRecord : medicalRecords) {
                JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
                jsonMedicalRecord
                    .setBusinessType(medicalRecord.getBusinessType())
                    .setQueueUserId(medicalRecord.getQueueUserId())
                    .setChiefComplain(medicalRecord.getChiefComplain())
                    .setPastHistory(medicalRecord.getPastHistory())
                    .setFamilyHistory(medicalRecord.getFamilyHistory())
                    .setKnownAllergies(medicalRecord.getKnownAllergies())
                    .setClinicalFinding(medicalRecord.getClinicalFinding())
                    .setProvisionalDifferentialDiagnosis(medicalRecord.getProvisionalDifferentialDiagnosis())
                    .setDiagnosedById(userProfileManager.findByQueueUserId(medicalRecord.getDiagnosedById()).getName())
                    .setCreateDate(DateUtil.dateToString(medicalRecord.getCreated()))
                    .setBusinessName(medicalRecord.getBusinessName())
                    .setBizCategoryName(medicalRecord.getBizCategoryId() == null
                        ? "NA"
                        : MedicalDepartmentEnum.valueOf(medicalRecord.getBizCategoryId()).getDescription());

                if (null != medicalRecord.getMedicalPhysical()) {
                    jsonMedicalRecord.setMedicalPhysical(
                        new JsonMedicalPhysical()
                            .setBloodPressure(medicalRecord.getMedicalPhysical().getBloodPressure())
                            .setPluse(medicalRecord.getMedicalPhysical().getPluse())
                            .setWeight(medicalRecord.getMedicalPhysical().getWeight()));
                }

                if (null != medicalRecord.getMedicalMedication()) {
                    List<MedicalMedicineEntity> medicalMedicines = findByIds(medicalRecord.getMedicalMedication().getMedicineIds());
                    for (MedicalMedicineEntity medicalMedicine : medicalMedicines) {
                        jsonMedicalRecord.addMedicine(JsonMedicalMedicine.fromMedicalMedicine(medicalMedicine));
                    }
                }

                List<JsonRecordAccess> jsonRecordAccesses = new ArrayList<>();
                for (Long date : medicalRecord.getRecordAccessed().keySet()) {
                    String accessedBy = medicalRecord.getRecordAccessed().get(date);
                    JsonRecordAccess jsonRecordAccess = new JsonRecordAccess()
                        .setRecordAccessedDate(DateUtil.dateToString(new Date(date)))
                        .setRecordAccessedQid("#######");

                    jsonRecordAccesses.add(jsonRecordAccess);
                }
                jsonMedicalRecord.setRecordAccess(jsonRecordAccesses);
                jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
            }
        }

        return jsonMedicalRecordList;
    }
}
