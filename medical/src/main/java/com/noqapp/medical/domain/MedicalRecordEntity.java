package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.medical.FormVersionEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
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
})
public class MedicalRecordEntity extends BaseEntity {

    @NotNull
    @Field ("BT")
    private BusinessTypeEnum businessType;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("PH")
    private String pastHistory;

    @Field("FH")
    private String familyHistory;

    @Field("KA")
    private String knownAllergies;

    @DBRef
    @Field("PY")
    private MedicalPhysicalEntity medicalPhysical;

    @Field("CC")
    private String chiefComplain;

    @Field("XM")
    private String examination;

    @Field("CF")
    private String clinicalFinding;

    @Field("DD")
    private String provisionalDifferentialDiagnosis;

    @DBRef
    @Field("MP")
    private MedicalPathologyEntity medicalLaboratory;

    @DBRef
    @Field("RE")
    private MedicalRadiologyEntity medicalRadiology;

    @DBRef
    @Field("ME")
    private MedicalMedicationEntity medicalMedication;

    @Field("DI")
    private String diagnosis;

    @Field("PP")
    private String planToPatient;

    @Field("FP")
    private String followUpInDays;

    /* Always doctors id who looked or was booked for. */
    @NotNull
    @Field("DBI")
    private String diagnosedById;

    @Field("RA")
    private Map<Long, String> recordAccessed = new HashMap<>();

    @Field ("N")
    private String businessName;

    @Field ("BCI")
    private String bizCategoryId;

    @Field ("FV")
    private FormVersionEnum formVersion;

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

    public String getPastHistory() {
        return pastHistory;
    }

    public MedicalRecordEntity setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public MedicalRecordEntity setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public MedicalRecordEntity setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public MedicalPhysicalEntity getMedicalPhysical() {
        return medicalPhysical;
    }

    public MedicalRecordEntity setMedicalPhysical(MedicalPhysicalEntity medicalPhysical) {
        this.medicalPhysical = medicalPhysical;
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

    public MedicalPathologyEntity getMedicalLaboratory() {
        return medicalLaboratory;
    }

    public MedicalRecordEntity setMedicalLaboratory(MedicalPathologyEntity medicalLaboratory) {
        this.medicalLaboratory = medicalLaboratory;
        return this;
    }

    public MedicalRadiologyEntity getMedicalRadiology() {
        return medicalRadiology;
    }

    public MedicalRecordEntity setMedicalRadiology(MedicalRadiologyEntity medicalRadiology) {
        this.medicalRadiology = medicalRadiology;
        return this;
    }

    public MedicalMedicationEntity getMedicalMedication() {
        return medicalMedication;
    }

    public MedicalRecordEntity setMedicalMedication(MedicalMedicationEntity medicalMedication) {
        this.medicalMedication = medicalMedication;
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

    public String getFollowUpInDays() {
        return followUpInDays;
    }

    public MedicalRecordEntity setFollowUpInDays(String followUpInDays) {
        this.followUpInDays = followUpInDays;
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

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public MedicalRecordEntity setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
        return this;
    }

    public FormVersionEnum getFormVersion() {
        return formVersion;
    }

    public MedicalRecordEntity setFormVersion(FormVersionEnum formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    @Override
    public String toString() {
        return "MedicalRecordEntity{" +
                "businessType=" + businessType +
                ", queueUserId='" + queueUserId + '\'' +
                ", chiefComplain='" + chiefComplain + '\'' +
                ", pastHistory='" + pastHistory + '\'' +
                ", familyHistory='" + familyHistory + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", medicalPhysical=" + medicalPhysical +
                ", clinicalFinding='" + clinicalFinding + '\'' +
                ", provisionalDifferentialDiagnosis='" + provisionalDifferentialDiagnosis + '\'' +
                ", medicalLaboratory=" + medicalLaboratory +
                ", medicalRadiology=" + medicalRadiology +
                ", medicalMedication=" + medicalMedication +
                ", diagnosedById='" + diagnosedById + '\'' +
                ", recordAccessed=" + recordAccessed +
                ", businessName='" + businessName + '\'' +
                ", bizCategoryId='" + bizCategoryId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
