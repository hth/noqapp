package com.noqapp.medical.form;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.medical.domain.MedicalMedicationEntity;
import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.domain.PhysicalEntity;
import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 3/5/18 1:35 AM
 */
public class MedicalRecordForm {

    private String name;
    private GenderEnum gender;
    private long age;
    private String guardianName;
    private String guardianPhone;

    private int token;
    private BusinessTypeEnum businessType;
    private String queueUserId;
    private String codeQR;

    private String chiefComplain;
    private String pastHistory;
    private String familyHistory;
    private String knownAllergies;
    private List<MedicalPhysicalForm> medicalPhysicalForms = new ArrayList<MedicalPhysicalForm>() {{add(new MedicalPhysicalForm()); add(new MedicalPhysicalForm()); add(new MedicalPhysicalForm());}};
    private String clinicalFinding;
    private String provisionalDifferentialDiagnosis;
    private MedicalPathologyEntity medicalLaboratory = new MedicalPathologyEntity();
    private MedicalRadiologyEntity medicalRadiology = new MedicalRadiologyEntity();
    private MedicalMedicationEntity medication = new MedicalMedicationEntity();
    private Map<Date, String> recordAccessed = new HashMap<>();

    @SuppressWarnings("unused")
    private MedicalRecordForm() {
        //Happy Bean
    }

    public MedicalRecordForm(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public MedicalRecordForm populateEmptyForm(List<PhysicalEntity> physicals) {
        medicalPhysicalForms = new ArrayList<MedicalPhysicalForm>() {{
            for (PhysicalEntity physical : physicals) {
                add(new MedicalPhysicalForm()
                        .setPhysicalReferenceId(physical.getId())
                        .setName(physical.getName()));
            }
        }};
        return this;
    }

    public MedicalRecordForm populateHistoricalForm(List<MedicalPhysicalExaminationEntity> medicalPhysicalExaminations) {
        medicalPhysicalForms = new ArrayList<MedicalPhysicalForm>() {{
            for (MedicalPhysicalExaminationEntity physical : medicalPhysicalExaminations) {
                add(new MedicalPhysicalForm()
                        .setPhysicalReferenceId(physical.getId())
                        .setName(physical.getName())
                        .setValue(physical.getTestResult()));
            }
        }};
        return this;
    }

    public String getName() {
        return name;
    }

    public MedicalRecordForm setName(String name) {
        this.name = name;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public MedicalRecordForm setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public long getAge() {
        return age;
    }

    public MedicalRecordForm setAge(long age) {
        this.age = age;
        return this;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public MedicalRecordForm setGuardianName(String guardianName) {
        this.guardianName = guardianName;
        return this;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public MedicalRecordForm setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
        return this;
    }

    public int getToken() {
        return token;
    }

    public MedicalRecordForm setToken(int token) {
        this.token = token;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MedicalRecordForm setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    /* Note: Required for bean to populate value. */
    public MedicalRecordForm setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public MedicalRecordForm setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getChiefComplain() {
        return chiefComplain;
    }

    public MedicalRecordForm setChiefComplain(String chiefComplain) {
        this.chiefComplain = chiefComplain;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public MedicalRecordForm setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public MedicalRecordForm setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public MedicalRecordForm setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public List<MedicalPhysicalForm> getMedicalPhysicalForms() {
        return medicalPhysicalForms;
    }

    public MedicalRecordForm setMedicalPhysicalForms(List<MedicalPhysicalForm> medicalPhysicalForms) {
        this.medicalPhysicalForms = medicalPhysicalForms;
        return this;
    }

    public String getClinicalFinding() {
        return clinicalFinding;
    }

    public MedicalRecordForm setClinicalFinding(String clinicalFinding) {
        this.clinicalFinding = clinicalFinding;
        return this;
    }

    public String getProvisionalDifferentialDiagnosis() {
        return provisionalDifferentialDiagnosis;
    }

    public MedicalRecordForm setProvisionalDifferentialDiagnosis(String provisionalDifferentialDiagnosis) {
        this.provisionalDifferentialDiagnosis = provisionalDifferentialDiagnosis;
        return this;
    }

    public MedicalPathologyEntity getMedicalLaboratory() {
        return medicalLaboratory;
    }

    public MedicalRecordForm setMedicalLaboratory(MedicalPathologyEntity medicalLaboratory) {
        this.medicalLaboratory = medicalLaboratory;
        return this;
    }

    public MedicalRadiologyEntity getMedicalRadiology() {
        return medicalRadiology;
    }

    public MedicalRecordForm setMedicalRadiology(MedicalRadiologyEntity medicalRadiology) {
        this.medicalRadiology = medicalRadiology;
        return this;
    }

    public MedicalMedicationEntity getMedication() {
        return medication;
    }

    public MedicalRecordForm setMedication(MedicalMedicationEntity medication) {
        this.medication = medication;
        return this;
    }

    public Map<Date, String> getRecordAccessed() {
        return recordAccessed;
    }

    public MedicalRecordForm setRecordAccessed(Map<Date, String> recordAccessed) {
        this.recordAccessed = recordAccessed;
        return this;
    }

    @Transient
    public String getGuardianPhoneFormatted() {
        if (StringUtils.isBlank(guardianPhone)) {
            return "";
        }

        return Formatter.phoneNationalFormat(guardianPhone, Formatter.getCountryShortNameFromInternationalPhone(guardianPhone));
    }
}
