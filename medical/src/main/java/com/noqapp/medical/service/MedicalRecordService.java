package com.noqapp.medical.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;
import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.medical.domain.MedicalMedicineEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.medical.domain.MedicalPathologyTestEntity;
import com.noqapp.medical.domain.MedicalPhysicalEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.domain.MedicalRadiologyTestEntity;
import com.noqapp.medical.domain.MedicalRecordEntity;
import com.noqapp.medical.domain.json.JsonMedicalMedicine;
import com.noqapp.medical.domain.json.JsonMedicalPathology;
import com.noqapp.medical.domain.json.JsonMedicalPhysical;
import com.noqapp.medical.domain.json.JsonMedicalRadiology;
import com.noqapp.medical.domain.json.JsonMedicalRecord;
import com.noqapp.medical.domain.json.JsonMedicalRecordList;
import com.noqapp.medical.domain.json.JsonRecordAccess;
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
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PurchaseOrderService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private BusinessUserStoreService businessUserStoreService;
    private PurchaseOrderService purchaseOrderService;

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
        BusinessUserStoreService businessUserStoreService,
        PurchaseOrderService purchaseOrderService
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
        this.businessUserStoreService = businessUserStoreService;
        this.purchaseOrderService = purchaseOrderService;

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
            .setPastHistory(StringUtils.capitalize(medicalRecordForm.getPastHistory().trim()))
            .setFamilyHistory(StringUtils.capitalize(medicalRecordForm.getFamilyHistory().trim()))
            .setKnownAllergies(StringUtils.capitalize(medicalRecordForm.getKnownAllergies().trim()))
            .setChiefComplain(StringUtils.capitalize(medicalRecordForm.getChiefComplain().trim()))
            .setExamination(StringUtils.capitalize(medicalRecordForm.getDiagnosis().trim()))
            .setClinicalFinding(StringUtils.capitalize(medicalRecordForm.getClinicalFinding().trim()))
            .setProvisionalDifferentialDiagnosis(StringUtils.capitalize(medicalRecordForm.getProvisionalDifferentialDiagnosis().trim()))
            .setDiagnosis(StringUtils.capitalize(medicalRecordForm.getDiagnosis().trim()))
            .setPlanToPatient(medicalRecord.getPlanToPatient())
            .setFollowUpDay(StringUtils.isNotBlank(medicalRecordForm.getFollowUpInDays()) ? DateUtil.now().plusDays(Integer.valueOf(medicalRecordForm.getFollowUpInDays())).toDate() : null)
            .setNoteForPatient(medicalRecordForm.getNoteForPatient())
            .setNoteToDiagnoser(medicalRecordForm.getNoteToDiagnoser())
            .setDiagnosedById(diagnosedById)
            .setBusinessName(bizStore.getBizName().getBusinessName())
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
        medicalRecord.addRecordAccessed(
            Instant.now().toEpochMilli(),
            diagnosedById);
        medicalRecordManager.save(medicalRecord);
        LOG.info("Saved medical record={}", medicalRecord);
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
            }

            switch (userProfile.getLevel()) {
                case S_MANAGER:
                    medicalRecord
                        .setBusinessType(bizStore.getBusinessType())
                        .setPastHistory(
                            StringUtils.isBlank(jsonRecord.getPastHistory())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getPastHistory().trim()))
                        .setFamilyHistory(
                            StringUtils.isBlank(jsonRecord.getFamilyHistory())
                                ? null
                                : StringUtils.capitalize(jsonRecord.getFamilyHistory().trim()))
                        .setKnownAllergies(
                            StringUtils.isBlank(jsonRecord.getKnownAllergies())
                                ? null :
                                StringUtils.capitalize(jsonRecord.getKnownAllergies().trim()))
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

                    if (null == medicalRecord.getMedicalPhysical()) {
                        if (null != jsonRecord.getMedicalPhysical()) {
                            populateWithMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                        }
                    } else {
                        updateMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                    }

                    if (null != jsonRecord.getMedicalMedicines()) {
                        populateWithMedicalMedicine(jsonRecord, medicalRecord);
                    }

                    if (null != jsonRecord.getMedicalPathologies()) {
                        populateWithPathologies(jsonRecord, medicalRecord);
                    }

                    if (null != jsonRecord.getMedicalRadiologies()) {
                        populateWithMedicalRadiologies(jsonRecord, medicalRecord);
                    }
                    break;
                case Q_SUPERVISOR:
                    if (null == medicalRecord.getMedicalPhysical()) {
                        if (null != jsonRecord.getMedicalPhysical()) {
                            populateWithMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                        }
                    } else {
                        updateMedicalPhysical(jsonRecord, medicalRecord, diagnosedById);
                    }
                    break;
            }
            medicalRecord
                .setFollowUpDay(StringUtils.isNotBlank(jsonRecord.getFollowUpInDays()) ? DateUtil.now().plusDays(Integer.valueOf(jsonRecord.getFollowUpInDays())).toDate() : null)
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBizCategoryId(bizStore.getBizCategoryId())
                .setCodeQR(bizStore.getCodeQR());


            //TODO remove this temp code below for record access
//            medicalRecord.addRecordAccessed(
//                Instant.now().toEpochMilli(),
//                medical.getDiagnosedById());
            medicalRecordManager.save(medicalRecord);
            LOG.info("Saved medical record={}", medicalRecord);
        } catch (Exception e) {
            LOG.error("Failed to add medical record reason={} {}", e.getLocalizedMessage(), jsonRecord, e);
            throw e;
        }
    }

    @Mobile
    public JsonMedicalRecord retrieveMedicalRecord(String codeQR, String recordReferenceId) {
        QueueEntity queue = queueManager.findOneByRecordReferenceId(codeQR, recordReferenceId);
        if (queue == null) {
            LOG.error("Not valid request for medical record codeQR={} recordReferenceId={}", codeQR, recordReferenceId);
            return null;
        }

        MedicalRecordEntity medicalRecord = medicalRecordManager.findById(recordReferenceId);
        if (medicalRecord == null) {
            return new JsonMedicalRecord().setCodeQR(codeQR).setRecordReferenceId(recordReferenceId);
        }
        return getJsonMedicalRecord(medicalRecord);
    }

    private void populateWithMedicalRadiologies(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        if (jsonMedicalRecord.getMedicalRadiologies().isEmpty()) {
            return;
        }

        MedicalRadiologyEntity medicalRadiology = new MedicalRadiologyEntity();
        medicalRadiology
            .setQueueUserId(jsonMedicalRecord.getQueueUserId())
            .setId(CommonUtil.generateHexFromObjectId());

        for (JsonMedicalRadiology jsonMedicalRadiology : jsonMedicalRecord.getMedicalRadiologies()) {
            MedicalRadiologyTestEntity medicalRadiologyTest = new MedicalRadiologyTestEntity();
            medicalRadiologyTest.setName(jsonMedicalRadiology.getName());
            medicalRadiologyTest.setMedicalRadiologyReferenceId(medicalRadiology.getId());
            medicalRadiologyTestManager.save(medicalRadiologyTest);
            medicalRadiology.addMedicalRadiologyXRayIds(medicalRadiologyTest.getId());
        }

        medicalRadiologyManager.save(medicalRadiology);
        medicalRecord.setMedicalRadiology(medicalRadiology);

        executorService.submit(() -> createRadiologyOrder(jsonMedicalRecord));
    }

    /** Creates order from radiology prescribed. */
    private void createRadiologyOrder(JsonMedicalRecord jsonMedicalRecord) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
        for (JsonMedicalRadiology jsonMedicalRadiology : jsonMedicalRecord.getMedicalRadiologies()) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                .setProductName(jsonMedicalRadiology.getName())
                .setProductPrice(0)
                .setProductDiscount(0)
                .setProductId(null)
                .setProductQuantity(jsonMedicalRadiology.getTimes());

            jsonPurchaseOrder.addJsonPurchaseOrderProduct(jsonPurchaseOrderProduct);
        }

        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, jsonMedicalRecord.getStoreIdRadiology());
    }

    private void populateWithPathologies(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        if (jsonMedicalRecord.getMedicalPathologies().isEmpty()) {
            return;
        }

        MedicalPathologyEntity medicalPathology = new MedicalPathologyEntity();
        medicalPathology
            .setQueueUserId(jsonMedicalRecord.getQueueUserId())
            .setId(CommonUtil.generateHexFromObjectId());

        for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalRecord.getMedicalPathologies()) {
            MedicalPathologyTestEntity medicalPathologyTest = new MedicalPathologyTestEntity();
            medicalPathologyTest.setName(jsonMedicalPathology.getName());
            medicalPathologyTest.setMedicalPathologyReferenceId(medicalPathology.getId());
            medicalPathologyTestManager.save(medicalPathologyTest);
            medicalPathology.addMedicalPathologyTestId(medicalPathologyTest.getId());
        }

        medicalPathologyManager.save(medicalPathology);
        medicalRecord.setMedicalLaboratory(medicalPathology);

        executorService.submit(() -> createPathologyOrder(jsonMedicalRecord));
    }

    /** Creates order from pathology lab test prescribed. */
    private void createPathologyOrder(JsonMedicalRecord jsonMedicalRecord) {
        JsonPurchaseOrder jsonPurchaseOrder = new JsonPurchaseOrder();
        for (JsonMedicalPathology jsonMedicalPathology : jsonMedicalRecord.getMedicalPathologies()) {
            JsonPurchaseOrderProduct jsonPurchaseOrderProduct = new JsonPurchaseOrderProduct()
                .setProductName(jsonMedicalPathology.getName())
                .setProductPrice(0)
                .setProductDiscount(0)
                .setProductId(null)
                .setProductQuantity(jsonMedicalPathology.getTimes());

            jsonPurchaseOrder.addJsonPurchaseOrderProduct(jsonPurchaseOrderProduct);
        }

        placeOrder(jsonMedicalRecord, jsonPurchaseOrder, jsonMedicalRecord.getStoreIdPathology());
    }

    private void populateWithMedicalMedicine(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        if (jsonMedicalRecord.getMedicalMedicines().isEmpty()) {
            return;
        }

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
        medicalRecord.setMedicalMedication(medicalMedication);

        executorService.submit(() -> createMedicineOrder(jsonMedicalRecord));
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
        medicalRecord.setMedicalMedication(medicalMedication);
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
            LOG.info("Populate medical physical complete medicalPhysical={}", medicalRecord.getMedicalPhysical());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    private void updateMedicalPhysicalData(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord, MedicalPhysicalEntity medicalPhysical, String diagnosedById) {
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
        medicalRecord.setMedicalPhysical(medicalPhysical);
    }

    private void updateMedicalPhysical(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord, String diagnosedById) {
        try {
            LOG.info("Populate medical physical qid={}", jsonMedicalRecord.getQueueUserId());

            if (jsonMedicalRecord.getMedicalPhysical() != null) {
                updateMedicalPhysicalData(jsonMedicalRecord, medicalRecord, medicalRecord.getMedicalPhysical(), diagnosedById);
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
                medicalRecord.setMedicalPhysical(medicalPhysical);
            }
            LOG.info("Populate medical physical complete medicalPhysical={}", medicalRecord.getMedicalPhysical());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    public List<MedicalRecordEntity> historicalRecords(String qid) {
        return medicalRecordManager.historicalRecords(qid, limitRecords);
    }

    public List<MedicalPhysicalEntity> findByQid(String qid) {
        return medicalPhysicalManager.findByQid(qid);
    }

    public List<MedicalMedicineEntity> findByIds(List<String> ids) {
        return medicalMedicineManager.findByIds(ids);
    }

    @Mobile
    public List<MedicalPathologyTestEntity> findPathologyTestByIds(String referenceId) {
        return medicalPathologyTestManager.findPathologyTestByIds(referenceId);
    }

    @Mobile
    public List<MedicalRadiologyTestEntity> findRadiologyTestByIds(String referenceId) {
        return medicalRadiologyTestManager.findRadiologyTestByIds(referenceId);
    }

    public List<MedicalMedicineEntity> findByMedicationRefId(String referenceId) {
        return medicalMedicineManager.findByMedicationRefId(referenceId);
    }

    /** Populate data for client case histories.*/
    @Mobile
    public JsonMedicalRecordList populateMedicalHistory(String qid) {
        JsonMedicalRecordList jsonMedicalRecordList = new JsonMedicalRecordList();

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        List<UserProfileEntity> dependentUserProfiles = userProfileManager.findDependentProfilesByPhone(userProfile.getPhone());
        List<String> queueUserIds = new LinkedList<String>() {{
            add(qid);
        }};

        for (UserProfileEntity userProfileOfDependent : dependentUserProfiles) {
            queueUserIds.add(userProfileOfDependent.getQueueUserId());
        }

        for (String queueUserId : queueUserIds) {
            List<MedicalRecordEntity> medicalRecords = historicalRecords(queueUserId);
            for (MedicalRecordEntity medicalRecord : medicalRecords) {
                JsonMedicalRecord jsonMedicalRecord = getJsonMedicalRecord(medicalRecord);
                jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
            }
        }

        return jsonMedicalRecordList;
    }

    private JsonMedicalRecord getJsonMedicalRecord(MedicalRecordEntity medicalRecord) {
        UserProfileEntity userProfile = null;
        if (StringUtils.isNotBlank(medicalRecord.getDiagnosedById())) {
            userProfile = userProfileManager.findByQueueUserId(medicalRecord.getDiagnosedById());
        }
        JsonMedicalRecord jsonMedicalRecord = new JsonMedicalRecord();
        jsonMedicalRecord
            .setFormVersion(medicalRecord.getFormVersion())
            .setBusinessType(medicalRecord.getBusinessType())
            .setQueueUserId(medicalRecord.getQueueUserId())
            .setPastHistory(medicalRecord.getPastHistory())
            .setFamilyHistory(medicalRecord.getFamilyHistory())
            .setKnownAllergies(medicalRecord.getKnownAllergies())
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
            .setCreateDate(DateUtil.dateToString(medicalRecord.getCreated()))
            .setBusinessName(medicalRecord.getBusinessName())
            .setBizCategoryName(medicalRecord.getBizCategoryId() == null
                ? "NA"
                : MedicalDepartmentEnum.valueOf(medicalRecord.getBizCategoryId()).getDescription());

        if (null != medicalRecord.getMedicalPhysical()) {
            jsonMedicalRecord.setMedicalPhysical(
                new JsonMedicalPhysical()
                    .setTemperature(medicalRecord.getMedicalPhysical().getTemperature())
                    .setBloodPressure(medicalRecord.getMedicalPhysical().getBloodPressure())
                    .setPulse(medicalRecord.getMedicalPhysical().getPulse())
                    .setOxygen(medicalRecord.getMedicalPhysical().getOxygen())
                    .setRespiratory(medicalRecord.getMedicalPhysical().getRespiratory())
                    .setWeight(medicalRecord.getMedicalPhysical().getWeight())
                    .setHeight(medicalRecord.getMedicalPhysical().getHeight()));
        }

        if (null != medicalRecord.getMedicalMedication()) {
            List<MedicalMedicineEntity> medicalMedicines = findByMedicationRefId(medicalRecord.getMedicalMedication().getId());
            for (MedicalMedicineEntity medicalMedicine : medicalMedicines) {
                jsonMedicalRecord.addMedicine(JsonMedicalMedicine.fromMedicalMedicine(medicalMedicine));
            }
        }

        if (null != medicalRecord.getMedicalLaboratory()) {
            List<MedicalPathologyTestEntity> medicalPathologyTests = findPathologyTestByIds(medicalRecord.getMedicalLaboratory().getId());
            for (MedicalPathologyTestEntity medicalPathologyTest : medicalPathologyTests) {
                jsonMedicalRecord.addMedicalPathology(new JsonMedicalPathology().setName(medicalPathologyTest.getName()));
            }
        }

        if (null != medicalRecord.getMedicalRadiology()) {
            List<MedicalRadiologyTestEntity> medicalPathologyTests = findRadiologyTestByIds(medicalRecord.getMedicalRadiology().getId());
            for (MedicalRadiologyTestEntity medicalRadiologyTest : medicalPathologyTests) {
                jsonMedicalRecord.addMedicalRadiology(new JsonMedicalRadiology().setName(medicalRadiologyTest.getName()));
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

    /**
     * Puts in a purchase order.
     * @param jsonMedicalRecord
     * @param jsonPurchaseOrder
     */
    private void placeOrder(JsonMedicalRecord jsonMedicalRecord, JsonPurchaseOrder jsonPurchaseOrder, String bizStoreId) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(jsonMedicalRecord.getQueueUserId());
        jsonPurchaseOrder
            .setCustomerName(userProfile.getName())
            .setDeliveryAddress(userProfile.getAddress())
            .setCustomerPhone(userProfile.getPhone())
            .setDeliveryType(DeliveryTypeEnum.TO)
            .setPaymentType(PaymentTypeEnum.CA)
            .setBizStoreId(bizStoreId);

        purchaseOrderService.createOrder(
            jsonPurchaseOrder,
            jsonMedicalRecord.getQueueUserId(),
            null,
            TokenServiceEnum.M
        );
    }
}
