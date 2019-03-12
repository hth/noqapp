package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.medical.FormVersionEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2/24/18 8:07 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_RECORD")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_record_idx", def = "{'QID' : 1}", unique = false),
        @CompoundIndex(name = "m_record_qr_idx", def = "{'QR' : 1}", unique = false),
})
public class MedicalRecordEntity extends BaseEntity {

    @NotNull
    @Field ("BT")
    private BusinessTypeEnum businessType;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PI")
    private String medicalPhysicalId;

    @Field("CC")
    private String chiefComplain;

    @Field("XM")
    private String examination;

    @Field("CF")
    private String clinicalFinding;

    @Field("DD")
    private String provisionalDifferentialDiagnosis;

    @Field("LI")
    private String medicalLaboratoryId;

    @Field("RI")
    private List<String> medicalRadiologies = new LinkedList<>();

    @Field("MI")
    private String medicalMedicationId;

    @Field("DI")
    private String diagnosis;

    @Field("PP")
    private String planToPatient;

    @Field("FP")
    private Date followUpDay;

    @Field("NF")
    private boolean notifiedFollowUp = false;

    @Field("NP")
    private String noteForPatient;

    @Field("ND")
    private String noteToDiagnoser;

    /* Always doctors id who looked or was booked for. */
    @NotNull
    @Field("DBI")
    private String diagnosedById;

    @Field("RA")
    private Map<Long, String> recordAccessed = new HashMap<>();

    @Field ("N")
    private String businessName;

    @Field ("BN")
    private String bizNameId;

    @Field ("BCI")
    private String bizCategoryId;

    @Field("QR")
    private String codeQR;

    @Field ("FV")
    private FormVersionEnum formVersion;

    @Field("TIS")
    private List<String> transactionIds = new ArrayList<>();

    @Field("IM")
    private List<String> images;

    @SuppressWarnings("unused")
    private MedicalRecordEntity() {}

    public MedicalRecordEntity(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MedicalRecordEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalRecordEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getMedicalPhysicalId() {
        return medicalPhysicalId;
    }

    public MedicalRecordEntity setMedicalPhysicalId(String medicalPhysicalId) {
        this.medicalPhysicalId = medicalPhysicalId;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public MedicalRecordEntity setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public String getExamination() {
        return examination;
    }

    public MedicalRecordEntity setExamination(String examination) {
        this.examination = examination;
        return this;
    }

    public String getClinicalFinding() {
        return clinicalFinding;
    }

    public MedicalRecordEntity setClinicalFinding(String clinicalFinding) {
        this.clinicalFinding = clinicalFinding;
        return this;
    }

    public String getProvisionalDifferentialDiagnosis() {
        return provisionalDifferentialDiagnosis;
    }

    public MedicalRecordEntity setProvisionalDifferentialDiagnosis(String provisionalDifferentialDiagnosis) {
        this.provisionalDifferentialDiagnosis = provisionalDifferentialDiagnosis;
        return this;
    }

    public String getMedicalLaboratoryId() {
        return medicalLaboratoryId;
    }

    public MedicalRecordEntity setMedicalLaboratoryId(String medicalLaboratoryId) {
        this.medicalLaboratoryId = medicalLaboratoryId;
        return this;
    }

    public List<String> getMedicalRadiologies() {
        return medicalRadiologies;
    }

    public MedicalRecordEntity setMedicalRadiologies(List<String> medicalRadiologies) {
        this.medicalRadiologies = medicalRadiologies;
        return this;
    }

    public MedicalRecordEntity addMedicalRadiology(String medicalRadiologyId) {
        this.medicalRadiologies.add(medicalRadiologyId);
        return this;
    }

    public String getMedicalMedicationId() {
        return medicalMedicationId;
    }

    public MedicalRecordEntity setMedicalMedicationId(String medicalMedicationId) {
        this.medicalMedicationId = medicalMedicationId;
        return this;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public MedicalRecordEntity setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public String getPlanToPatient() {
        return planToPatient;
    }

    public MedicalRecordEntity setPlanToPatient(String planToPatient) {
        this.planToPatient = planToPatient;
        return this;
    }

    public Date getFollowUpDay() {
        return followUpDay;
    }

    public MedicalRecordEntity setFollowUpDay(Date followUpDay) {
        this.followUpDay = followUpDay;
        return this;
    }

    public boolean isNotifiedFollowUp() {
        return notifiedFollowUp;
    }

    public MedicalRecordEntity setNotifiedFollowUp(boolean notifiedFollowUp) {
        this.notifiedFollowUp = notifiedFollowUp;
        return this;
    }

    public String getNoteForPatient() {
        return noteForPatient;
    }

    public MedicalRecordEntity setNoteForPatient(String noteForPatient) {
        this.noteForPatient = noteForPatient;
        return this;
    }

    public String getNoteToDiagnoser() {
        return noteToDiagnoser;
    }

    public MedicalRecordEntity setNoteToDiagnoser(String noteToDiagnoser) {
        this.noteToDiagnoser = noteToDiagnoser;
        return this;
    }

    public String getDiagnosedById() {
        return diagnosedById;
    }

    public MedicalRecordEntity setDiagnosedById(String diagnosedById) {
        this.diagnosedById = diagnosedById;
        return this;
    }

    public Map<Long, String> getRecordAccessed() {
        return recordAccessed;
    }

    public MedicalRecordEntity setRecordAccessed(Map<Long, String> recordAccessed) {
        this.recordAccessed = recordAccessed;
        return this;
    }

    public MedicalRecordEntity addRecordAccessed(Long date, String queueUserId) {
        this.recordAccessed.put(date, queueUserId);
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public MedicalRecordEntity setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public MedicalRecordEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public MedicalRecordEntity setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public MedicalRecordEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public FormVersionEnum getFormVersion() {
        return formVersion;
    }

    public MedicalRecordEntity setFormVersion(FormVersionEnum formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    public List<String> getTransactionIds() {
        return transactionIds;
    }

    public MedicalRecordEntity setTransactionIds(List<String> transactionIds) {
        this.transactionIds = transactionIds;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public MedicalRecordEntity setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public MedicalRecordEntity addImage(String image) {
        if (images == null) {
            images = new LinkedList<>();
        }
        this.images.add(image);
        return this;
    }

    @Override
    public String toString() {
        return "MedicalRecordEntity{" +
            "businessType=" + businessType +
            ", queueUserId='" + queueUserId + '\'' +
            ", medicalPhysicalId='" + medicalPhysicalId + '\'' +
            ", chiefComplain='" + chiefComplain + '\'' +
            ", examination='" + examination + '\'' +
            ", clinicalFinding='" + clinicalFinding + '\'' +
            ", provisionalDifferentialDiagnosis='" + provisionalDifferentialDiagnosis + '\'' +
            ", medicalLaboratoryId='" + medicalLaboratoryId + '\'' +
            ", medicalRadiologies=" + medicalRadiologies +
            ", medicalMedicationId='" + medicalMedicationId + '\'' +
            ", diagnosis='" + diagnosis + '\'' +
            ", planToPatient='" + planToPatient + '\'' +
            ", followUpDay=" + followUpDay +
            ", notifiedFollowUp=" + notifiedFollowUp +
            ", noteForPatient='" + noteForPatient + '\'' +
            ", noteToDiagnoser='" + noteToDiagnoser + '\'' +
            ", diagnosedById='" + diagnosedById + '\'' +
            ", recordAccessed=" + recordAccessed +
            ", businessName='" + businessName + '\'' +
            ", bizCategoryId='" + bizCategoryId + '\'' +
            ", codeQR='" + codeQR + '\'' +
            ", formVersion=" + formVersion +
            ", id='" + id + '\'' +
            '}';
    }
}
