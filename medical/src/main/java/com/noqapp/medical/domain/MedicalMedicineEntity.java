package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2/25/18 3:36 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "M_MEDICINE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "m_medicine_idx", def = "{'QID' : 1}", unique = false),
})
public class MedicalMedicineEntity extends BaseEntity {

    /** Maps to MedicalMedicationEntity Id. */
    @NotNull
    @Field("MRI")
    private String medicalMedicationReferenceId;

    /** Maps to StoreProduct Id. */
    @NotNull
    @Field("PI")
    private String pharmacyReferenceId;

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("NA")
    private String name;

    @Field("ST")
    private String strength;

    @Field("DF")
    private String dailyFrequency;

    @Field("CO")
    private String course;

    @Field("MF")
    private String medicationWithFood;

    @Field ("MT")
    private String medicationType;

    public String getMedicalMedicationReferenceId() {
        return medicalMedicationReferenceId;
    }

    public MedicalMedicineEntity setMedicalMedicationReferenceId(String medicalMedicationReferenceId) {
        this.medicalMedicationReferenceId = medicalMedicationReferenceId;
        return this;
    }

    public String getPharmacyReferenceId() {
        return pharmacyReferenceId;
    }

    public MedicalMedicineEntity setPharmacyReferenceId(String pharmacyReferenceId) {
        this.pharmacyReferenceId = pharmacyReferenceId;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public MedicalMedicineEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalMedicineEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getStrength() {
        return strength;
    }

    public MedicalMedicineEntity setStrength(String strength) {
        this.strength = strength;
        return this;
    }

    public String getDailyFrequency() {
        return dailyFrequency;
    }

    public MedicalMedicineEntity setDailyFrequency(String dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
        return this;
    }

    public String getCourse() {
        return course;
    }

    public MedicalMedicineEntity setCourse(String course) {
        this.course = course;
        return this;
    }

    public String getMedicationWithFood() {
        return medicationWithFood;
    }

    public MedicalMedicineEntity setMedicationWithFood(String medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public String getMedicationType() {
        return medicationType;
    }

    public MedicalMedicineEntity setMedicationType(String medicationType) {
        this.medicationType = medicationType;
        return this;
    }
}
