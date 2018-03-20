package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

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
@Document(collection = "M_REC")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_rec_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalRecordEntity extends BaseEntity {

    @NotNull
    @Field ("BT")
    private BusinessTypeEnum businessType;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field ("CC")
    private String chiefComplain;

    @Field("PH")
    private String pastHistory;

    @Field("FH")
    private String familyHistory;

    @Field("KA")
    private String knownAllergies;

    @DBRef
    @Field("PE")
    private MedicalPhysicalEntity medicalPhysical;

    @Field("CF")
    private String clinicalFinding;

    @Field("DD")
    private String provisionalDifferentialDiagnosis;

    @DBRef
    @Field("LE")
    private MedicalLaboratoryEntity medicalLaboratory;

    @DBRef
    @Field("RE")
    private MedicalRadiologyEntity medicalRadiology;

    @DBRef
    @Field("ME")
    private MedicationEntity medication;

    @NotNull
    @Field("REI")
    private String recordEnteredUserId;

    @Field("RA")
    private Map<Date, String> recordAccessed;

    private MedicalRecordEntity() {}

    public MedicalRecordEntity(String queueUserId) {
        this.queueUserId = queueUserId;
        medicalPhysical = new MedicalPhysicalEntity(queueUserId);
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

    public String getChiefComplain() {
        return chiefComplain;
    }

    public MedicalRecordEntity setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
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

    public MedicalLaboratoryEntity getMedicalLaboratory() {
        return medicalLaboratory;
    }

    public MedicalRecordEntity setMedicalLaboratory(MedicalLaboratoryEntity medicalLaboratory) {
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

    public MedicationEntity getMedication() {
        return medication;
    }

    public MedicalRecordEntity setMedication(MedicationEntity medication) {
        this.medication = medication;
        return this;
    }

    public String getRecordEnteredUserId() {
        return recordEnteredUserId;
    }

    public MedicalRecordEntity setRecordEnteredUserId(String recordEnteredUserId) {
        this.recordEnteredUserId = recordEnteredUserId;
        return this;
    }

    public Map<Date, String> getRecordAccessed() {
        return recordAccessed;
    }

    public MedicalRecordEntity setRecordAccessed(Map<Date, String> recordAccessed) {
        this.recordAccessed = recordAccessed;
        return this;
    }
}
