package com.noqapp.medical.service;

import static java.util.concurrent.Executors.newCachedThreadPool;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.json.JsonPurchaseOrderProduct;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.DeliveryTypeEnum;
import com.noqapp.domain.types.PaymentTypeEnum;
import com.noqapp.domain.types.TokenServiceEnum;
import com.noqapp.domain.types.UserLevelEnum;
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
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PurchaseOrderService;

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
import java.util.concurrent.ExecutorService;

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
    private MedicalPathologyManager medicalPathologyManager;
    private MedicalPathologyTestManager medicalPathologyTestManager;
    private MedicalRadiologyManager medicalRadiologyManager;
    private MedicalRadiologyTestManager medicalRadiologyTestManager;
    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private UserProfileManager userProfileManager;
    private PurchaseOrderService purchaseOrderService;

    private ExecutorService executorService;

    @Autowired
    public MedicalRecordService(
        MedicalRecordManager medicalRecordManager,
        MedicalPhysicalManager medicalPhysicalManager,
        MedicalMedicationManager medicalMedicationManager,
        MedicalMedicineManager medicalMedicineManager,
        MedicalPathologyManager medicalPathologyManager,
        MedicalPathologyTestManager medicalPathologyTestManager,
        MedicalRadiologyManager medicalRadiologyManager,
        MedicalRadiologyTestManager medicalRadiologyTestManager,
        BizService bizService,
        BusinessUserStoreService businessUserStoreService,
        UserProfileManager userProfileManager,
        PurchaseOrderService purchaseOrderService
    ) {
        this.medicalRecordManager = medicalRecordManager;
        this.medicalPhysicalManager = medicalPhysicalManager;
        this.medicalMedicationManager = medicalMedicationManager;
        this.medicalMedicineManager = medicalMedicineManager;
        this.medicalPathologyManager = medicalPathologyManager;
        this.medicalPathologyTestManager = medicalPathologyTestManager;
        this.medicalRadiologyManager = medicalRadiologyManager;
        this.medicalRadiologyTestManager = medicalRadiologyTestManager;
        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.userProfileManager = userProfileManager;
        this.purchaseOrderService = purchaseOrderService;

        this.executorService = newCachedThreadPool();
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
            .setProvisionalDifferentialDiagnosis(StringUtils.capitalize(medicalRecordForm.getProvisionalDifferentialDiagnosis().trim()))
            .setPlanToPatient(medicalRecord.getPlanToPatient())
            .setFollowUpInDays(medicalRecord.getFollowUpInDays())
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
        try {
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

            MedicalRecordEntity medicalRecord = medicalRecordManager.findById(jsonMedicalRecord.getRecordReferenceId());
            if (null == medicalRecord) {
                medicalRecord = new MedicalRecordEntity(jsonMedicalRecord.getQueueUserId());
                /* Setting its own ObjectId when not set. */
                medicalRecord.setId(StringUtils.isBlank(jsonMedicalRecord.getRecordReferenceId())
                    ? CommonUtil.generateHexFromObjectId()
                    : jsonMedicalRecord.getRecordReferenceId());
            }

            medicalRecord
                .setBusinessType(bizStore.getBusinessType())
                .setChiefComplain(
                    StringUtils.isBlank(jsonMedicalRecord.getChiefComplain())
                        ? null
                        : StringUtils.capitalize(jsonMedicalRecord.getChiefComplain().trim()))
                .setPastHistory(
                    StringUtils.isBlank(jsonMedicalRecord.getPastHistory())
                        ? null
                        : StringUtils.capitalize(jsonMedicalRecord.getPastHistory().trim()))
                .setFamilyHistory(
                    StringUtils.isBlank(jsonMedicalRecord.getFamilyHistory())
                        ? null
                        : StringUtils.capitalize(jsonMedicalRecord.getFamilyHistory().trim()))
                .setKnownAllergies(
                    StringUtils.isBlank(jsonMedicalRecord.getKnownAllergies())
                        ? null :
                        StringUtils.capitalize(jsonMedicalRecord.getKnownAllergies().trim()))
                .setClinicalFinding(
                    StringUtils.isBlank(jsonMedicalRecord.getClinicalFinding())
                        ? null
                        : StringUtils.capitalize(jsonMedicalRecord.getClinicalFinding().trim()))
                .setProvisionalDifferentialDiagnosis(
                    StringUtils.isBlank(jsonMedicalRecord.getProvisionalDifferentialDiagnosis())
                        ? null
                        : StringUtils.capitalize(jsonMedicalRecord.getProvisionalDifferentialDiagnosis().trim()))
                .setPlanToPatient(
                    StringUtils.isBlank(jsonMedicalRecord.getPlanToPatient())
                        ? null
                        : jsonMedicalRecord.getPlanToPatient())
                .setFollowUpInDays(jsonMedicalRecord.getFollowUpInDays())
                .setDiagnosedById(jsonMedicalRecord.getDiagnosedById())
                .setBusinessName(bizStore.getBizName().getBusinessName())
                .setBizCategoryId(bizStore.getBizCategoryId());

            if (null == medicalRecord.getMedicalPhysical()) {
                if (null != jsonMedicalRecord.getMedicalPhysical()) {
                    populateWithMedicalPhysical(jsonMedicalRecord, medicalRecord);
                }
            } else {
                updateMedicalPhysical(jsonMedicalRecord, medicalRecord);
            }

            if (null != jsonMedicalRecord.getMedicalMedicines()) {
                populateWithMedicalMedicine(jsonMedicalRecord, medicalRecord);
            }

            if (null != jsonMedicalRecord.getMedicalPathologies()) {
                populateWithPathologies(jsonMedicalRecord, medicalRecord);
            }

            if (null != jsonMedicalRecord.getMedicalRadiologies()) {
                populateWithMedicalRadiologies(jsonMedicalRecord, medicalRecord);
            }

            //TODO remove this temp code below for record access
//            medicalRecord.addRecordAccessed(
//                Instant.now().toEpochMilli(),
//                medical.getDiagnosedById());
            medicalRecordManager.save(medicalRecord);
            LOG.info("Saved medical record={}", medicalRecord);
        } catch (Exception e) {
            LOG.error("Failed to add medical record reason={} {}", e.getLocalizedMessage(), jsonMedicalRecord, e);
            throw e;
        }
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
                .setMedicationWithFood(jsonMedicalMedicine.getMedicationWithFood())
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

            jsonPurchaseOrder.addPurchaseOrderProduct(jsonPurchaseOrderProduct);
        }

        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(jsonMedicalRecord.getQueueUserId());
        jsonPurchaseOrder
            .setCustomerName(userProfile.getName())
            .setDeliveryAddress(userProfile.getAddress())
            .setCustomerPhone(userProfile.getPhone())
            .setDeliveryType(DeliveryTypeEnum.TO)
            .setPaymentType(PaymentTypeEnum.CA)
            .setBizStoreId(jsonMedicalRecord.getStoreIdPharmacy());

        purchaseOrderService.createOrder(
            jsonPurchaseOrder,
            jsonMedicalRecord.getQueueUserId(),
            null,
            TokenServiceEnum.M
        );
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
                updateMedicalPhysicalData(jsonMedicalRecord, medicalRecord, medicalPhysical);
            }
            LOG.info("Populate medical physical complete medicalPhysical={}", medicalRecord.getMedicalPhysical());
        } catch (Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }
    }

    private void updateMedicalPhysicalData(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord, MedicalPhysicalEntity medicalPhysical) {
        medicalPhysical
            .setTemperature(jsonMedicalRecord.getMedicalPhysical().getTemperature())
            .setBloodPressure(jsonMedicalRecord.getMedicalPhysical().getBloodPressure())
            .setPluse(jsonMedicalRecord.getMedicalPhysical().getPluse())
            .setOxygen(jsonMedicalRecord.getMedicalPhysical().getOxygen())
            .setWeight(jsonMedicalRecord.getMedicalPhysical().getWeight())
            .setDiagnosedById(StringUtils.isBlank(jsonMedicalRecord.getMedicalPhysical().getDiagnosedById())
                ? jsonMedicalRecord.getDiagnosedById()
                : jsonMedicalRecord.getMedicalPhysical().getDiagnosedById());

        LOG.info("Before save of MedicalPhysical={}", medicalPhysical);
        medicalPhysicalManager.save(medicalPhysical);

        /* Add the Medical Physical to Medical Record. */
        medicalRecord.setMedicalPhysical(medicalPhysical);
    }

    private void updateMedicalPhysical(JsonMedicalRecord jsonMedicalRecord, MedicalRecordEntity medicalRecord) {
        try {
            LOG.info("Populate medical physical qid={}", jsonMedicalRecord.getQueueUserId());

            if (jsonMedicalRecord.getMedicalPhysical() != null) {
                updateMedicalPhysicalData(jsonMedicalRecord, medicalRecord, medicalRecord.getMedicalPhysical());
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
                    .setPluse(medicalPhysical.getPluse())
                    .setOxygen(medicalPhysical.getOxygen())
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
                        : MedicalDepartmentEnum.valueOf(medicalRecord.getBizCategoryId()).getDescription())
                    .setPlanToPatient(medicalRecord.getPlanToPatient())
                    .setFollowUpInDays(medicalRecord.getFollowUpInDays());

                if (null != medicalRecord.getMedicalPhysical()) {
                    jsonMedicalRecord.setMedicalPhysical(
                        new JsonMedicalPhysical()
                            .setTemperature(medicalRecord.getMedicalPhysical().getTemperature())
                            .setBloodPressure(medicalRecord.getMedicalPhysical().getBloodPressure())
                            .setPluse(medicalRecord.getMedicalPhysical().getPluse())
                            .setOxygen(medicalRecord.getMedicalPhysical().getOxygen())
                            .setWeight(medicalRecord.getMedicalPhysical().getWeight()));
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
                jsonMedicalRecordList.addJsonMedicalRecords(jsonMedicalRecord);
            }
        }

        return jsonMedicalRecordList;
    }
}
