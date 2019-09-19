package com.noqapp.medical.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.RegisteredDeviceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryModeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.domain.types.medical.MedicalRecordFieldFilterEnum;
import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.medical.domain.MedicalPathologyTestEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.domain.MedicalRadiologyTestEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.UserMedicalProfileEntity;
import com.noqapp.medical.domain.json.JsonMedicalMedicine;
import com.noqapp.medical.domain.json.JsonMedicalPathology;
import com.noqapp.medical.domain.json.JsonMedicalPathologyList;
import com.noqapp.medical.domain.json.JsonMedicalPhysical;
import com.noqapp.medical.domain.json.JsonMedicalProfile;
import com.noqapp.medical.domain.json.JsonMedicalRadiology;
import com.noqapp.medical.domain.json.JsonMedicalRadiologyList;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.domain.json.JsonMedicalRecordList;
import com.noqapp.medical.domain.json.JsonRecordAccess;
import com.noqapp.medical.exception.ExistingLabResultException;
import com.noqapp.medical.form.MedicalRecordForm;
import com.noqapp.medical.repository.MedicalMedicationManager;
import com.noqapp.medical.repository.MedicalMedicineManager;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalPathologyTestManager;
import com.noqapp.medical.repository.MedicalPhysicalManager;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.repository.MedicalRadiologyTestManager;
import com.noqapp.medical.repository.MedicalRecordManager;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.QueueManager;
import com.noqapp.repository.RegisteredDeviceManager;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PurchaseOrderService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * hitender
 * 3/16/18 1:17 PM
 */
@Service
public class MedicalRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalRecordService.class);

    private int limitRecords;

    private MedicalRecordManager medicalRecordManager;
    private MedicalPhysicalManager medicalPhysicalManager;
    private MedicalMedicationManager medicalMedicationManager;
    private MedicalMedicineManager medicalMedicineManager;
    private MedicalPathologyManager medicalPathologyManager;
    private MedicalPathologyTestManager medicalPathologyTestManager;
    private MedicalRadiologyManager medicalRadiologyManager;
    private MedicalRadiologyTestManager medicalRadiologyTestManager;
    private UserProfileManager userProfileManager;
    private BizStoreManager bizStoreManager;
    private QueueManager queueManager;
    private RegisteredDeviceManager registeredDeviceManager;
    private BusinessUserStoreService businessUserStoreService;
    private PurchaseOrderService purchaseOrderService;
    private UserMedicalProfileService userMedicalProfileService;

    private ExecutorService executorService;

    @Autowired
    public MedicalRecordService(
        @Value("${MedicalRecordService.limitRecords}")
        int limitRecords,

        MedicalRecordManager medicalRecordManager,
        MedicalPhysicalManager medicalPhysicalManager,
        MedicalMedicationManager medicalMedicationManager,
        MedicalMedicineManager medicalMedicineManager,
        MedicalPathologyManager medicalPathologyManager,
        MedicalPathologyTestManager medicalPathologyTestManager,
        MedicalRadiologyManager medicalRadiologyManager,
        MedicalRadiologyTestManager medicalRadiologyTestManager,
        UserProfileManager userProfileManager,
        BizStoreManager bizStoreManager,
        QueueManager queueManager,
        RegisteredDeviceManager registeredDeviceManager,
        BusinessUserStoreService businessUserStoreService,
        PurchaseOrderService purchaseOrderService,
        UserMedicalProfileService userMedicalProfileService
    ) {
        this.limitRecords = limitRecords;

        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalMedicationManager = medicalMedicationManager;
        this.medicalMedicineManager = medicalMedicineManager;
        this.medicalPathologyManager = medicalPathologyManager;
        this.medicalPathologyTestManager = medicalPathologyTestManager;
        this.medicalRadiologyManager = medicalRadiologyManager;
        this.medicalRadiologyTestManager = medicalRadiologyTestManager;
        this.userProfileManager = userProfileManager;
        this.bizStoreManager = bizStoreManager;
        this.queueManager = queueManager;
        this.registeredDeviceManager = registeredDeviceManager;
        this.businessUserStoreService = businessUserStoreService;
        this.purchaseOrderService = purchaseOrderService;
        this.userMedicalProfileService = userMedicalProfileService;

        this.executorService = newCachedThreadPool();
    }

    //TODO add check for qid
    public void addMedicalRecord(MedicalRecordForm medicalRecordForm, String diagnosedById, String codeQR) {
        LOG.info("Add medical record");
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);

        MedicalRecordEntity medicalRecord = new MedicalRecordEntity(medicalRecordForm.getQueueUserId());
        /* Setting its own ObjectId. */
        medicalRecord.setId(CommonUtil.generateHexFromObjectId());
        medicalRecord
            .setBusinessType(bizStore.getBusinessType())
            .setChiefComplain(StringUtils.capitalize(medicalRecordForm.getChiefComplain().trim()))
            .setExamination(StringUtils.capitalize(medicalRecordForm.getDiagnosis().trim()))
            .setClinicalFinding(StringUtils.capitalize(medicalRecordForm.getClinicalFinding().trim()))
            .setProvisionalDifferentialDiagnosis(StringUtils.capitalize(medicalRecordForm.getProvisionalDifferentialDiagnosis().trim()))
            .setDiagnosis(StringUtils.capitalize(medicalRecordForm.getDiagnosis().trim()))
            .setPlanToPatient(medicalRecord.getPlanToPatient())
            .setFollowUpDay(StringUtils.isNotBlank(medicalRecordForm.getFollowUpInDays())
                ? DateUtil.now().plusDays(Integer.valueOf(medicalRecordForm.getFollowUpInDays())).toDate()
                : null)
            .setNoteForPatient(medicalRecordForm.getNoteForPatient())
            .setNoteToDiagnoser(medicalRecordForm.getNoteToDiagnoser())
            .setDiagnosedById(diagnosedById)
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setBizNameId(bizStore.getBizName().getId())
            .setBizCategoryId(bizStore.getBizCategoryId())
            .setCodeQR(bizStore.getCodeQR())
            .setFormVersion(medicalRecordForm.getFormVersion());

        if (null != medicalRecordForm.getMedicalPhysicalHistoricals()) {
            populateWithMedicalPhysical(medicalRecordForm, medicalRecord);
        }

        if (null != medicalRecordForm.getMedicalMedicines()) {
            populateWithMedicalMedicine(medicalRecordForm, medicalRecord);
        }

        //TODO remove this temp code below for record access
        medicalRecord.addRecordAccessed(Instant.now().toEpochMilli(), diagnosedById);
        medicalRecordManager.save(medicalRecord);
        LOG.info("Saved medical record={}", medicalRecord);
    }

    @Mobile
    public void changePatient(JsonMedicalRecord jsonRecord, String diagnosedById) {
        UserProfileEntity diagnoserUserProfile = userProfileManager.findByQueueUserId(diagnosedById);
        MedicalRecordEntity medicalRecord = medicalRecordManager.findById(jsonRecord.getRecordReferenceId());

        /* Check if record can be updated. */
        checkIfMedicalRecordCanBeUpdated(medicalRecord);

        /* Check if patient has been changed. */
        changePatient(jsonRecord, diagnoserUserProfile, medicalRecord);

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(jsonRecord.getQueueUserId());
        /* Now subsequently update all related record. */
        String medicalPhysicalId = medicalRecord.getMedicalPhysicalId();
        if (StringUtils.isNotBlank(medicalPhysicalId)) {
            medicalPhysicalManager.changePatient(medicalPhysicalId, jsonRecord.getQueueUserId());
        }

        if (!medicalRecord.getMedicalRadiologies().isEmpty()) {
            for (String medicalRadiologyId : medicalRecord.getMedicalRadiologies()) {
                medicalRadiologyManager.changePatient(medicalRadiologyId, userProfile.getQueueUserId());
                medicalRadiologyTestManager.changePatient(medicalRadiologyId, userProfile.getQueueUserId());
            }
        }

        if (StringUtils.isNotBlank(medicalRecord.getMedicalLaboratoryId())) {
            medicalPathologyManager.changePatient(medicalRecord.getMedicalLaboratoryId(), userProfile.getQueueUserId());
            medicalPathologyTestManager.changePatient(medicalRecord.getMedicalLaboratoryId(), userProfile.getQueueUserId());
        }

        if (StringUtils.isNotBlank(medicalRecord.getMedicalMedicationId())) {
            medicalMedicationManager.changePatient(medicalRecord.getMedicalMedicationId(), userProfile.getQueueUserId());
            medicalMedicineManager.changePatient(medicalRecord.getMedicalMedicationId(), userProfile.getQueueUserId());
        }

        List<String> transactionIds = medicalRecord.getTransactionIds();
        if (null != transactionIds) {
            for (String transactionId : transactionIds) {
                purchaseOrderService.changePatient(transactionId, userProfile);
            }
        }

        medicalRecordManager.save(medicalRecord);
    }

    @Mobile
    public void addMedicalRecordWhenExternal(JsonMedicalRecord jsonRecord, String transactionId) {
        try {
            Assert.isNull(jsonRecord.getDiagnosedById(), "Cannot contain Diagnosed By Id");
            Assert.hasText(transactionId, "Transaction Id cannot be empty");

            /* Check if business type is of Hospital or Doctor to allow adding record. */
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(jsonRecord.getCodeQR());

            MedicalRecordEntity medicalRecord = medicalRecordManager.findById(jsonRecord.getRecordReferenceId());
            if (null == medicalRecord) {
                medicalRecord = new MedicalRecordEntity(jsonRecord.getQueueUserId());
                /* Setting its own ObjectId when not set. */
                medicalRecord.setId(StringUtils.isBlank(jsonRecord.getRecordReferenceId())
                    ? CommonUtil.generateHexFromObjectId()
                    : jsonRecord.getRecordReferenceId());

                medicalRecord.setBusinessName(bizStore.getBizName().getBusinessName());
                medicalRecord.setBusinessType(bizStore.getBusinessType());
                medicalRecord.setBizNameId(bizStore.getBizName().getId());
                medicalRecord.setCodeQR(bizStore.getCodeQR());
                medicalRecord.setFormVersion(jsonRecord.getFormVersion());
                medicalRecord.setBizCategoryId(bizStore.getBizCategoryId());
            }

            medicalRecordManager.save(medicalRecord);

            String recordId = null;
            if (null != jsonRecord.getMedicalPathologiesLists() && !jsonRecord.getMedicalPathologiesLists().isEmpty()) {
                populateWithPathologies(jsonRecord, medicalRecord);
                recordId = medicalRecord.getMedicalLaboratoryId();
            }

            if (null != jsonRecord.getMedicalRadiologyLists() && !jsonRecord.getMedicalRadiologyLists().isEmpty()) {
                for (JsonMedicalRadiologyList jsonMedicalRadiologyList : jsonRecord.getMedicalRadiologyLists()) {
                    jsonMedicalRadiologyList.setLabCategory(LabCategoryEnum.valueOf(bizStore.getBizCategoryId()));
                }
                populateWithMedicalRadiologies(jsonRecord, medicalRecord);
                recordId = medicalRecord.getMedicalRadiologies().get(0);
            }

            switch (LabCategoryEnum.valueOf(bizStore.getBizCategoryId())) {
                case PATH:
                    medicalPathologyManager.updateWithTransactionId(recordId, transactionId);
                    break;
                case SCAN:
                case XRAY:
                case SPEC:
                case MRI:
                case SONO:
                    medicalRadiologyManager.updateWithTransactionId(recordId, transactionId);
                    break;
                default:
                    break;
            }

            LOG.info("Saved medical record={}", medicalRecord);
        } catch (ExistingLabResultException e) {
            LOG.error("Failed to modify medical record reason={} {}", e.getLocalizedMessage(), jsonRecord, e);
            throw e;
        } catch (Exception e) {
            LOG.error("Failed to add medical record reason={} {}", e.getLocalizedMessage(), jsonRecord, e);
            throw e;
        }
    }

    @Mobile
    public void addMedicalRecord(JsonMedicalRecord jsonRecord, String diagnosedById) {
        try {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(diagnosedById);
            LOG.info("Add medical record {} {}", diagnosedById, userProfile.getLevel());

            /* Check if user has proper role to allow adding of medical record. */
            if (!businessUserStoreService.hasAccessWithUserLevel(diagnosedById, jsonRecord.getCodeQR(), userProfile.getLevel())) {
                LOG.info("Your are not authorized to add medical record mail={} {}", diagnosedById, jsonRecord.getCodeQR());
                return;
            }

            /* Check if business type is of Hospital or Doctor to allow adding record. */
            BizStoreEntity bizStore = bizStoreManager.findByCodeQR(jsonRecord.getCodeQR());
            if (bizStore.getBusinessType() != BusinessTypeEnum.DO && bizStore.getBizName().getBusinessType() != BusinessTypeEnum.DO) {
                LOG.error("Failed as its not a Doctor or Hospital business type, found store={} biz={}",
                    bizStore.getBusinessType(),
                    bizStore.getBizName().getBusinessType());
                return;
            }

            MedicalRecordEntity medicalRecord = medicalRecordManager.findById(jsonRecord.getRecordReferenceId());
            if (null == medicalRecord) {
                medicalRecord = new MedicalRecordEntity(jsonRecord.getQueueUserId());
                /* Setting its own ObjectId when not set. */
                medicalRecord.setId(StringUtils.isBlank(jsonRecord.getRecordReferenceId())
                    ? CommonUtil.generateHexFromObjectId()
                    : jsonRecord.getRecordReferenceId());
            } else {
                /* Check if record can be updated. */
                checkIfMedicalRecordCanBeUpdated(medicalRecord);

                List<String> transactionIds = medicalRecord.getTransactionIds();
                if (null != transactionIds) {
                    for (String transactionId : transactionIds) {
                        PurchaseOrderEntity purchaseOrder = purchaseOrderService.findByTransactionId(transactionId);
                        purchaseOrderService.cancelOrderByMerchant(purchaseOrder.getQueueUserId(), purchaseOrder.getTransactionId());
                    }

                    medicalRecord.setTransactionIds(new ArrayList<>());
                }
            }

            /* Updated User Medical Profile. */
            updateUserMedicalProfile(jsonRecord, diagnosedById);
            switch (userProfile.getLevel()) {
                case S_MANAGER:
                    medicalRecord
                        .setChiefComplain(
                            StringUtils.isBlank(jsonRecord.getChiefComplain())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getChiefComplain().trim()))
                        .setExamination(
                            StringUtils.isBlank(jsonRecord.getExamination())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getExamination().trim()))
                        .setClinicalFinding(
                            StringUtils.isBlank(jsonRecord.getClinicalFinding())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getClinicalFinding().trim()))
                        .setProvisionalDifferentialDiagnosis(
                            StringUtils.isBlank(jsonRecord.getProvisionalDifferentialDiagnosis())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getProvisionalDifferentialDiagnosis().trim()))
                        .setDiagnosis(
                            StringUtils.isBlank(jsonRecord.getDiagnosis())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getDiagnosis().trim()))
                        .setPlanToPatient(
                            StringUtils.isBlank(jsonRecord.getPlanToPatient())
                                ? null
                                : jsonRecord.getPlanToPatient())
                        .setNoteForPatient(jsonRecord.getNoteForPatient())
                        .setNoteToDiagnoser(jsonRecord.getNoteToDiagnoser())
                        .setDiagnosedById(diagnosedById)
                        .setFormVersion(jsonRecord.getFormVersion());

                    if (null == medicalRecord.getMedicalPhysicalId()) {
                        if (null != jsonRecord.getMedicalPhysical() && jsonRecord.getMedicalPhysical().isPhysicalFilled()) {
                            populateWithMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                        }
                    } else {
                        updateMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                    }
                    break;
                case Q_SUPERVISOR:
                    if (null == medicalRecord.getMedicalPhysicalId()) {
                        if (null != jsonRecord.getMedicalPhysical()) {
                            populateWithMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                        }
                    } else {
                        updateMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                    }
                    break;
            }
            medicalRecord
                .setBusinessType(bizStore.getBusinessType())
                .setFollowUpDay(StringUtils.isNotBlank(jsonRecord.getFollowUpInDays())
                    ? DateUtil.now().plusDays(Integer.valueOf(jsonRecord.getFollowUpInDays())).toDate()
                    : null)
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBizNameId(bizStore.getBizName().getId())
                .setBizCategoryId(bizStore.getBizCategoryId())
                .setCodeQR(bizStore.getCodeQR());


            //TODO remove this temp code below for record access
//            medicalRecord.addRecordAccessed(
//                Instant.now().toEpochMilli(),
//                medical.getDiagnosedById());
            medicalRecordManager.save(medicalRecord);

            /* Place medical order after saving medical record. There are version clash hence persist after saving medical record. */
            if (userProfile.getLevel() == UserLevelEnum.S_MANAGER) {
                if (null != jsonRecord.getMedicalMedicines()) {
                    populateWithMedicalMedicine(jsonRecord, medicalRecord);
                }

                if (null != jsonRecord.getMedicalPathologiesLists()) {
                    populateWithPathologies(jsonRecord, medicalRecord);
                }

                if (null != jsonRecord.getMedicalRadiologyLists()) {
                    populateWithMedicalRadiologies(jsonRecord, medicalRecord);
                }
            }
            LOG.info("Saved medical record={}", medicalRecord);
        } catch (ExistingLabResultException e) {
            LOG.error("Failed to modify medical record reason={} {}", e.getLocalizedMessage(), jsonRecord, e);
            throw e;
        } catch (Exception e) {
            LOG.error("Failed to add medical record reason={} {}", e.getLocalizedMessage(), jsonRecord, e);
            throw e;
        }
    }

    private void changePatient(JsonMedicalRecord jsonRecord, UserProfileEntity userProfile, MedicalRecordEntity medicalRecord) {
        if (!medicalRecord.getQueueUserId().equalsIgnoreCase(jsonRecord.getQueueUserId())) {
            LOG.warn("Change patient on medical record {} from {} to {}", medicalRecord.getId(), medicalRecord.getQueueUserId(), jsonRecord.getQueueUserId());
            if (UserLevelEnum.S_MANAGER == userProfile.getLevel()) {
                /* Update Queue User Id when user has been changed by doctor. Only doctors can change the queueUserId. */
                medicalRecord.setQueueUserId(jsonRecord.getQueueUserId());
            } else {
                LOG.error("Reached unsupported condition for changing patient in medical record={} by qid={}", medicalRecord.getId(), userProfile.getQueueUserId());
                throw new UnsupportedOperationException("Reached unsupported condition");
            }
        } else {
            LOG.error("Found same patient for medical record={} by qid={}", medicalRecord.getId(), userProfile.getQueueUserId());
            throw new UnsupportedOperationException("Reached unsupported condition");
        }
    }

    private void checkIfMedicalRecordCanBeUpdated(MedicalRecordEntity medicalRecord) {
        String medicalLaboratoryId = medicalRecord.getMedicalLaboratoryId();
        if (StringUtils.isNotBlank(medicalLaboratoryId)) {
            MedicalPathologyEntity medicalPathology = findPathologyById(medicalLaboratoryId);
            if (medicalPathology.getImages() != null && !medicalPathology.getImages().isEmpty()) {
                LOG.warn("Failed updating as medical pathology contains attachments and images");
                throw new ExistingLabResultException("Cannot modify as lab result exists");
            }
        }

        List<String> medicalRadiologies = medicalRecord.getMedicalRadiologies();
        for (String medicalRadiologyId : medicalRadiologies) {
            if (StringUtils.isNotBlank(medicalRadiologyId)) {
                MedicalRadiologyEntity medicalRadiology = findRadiologyById(medicalRadiologyId);
                if (medicalRadiology.getImages() != null && !medicalRadiology.getImages().isEmpty()) {
                    LOG.warn("Failed updating as medical radiology contains attachments and images");
                    throw new ExistingLabResultException("Cannot modify as lab result exists");
                }
            }
        }
    }

    private void updateUserMedicalProfile(JsonMedicalRecord jsonRecord, String diagnosedById) {
        if (jsonRecord.getJsonUserMedicalProfile().isDentalAnatomyDirty()) {
            UserMedicalProfileEntity userMedicalProfile = userMedicalProfileService.findOne(jsonRecord.getQueueUserId());
            if (null == userMedicalProfile) {
                userMedicalProfile = new UserMedicalProfileEntity(jsonRecord.getQueueUserId());
                userMedicalProfileService.save(userMedicalProfile);
            }

            userMedicalProfileService.updateDentalAnatomy(jsonRecord.getQueueUserId(), jsonRecord.getJsonUserMedicalProfile().getDentalAnatomy(), diagnosedById);
        }

        if (jsonRecord.getJsonUserMedicalProfile().isHistoryDirty()) {
            JsonUserMedicalProfile jsonUserMedicalProfile = jsonRecord.getJsonUserMedicalProfile();
            UserMedicalProfileEntity userMedicalProfile = userMedicalProfileService.findOne(jsonRecord.getQueueUserId());
            if (null == userMedicalProfile) {
                userMedicalProfile = new UserMedicalProfileEntity(jsonRecord.getQueueUserId());
            }

            userMedicalProfile
                .setBloodType(jsonUserMedicalProfile.getBloodType())
                .setOccupation(jsonUserMedicalProfile.getOccupation())
                .setPastHistory(StringUtils.isBlank(jsonUserMedicalProfile.getPastHistory())
                    ? null
                    : StringUtils.capitalize(jsonUserMedicalProfile.getPastHistory().trim()))
                .setFamilyHistory(StringUtils.isBlank(jsonUserMedicalProfile.getFamilyHistory())
                    ? null
                    : StringUtils.capitalize(jsonUserMedicalProfile.getFamilyHistory().trim()))
                .setKnownAllergies(StringUtils.isBlank(jsonUserMedicalProfile.getKnownAllergies())
                    ? null
                    : StringUtils.capitalize(jsonUserMedicalProfile.getKnownAllergies().trim()))
                .setMedicineAllergies(StringUtils.isBlank(jsonUserMedicalProfile.getMedicineAllergies())
                    ? null
                    : StringUtils.capitalize(jsonUserMedicalProfile.getMedicineAllergies().trim()))
                .setEditedByQID(diagnosedById);

            /* Sequence is important as this also backs up user medical profile to history. */
            userMedicalProfileService.save(userMedicalProfile);
        }
    }

    @Mobile
    public JsonMedicalRecord retrieveMedicalRecord(String codeQR, String recordReferenceId) {
        QueueEntity queue = queueManager.findOneByRecordReferenceId(codeQR, recordReferenceId);
        if (null == queue) {
            LOG.error("Not valid request for medical record codeQR={} recordReferenceId={}", codeQR, recordReferenceId);
            return null;
        }

        JsonMedicalRecord jsonMedicalRecord;
        MedicalRecordEntity medicalRecord = medicalRecordManager.findById(recordReferenceId);
        if (null == medicalRecord) {
            jsonMedicalRecord = new JsonMedicalRecord()
                .setJsonUserMedicalProfile(userMedicalProfileService.findOneAsJson(queue.getQueueUserId()))
                .setCodeQR(codeQR)
                .setRecordReferenceId(recordReferenceId);
        } else {
            jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
        }
        populateJsonMedicalRecordWithStoreDetails(codeQR, jsonMedicalRecord);
        return jsonMedicalRecord;
    }

    @Mobile
    public JsonMedicalRecordList retrieveMedicalRecord(String codeQR, MedicalRecordFieldFilterEnum medicalRecordFieldFilter, Date from, Date until, int currentPosition) {
        LOG.info("Search Work History {} {} {} {} {} ", codeQR, medicalRecordFieldFilter, from, until, currentPosition);
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        JsonMedicalRecord jsonMedicalRecord;
        List<MedicalRecordEntity> medicalRecords = medicalRecordManager.findByCodeQRFilteredOnFieldWithinDateRange(
            codeQR,
            medicalRecordFieldFilter.getName(),
            from,
            until,
            currentPosition);
        for (MedicalRecordEntity medicalRecord : medicalRecords) {
            jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(medicalRecord.getQueueUserId());
            jsonMedicalRecord
                .setPatientDisplayName(userProfile.getName())
                .setPatientPhoneRaw(userProfile.getPhoneRaw());
            jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
        }
        return jsonMedicalRecordList;
    }

    @Mobile
    public JsonMedicalRecord findMedicalRecord(String codeQR, String recordReferenceId) {
        QueueEntity queue = queueManager.findOneByRecordReferenceId(codeQR, recordReferenceId);
        if (null == queue) {
            LOG.error("Not valid request for medical record codeQR={} recordReferenceId={}", codeQR, recordReferenceId);
            return null;
        }

        MedicalRecordEntity medicalRecord = medicalRecordManager.findById(recordReferenceId);
        if (null == medicalRecord) {
            return null;
        } else {
            JsonMedicalRecord jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
            populateJsonMedicalRecordWithStoreDetails(codeQR, jsonMedicalRecord);
            return jsonMedicalRecord;
        }
    }

    private void populateJsonMedicalRecordWithStoreDetails(String codeQR, JsonMedicalRecord jsonMedicalRecord) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        jsonMedicalRecord
            /* setBusinessName is also populated from MedicalRecordEntity. */
            .setBusinessName(bizStore.getBizName().getBusinessName())
            .setAreaAndTown(bizStore.getAreaAndTown());
    }

    private void populateWithMedicalRadiologies(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        /* Delete Existing. */
        if (null != medicalRecord.getMedicalRadiologies() && !medicalRecord.getMedicalRadiologies().isEmpty()) {
            List<MedicalRadiologyEntity> medicalRadiologies = medicalRadiologyManager.findByIds(medicalRecord.getMedicalRadiologies());
            for (MedicalRadiologyEntity medicalRadiology : medicalRadiologies) {
                medicalRadiologyTestManager.deleteByRadiologyReferenceId(medicalRadiology.getId());
                medicalRadiologyManager.deleteHard(medicalRadiology);
            }
            medicalRecordManager.unsetMedicalRadiology(jsonMedicalRecord.getRecordReferenceId());
        }

        /* Exit when empty. */
        if (jsonMedicalRecord.getMedicalRadiologyLists().isEmpty()) {
            return;
        }

        /* Create new when not empty. */
        for (JsonMedicalRadiologyList jsonMedicalRadiologyList : jsonMedicalRecord.getMedicalRadiologyLists()) {
            MedicalRadiologyEntity medicalRadiology = new MedicalRadiologyEntity();
            medicalRadiology
                .setQueueUserId(jsonMedicalRecord.getQueueUserId())
                .setLabCategory(jsonMedicalRadiologyList.getLabCategory())
                .setId(CommonUtil.generateHexFromObjectId());

            for (JsonMedicalRadiology jsonMedicalRadiology : jsonMedicalRadiologyList.getJsonMedicalRadiologies()) {
                MedicalRadiologyTestEntity medicalRadiologyTest = new MedicalRadiologyTestEntity();
                medicalRadiologyTest
                    .setMedicalRadiologyReferenceId(medicalRadiology.getId())
                    .setQueueUserId(jsonMedicalRecord.getQueueUserId())
                    .setName(jsonMedicalRadiology.getName());
                medicalRadiologyTestManager.save(medicalRadiologyTest);
                medicalRadiology.addMedicalRadiologyXRayIds(medicalRadiologyTest.getId());
            }

            medicalRadiologyManager.save(medicalRadiology);
            medicalRecordManager.addMedicalRadiologiesId(jsonMedicalRecord.getRecordReferenceId(), medicalRadiology.getId());
            medicalRecord.addMedicalRadiology(medicalRadiology.getId());

            if (StringUtils.isNotBlank(jsonMedicalRadiologyList.getBizStoreId())) {
                if (bizStoreManager.exists(jsonMedicalRadiologyList.getBizStoreId())) {
                    executorService.submit(() -> createRadiologyOrder(jsonMedicalRecord, jsonMedicalRadiologyList, medicalRadiology.getId()));
                }
            }
        }
    }

    /** Creates order from radiology prescribed. */
    private void createRadiologyOrder(JsonMedicalRecord jsonMedicalRecord, JsonMedicalRadiologyList jsonMedicalRadiologyList, String radiologyId) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
        for (JsonMedicalRadiology jsonMedicalRadiology : jsonMedicalRadiologyList.getJsonMedicalRadiologies()) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                .setProductName(jsonMedicalRadiology.getName())
                .setProductPrice(0)
                .setProductDiscount(0)
                .setProductId(null)
                .setProductQuantity(jsonMedicalRadiology.getTimes());

            jsonPurchaseOrder.addJsonPurchaseOrderProduct(jsonPurchaseOrderProduct);
        }

        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, jsonMedicalRadiologyList.getBizStoreId(), radiologyId, jsonMedicalRadiologyList.getLabCategory());
    }

    private void populateWithPathologies(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        /* Delete Existing. */
        if (StringUtils.isNotBlank(medicalRecord.getMedicalLaboratoryId())) {
            medicalPathologyTestManager.deleteByPathologyReferenceId(medicalRecord.getMedicalLaboratoryId());
            medicalPathologyManager.deleteHard(medicalRecord.getMedicalLaboratoryId());
            medicalRecord.setMedicalLaboratoryId(null);
        }

        /* Exit when empty. */
        if (jsonMedicalRecord.getMedicalPathologiesLists().isEmpty()) {
            return;
        }

        /* Create new when not empty. */
        MedicalPathologyEntity medicalPathology = new MedicalPathologyEntity();
        medicalPathology
            .setQueueUserId(jsonMedicalRecord.getQueueUserId())
            .setId(CommonUtil.generateHexFromObjectId());

        for (JsonMedicalPathologyList jsonMedicalPathologyList : jsonMedicalRecord.getMedicalPathologiesLists()) {
            for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalPathologyList.getJsonMedicalPathologies()) {
                MedicalPathologyTestEntity medicalPathologyTest = new MedicalPathologyTestEntity()
                    .setMedicalPathologyReferenceId(medicalPathology.getId())
                    .setQueueUserId(medicalPathology.getQueueUserId())
                    .setName(jsonMedicalPathology.getName());
                medicalPathologyTestManager.save(medicalPathologyTest);
                medicalPathology.addMedicalPathologyTestId(medicalPathologyTest.getId());
            }
        }

        medicalPathologyManager.save(medicalPathology);
        medicalRecordManager.addMedicalLaboratoryId(jsonMedicalRecord.getRecordReferenceId(), medicalPathology.getId());
        medicalRecord.setMedicalLaboratoryId(medicalPathology.getId());

        if (StringUtils.isNotBlank(jsonMedicalRecord.getStoreIdPathology())) {
            if (bizStoreManager.exists(jsonMedicalRecord.getStoreIdPathology())) {
                executorService.submit(() -> createPathologyOrder(jsonMedicalRecord, medicalPathology.getId()));
            }
        }
    }

    /** Creates order from pathology lab test prescribed. */
    private void createPathologyOrder(JsonMedicalRecord jsonMedicalRecord, String pathologyId) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
        for (JsonMedicalPathologyList jsonMedicalPathologyList : jsonMedicalRecord.getMedicalPathologiesLists()) {
            for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalPathologyList.getJsonMedicalPathologies()) {
                JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                    .setProductName(jsonMedicalPathology.getName())
                    .setProductPrice(0)
                    .setProductDiscount(0)
                    .setProductId(null)
                    .setProductQuantity(jsonMedicalPathology.getTimes());

                jsonPurchaseOrder.addJsonPurchaseOrderProduct(jsonPurchaseOrderProduct);
            }
        }

        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, jsonMedicalRecord.getStoreIdPathology(), pathologyId, LabCategoryEnum.PATH);
    }

    private void populateWithMedicalMedicine(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        /* Delete Existing. */
        if (null != medicalRecord.getMedicalMedicationId()) {
            medicalMedicineManager.deleteByMedicationRefId(medicalRecord.getMedicalMedicationId());
            medicalMedicationManager.deleteHard(medicalRecord.getMedicalMedicationId());
            medicalRecord.setMedicalMedicationId(null);
        }

        /* Exit when empty. */
        if (jsonMedicalRecord.getMedicalMedicines().isEmpty()) {
            return;
        }

        /* Create new when not empty. */
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
                .setMedicationIntake(jsonMedicalMedicine.getMedicationIntake())
                .setMedicationType(jsonMedicalMedicine.getPharmacyCategory())
                .setMedicalMedicationReferenceId(medicalMedication.getId())
                .setPharmacyReferenceId("")             //TODO(hth) with pharmacy medicine id
                .setQueueUserId(jsonMedicalRecord.getQueueUserId())
                .setId(CommonUtil.generateHexFromObjectId());

            medicalMedicineManager.save(medicalMedicine);
            medicalMedication.addMedicineId(medicalMedicine.getId());
        }

        medicalMedicationManager.save(medicalMedication);
        medicalRecordManager.addMedicalMedicationId(jsonMedicalRecord.getRecordReferenceId(), medicalMedication.getId());

        if (StringUtils.isNotBlank(jsonMedicalRecord.getStoreIdPharmacy())) {
            if (bizStoreManager.exists(jsonMedicalRecord.getStoreIdPharmacy())) {
                executorService.submit(() -> createMedicineOrder(jsonMedicalRecord));
            }
        }
    }

    /** Creates order from medicine prescribed. */
    private void createMedicineOrder(JsonMedicalRecord jsonMedicalRecord) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
        for (JsonMedicalMedicine jsonMedicalMedicine : jsonMedicalRecord.getMedicalMedicines()) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                .setProductName(jsonMedicalMedicine.getName())
                .setProductPrice(0)
                .setProductDiscount(0)
                .setProductId(null)
                .setProductQuantity(jsonMedicalMedicine.getTimes());

            jsonPurchaseOrder.addJsonPurchaseOrderProduct(jsonPurchaseOrderProduct);
        }

        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, jsonMedicalRecord.getStoreIdPharmacy());
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
        medicalRecord.setMedicalMedicationId(medicalMedication.getId());
    }

    private void populateWithMedicalPhysical(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord, String qid) {
        try {
            LOG.info("Populate medical physical qid={}", jsonMedicalRecord.getQueueUserId());
            if (jsonMedicalRecord.getMedicalPhysical() != null) {
                MedicalPhysicalEntity medicalPhysical = new MedicalPhysicalEntity(jsonMedicalRecord.getQueueUserId());
                /* Setting its own ObjectId. */
                medicalPhysical.setId(CommonUtil.generateHexFromObjectId());
                updateMedicalPhysicalData(jsonMedicalRecord, medicalRecord, medicalPhysical, qid);
            }
            LOG.info("Populate medical physical complete medicalPhysicalId={}", medicalRecord.getMedicalPhysicalId());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    private void updateMedicalPhysicalData(
        JsonMedicalRecord jsonMedicalRecord,
        MedicalRecordEntity medicalRecord,
        MedicalPhysicalEntity medicalPhysical,
        String diagnosedById
    ) {
        medicalPhysical
            .setTemperature(jsonMedicalRecord.getMedicalPhysical().getTemperature())
            .setBloodPressure(jsonMedicalRecord.getMedicalPhysical().getBloodPressure())
            .setPulse(jsonMedicalRecord.getMedicalPhysical().getPulse())
            .setOxygen(jsonMedicalRecord.getMedicalPhysical().getOxygen())
            .setRespiratory(jsonMedicalRecord.getMedicalPhysical().getRespiratory())
            .setWeight(jsonMedicalRecord.getMedicalPhysical().getWeight())
            .setHeight(jsonMedicalRecord.getMedicalPhysical().getHeight())
            .setDiagnosedById(diagnosedById);

        LOG.info("Before save of MedicalPhysical={}", medicalPhysical);
        medicalPhysicalManager.save(medicalPhysical);

        /* Add the Medical Physical to Medical Record. */
        medicalRecord.setMedicalPhysicalId(medicalPhysical.getId());
    }

    private void updateMedicalPhysical(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord, String diagnosedById) {
        try {
            LOG.info("Populate medical physical qid={}", jsonMedicalRecord.getQueueUserId());
            if (null != jsonMedicalRecord.getMedicalPhysical() && jsonMedicalRecord.getMedicalPhysical().isPhysicalFilled()) {
                MedicalPhysicalEntity medicalPhysical = medicalPhysicalManager.findOne(medicalRecord.getMedicalPhysicalId());
                updateMedicalPhysicalData(jsonMedicalRecord, medicalRecord, medicalPhysical, diagnosedById);
            } else {
                medicalPhysicalManager.deleteHard(medicalRecord.getMedicalPhysicalId());
                medicalRecord.setMedicalPhysicalId(null);
            }
            LOG.info("Populate medical physical complete medicalPhysicalId={}", medicalRecord.getMedicalPhysicalId());
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
                    .setTemperature(medicalPhysical.getTemperature())
                    .setBloodPressure(medicalPhysical.getBloodPressure())
                    .setPulse(medicalPhysical.getPulse())
                    .setOxygen(medicalPhysical.getOxygen())
                    .setRespiratory(medicalPhysical.getRespiratory())
                    .setWeight(medicalPhysical.getWeight())
                    .setHeight(medicalPhysical.getHeight());

                LOG.info("Before save of MedicalPhysical={}", medicalPhysical);
                medicalPhysicalManager.save(medicalPhysical);

                /* Add the Medical Physical to Medical Record. */
                medicalRecord.setMedicalPhysicalId(medicalPhysical.getId());
            }
            LOG.info("Populate medical physical complete medicalPhysicalId={}", medicalRecord.getMedicalPhysicalId());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    public List<MedicalRecordEntity> historicalRecords(String qid) {
        return medicalRecordManager.historicalRecords(qid, limitRecords);
    }

    public List<MedicalRecordEntity> historicalRecords(String qid, MedicalDepartmentEnum medicalDepartment) {
        return medicalRecordManager.historicalRecords(qid, medicalDepartment, limitRecords);
    }

    public List<MedicalPhysicalEntity> findByQid(String qid) {
        return medicalPhysicalManager.findByQid(qid);
    }

    public List<MedicalMedicineEntity> findMedicinesByIds(List<String> ids) {
        return medicalMedicineManager.findByIds(ids);
    }

    private List<MedicalRadiologyEntity> findRadiologiesById(List<String> ids) {
        return medicalRadiologyManager.findByIds(ids);
    }

    private MedicalPathologyEntity findPathologyById(String id) {
        return medicalPathologyManager.findById(id);
    }

    @Mobile
    public void updatePathologyObservation(String id, String observation) {
        medicalPathologyManager.updatePathologyObservation(id, observation);
    }

    private MedicalRadiologyEntity findRadiologyById(String id) {
        return medicalRadiologyManager.findById(id);
    }

    @Mobile
    public void updateRadiologyObservation(String id, String observation) {
        medicalRadiologyManager.updateRadiologyObservation(id, observation);
    }

    @Mobile
    public List<MedicalPathologyTestEntity> findPathologyTestByIds(String referenceId) {
        return medicalPathologyTestManager.findPathologyTestByIds(referenceId);
    }

    @Mobile
    public List<MedicalRadiologyTestEntity> findRadiologyTestByIds(String referenceId) {
        return medicalRadiologyTestManager.findRadiologyTestByIds(referenceId);
    }

    public MedicalMedicationEntity findByMedicationId(String id) {
        return medicalMedicationManager.findOneById(id);
    }

    public List<MedicalMedicineEntity> findByMedicationRefId(String referenceId) {
        return medicalMedicineManager.findByMedicationRefId(referenceId);
    }

    /** Populate data for client case histories. */
    @Mobile
    public JsonMedicalRecordList populateMedicalHistory(String qid) {
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        List<String> queueUserIds = getAllQIDsForGuardian(qid);
        for (String queueUserId : queueUserIds) {
            List<MedicalRecordEntity> medicalRecords = historicalRecords(queueUserId);
            for (MedicalRecordEntity medicalRecord : medicalRecords) {
                JsonMedicalRecord jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
                jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
            }
        }

        return jsonMedicalRecordList;
    }

    /** Populate case histories of patient for doctor. */
    @Mobile
    public JsonMedicalRecordList populateMedicalHistoryForDoctor(String qid) {
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        List<MedicalRecordEntity> medicalRecords = historicalRecords(qid);
        for (MedicalRecordEntity medicalRecord : medicalRecords) {
            JsonMedicalRecord jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
            jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
        }

        return jsonMedicalRecordList;
    }

    /** Populate case histories of patient for doctor associated with medical department. */
    @Mobile
    public JsonMedicalRecordList populateMedicalHistoryForDoctorByMedicalDepartment(String qid, MedicalDepartmentEnum medicalDepartment) {
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        List<MedicalRecordEntity> medicalRecords = historicalRecords(qid, medicalDepartment);
        for (MedicalRecordEntity medicalRecord : medicalRecords) {
            JsonMedicalRecord jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
            jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
        }

        return jsonMedicalRecordList;
    }

    /** Populate data for client physical histories and medical profile. */
    @Mobile
    public JsonMedicalProfile populateMedicalProfileAndPhysicalHistory(String qid) {
        JsonMedicalProfile jsonMedicalProfile = new JsonMedicalProfile();

        List<String> queueUserIds = getAllQIDsForGuardian(qid);
        for (String queueUserId : queueUserIds) {
            List<MedicalPhysicalEntity> medicalPhysicals = findByQid(queueUserId);
            for (MedicalPhysicalEntity medicalPhysical : medicalPhysicals) {
                jsonMedicalProfile.addJsonMedicalPhysical(JsonMedicalPhysical.populateJsonMedicalPhysical(medicalPhysical));
            }
        }

        jsonMedicalProfile.setJsonUserMedicalProfile(userMedicalProfileService.findOneAsJson(qid));
        return jsonMedicalProfile;
    }

    private List<String> getAllQIDsForGuardian(String qid) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        List<UserProfileEntity> dependentUserProfiles = userProfileManager.findDependentProfilesByPhone(userProfile.getPhone());
        List<String> queueUserIds = new LinkedList<String>() {{
            add(qid);
        }};

        for (UserProfileEntity userProfileOfDependent : dependentUserProfiles) {
            queueUserIds.add(userProfileOfDependent.getQueueUserId());
        }
        return queueUserIds;
    }

    private JsonMedicalRecord getJsonMedicalRecord(MedicalRecordEntity medicalRecord) {
        UserProfileEntity userProfile = null;
        if (StringUtils.isNotBlank(medicalRecord.getDiagnosedById())) {
            userProfile = userProfileManager.findByQueueUserId(medicalRecord.getDiagnosedById());
        }

        JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord()
            .setJsonUserMedicalProfile(userMedicalProfileService.findOneAsJson(medicalRecord.getQueueUserId()));

        jsonMedicalRecord
            .setRecordReferenceId(medicalRecord.getId())
            .setFormVersion(medicalRecord.getFormVersion())
            .setBusinessType(medicalRecord.getBusinessType())
            .setCodeQR(medicalRecord.getCodeQR())
            .setQueueUserId(medicalRecord.getQueueUserId())
            .setChiefComplain(medicalRecord.getChiefComplain())
            .setExamination(medicalRecord.getExamination())
            .setClinicalFinding(medicalRecord.getClinicalFinding())
            .setProvisionalDifferentialDiagnosis(medicalRecord.getProvisionalDifferentialDiagnosis())
            .setDiagnosis(medicalRecord.getDiagnosis())
            .setPlanToPatient(medicalRecord.getPlanToPatient())
            .setFollowUpInDays(null == medicalRecord.getFollowUpDay() ? null : String.valueOf(DateUtil.getDaysBetween(medicalRecord.getCreated(), medicalRecord.getFollowUpDay())))
            .setNoteForPatient(medicalRecord.getNoteForPatient())
            .setNoteToDiagnoser(medicalRecord.getNoteToDiagnoser())
            .setDiagnosedById(medicalRecord.getDiagnosedById())
            .setDiagnosedByDisplayName(userProfile == null ? "" : userProfile.getName())
            .setCreateDate(DateUtil.dateToISO_8601(medicalRecord.getCreated()))
            .setBusinessName(medicalRecord.getBusinessName())
            .setImages(medicalRecord.getImages());

        switch (medicalRecord.getBusinessType()) {
            case HS:
                jsonMedicalRecord.setBizCategoryName(medicalRecord.getBizCategoryId() == null ? "NA" : LabCategoryEnum.valueOf(medicalRecord.getBizCategoryId()).getDescription());
                break;
            case DO:
                jsonMedicalRecord.setBizCategoryName(medicalRecord.getBizCategoryId() == null ? "NA" : MedicalDepartmentEnum.valueOf(medicalRecord.getBizCategoryId()).getDescription());
                break;
            default:
                LOG.error("Error supporting in-correct businessType={}", medicalRecord.getBusinessType());
        }

        if (null != medicalRecord.getMedicalPhysicalId()) {
            MedicalPhysicalEntity medicalPhysical = medicalPhysicalManager.findOne(medicalRecord.getMedicalPhysicalId());
            if (null != medicalPhysical) {
                jsonMedicalRecord.setMedicalPhysical(JsonMedicalPhysical.populateJsonMedicalPhysical(medicalPhysical));
            }
        }

        if (null != medicalRecord.getMedicalMedicationId()) {
            List<MedicalMedicineEntity> medicalMedicines = findByMedicationRefId(medicalRecord.getMedicalMedicationId());
            if (null != medicalMedicines) {
                for (MedicalMedicineEntity medicalMedicine : medicalMedicines) {
                    jsonMedicalRecord.addMedicine(JsonMedicalMedicine.fromMedicalMedicine(medicalMedicine));
                }
            }
        }

        if (null != medicalRecord.getMedicalLaboratoryId()) {
            MedicalPathologyEntity medicalPathology = findPathologyById(medicalRecord.getMedicalLaboratoryId());
            if (null != medicalPathology) {
                JsonMedicalPathologyList jsonMedicalPathologyList = new JsonMedicalPathologyList()
                    .setRecordReferenceId(medicalPathology.getId())
                    .setImages(medicalPathology.getImages())
                    .setObservation(medicalPathology.getObservation());

                List<MedicalPathologyTestEntity> medicalPathologyTests = findPathologyTestByIds(medicalRecord.getMedicalLaboratoryId());
                for (MedicalPathologyTestEntity medicalPathologyTest : medicalPathologyTests) {
                    jsonMedicalPathologyList.addJsonMedicalPathologies(new JsonMedicalPathology().setName(medicalPathologyTest.getName()));
                }
                jsonMedicalRecord.addMedicalPathologiesLists(jsonMedicalPathologyList);
            }
        }

        if (!medicalRecord.getMedicalRadiologies().isEmpty()) {
            List<MedicalRadiologyEntity> medicalRadiologies = findRadiologiesById(medicalRecord.getMedicalRadiologies());
            if (null != medicalRadiologies) {
                for (MedicalRadiologyEntity medicalRadiology : medicalRadiologies) {
                    JsonMedicalRadiologyList jsonMedicalRadiologyList = new JsonMedicalRadiologyList()
                        .setRecordReferenceId(medicalRadiology.getId())
                        .setLabCategory(medicalRadiology.getLabCategory())
                        .setImages(medicalRadiology.getImages())
                        .setObservation(medicalRadiology.getObservation());

                    List<MedicalRadiologyTestEntity> medicalPathologyTests = findRadiologyTestByIds(medicalRadiology.getId());
                    for (MedicalRadiologyTestEntity medicalRadiologyTest : medicalPathologyTests) {
                        jsonMedicalRadiologyList.addJsonMedicalRadiologies(new JsonMedicalRadiology().setName(medicalRadiologyTest.getName()));
                    }
                    jsonMedicalRecord.addMedicalRadiologyLists(jsonMedicalRadiologyList);
                }
            }
        }

        List<JsonRecordAccess> jsonRecordAccesses = new ArrayList<>();
        for (Long date : medicalRecord.getRecordAccessed().keySet()) {
            String accessedBy = medicalRecord.getRecordAccessed().get(date);
            JsonRecordAccess jsonRecordAccess = new JsonRecordAccess()
                .setRecordAccessedDate(DateUtil.dateToISO_8601(new Date(date)))
                .setRecordAccessedQid("#######");

            jsonRecordAccesses.add(jsonRecordAccess);
        }
        jsonMedicalRecord.setRecordAccess(jsonRecordAccesses);
        return jsonMedicalRecord;
    }

    @Mobile
    public String findAllFollowUp(String codeQR) {
        List<JsonQueuedPerson> jsonQueuedPeople = new ArrayList<>();
        List<MedicalRecordEntity> medicalRecords = medicalRecordManager.findAllFollowUp(codeQR);
        for (MedicalRecordEntity medicalRecord : medicalRecords) {
            String qid = medicalRecord.getQueueUserId();
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
            JsonQueuedPerson jsonQueuedPerson = new JsonQueuedPerson()
                .setCustomerName(userProfile.getName())
                .setCustomerPhone(StringUtils.isNotBlank(userProfile.getGuardianPhone()) ? userProfile.getGuardianPhone() : userProfile.getPhone())
                .setCreated(medicalRecord.getFollowUpDay());

            jsonQueuedPeople.add(jsonQueuedPerson);
        }

        return new JsonQueuePersonList().setQueuedPeople(jsonQueuedPeople).asJson();
    }

    /** Puts in a purchase order. */
    private void placeOrder(
        JsonMedicalRecord jsonMedicalRecord,
        JsonPurchaseOrder jsonPurchaseOrder,
        String bizStoreId,
        String recordId,
        LabCategoryEnum labCategory
    ) {
        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, bizStoreId);
        switch (labCategory) {
            case PATH:
                medicalPathologyManager.updateWithTransactionId(recordId, jsonPurchaseOrder.getTransactionId());
                break;
            case SCAN:
            case XRAY:
            case SPEC:
            case MRI:
            case SONO:
                medicalRadiologyManager.updateWithTransactionId(recordId, jsonPurchaseOrder.getTransactionId());
                break;
            default:
                break;
        }
    }

    /** Puts in a purchase order. */
    private void placeOrder(JsonMedicalRecord jsonMedicalRecord, JsonPurchaseOrder jsonPurchaseOrder, String bizStoreId) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(jsonMedicalRecord.getQueueUserId());
        jsonPurchaseOrder
            .setCustomerName(userProfile.getName())
            .setDeliveryAddress(userProfile.getAddress())
            .setCustomerPhone(userProfile.getPhone())
            .setDeliveryMode(DeliveryModeEnum.TO)
            //.setPaymentMode(PaymentModeEnum.CA)
            .setBizStoreId(bizStoreId);

        RegisteredDeviceEntity registeredDevice;
        if (StringUtils.isNotBlank(userProfile.getGuardianPhone())) {
            UserProfileEntity guardianProfile = userProfileManager.findOneByPhone(userProfile.getGuardianPhone());
            registeredDevice = registeredDeviceManager.findRecentDevice(guardianProfile.getQueueUserId());
        } else {
            registeredDevice = registeredDeviceManager.findRecentDevice(userProfile.getQueueUserId());
        }

        purchaseOrderService.createOrder(
            jsonPurchaseOrder,
            jsonMedicalRecord.getQueueUserId(),
            null == registeredDevice ? null : registeredDevice.getDeviceId(),
            TokenServiceEnum.M);
        medicalRecordManager.addTransactionId(jsonMedicalRecord.getRecordReferenceId(), jsonPurchaseOrder.getTransactionId());
    }

    @Mobile
    public void deleteReminisceOfTransactionId(String transactionId) {
        Assert.hasLength(transactionId, "Transaction Id cannot be null");
        PurchaseOrderEntity purchaseOrder = purchaseOrderService.findByTransactionId(transactionId);
        try {
            MedicalRecordEntity medicalRecord = medicalRecordManager.findById(purchaseOrder.getId());
            /* Delete Existing. */
            if (StringUtils.isNotBlank(medicalRecord.getMedicalLaboratoryId())) {
                medicalPathologyTestManager.deleteByPathologyReferenceId(medicalRecord.getMedicalLaboratoryId());
                medicalPathologyManager.deleteHard(medicalRecord.getMedicalLaboratoryId());
                medicalRecord.setMedicalLaboratoryId(null);
            }

            /* Delete Existing. */
            if (null != medicalRecord.getMedicalRadiologies() && !medicalRecord.getMedicalRadiologies().isEmpty()) {
                List<MedicalRadiologyEntity> medicalRadiologies = medicalRadiologyManager.findByIds(medicalRecord.getMedicalRadiologies());
                for (MedicalRadiologyEntity medicalRadiology : medicalRadiologies) {
                    medicalRadiologyTestManager.deleteByRadiologyReferenceId(medicalRadiology.getId());
                    medicalRadiologyManager.deleteHard(medicalRadiology);
                }
                medicalRecord.setMedicalRadiologies(new ArrayList<>());
            }

            medicalRecordManager.deleteHard(medicalRecord);
            LOG.info("Removed reference to medicalRecord when purchaseOrder orderState={} transactionId={}", purchaseOrder.getPresentOrderState(), purchaseOrder.getTransactionId());
        } catch (Exception e) {
            if (null != purchaseOrder) {
                LOG.error("Failed reference to medicalRecord when purchaseOrder orderState={} transactionId={} reason={}",
                    purchaseOrder.getPresentOrderState(), purchaseOrder.getTransactionId(), e.getLocalizedMessage(), e);
            } else {
                LOG.error("Failed removing medical reference reason={}", e.getLocalizedMessage(), e);
            }
        }
    }
}
