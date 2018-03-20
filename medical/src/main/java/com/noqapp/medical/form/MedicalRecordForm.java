package com.noqapp.medical.form;

import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.GenderEnum;
import com.noqapp.domain.types.PhysicalExamEnum;
import com.noqapp.medical.domain.MedicalLaboratoryEntity;
import com.noqapp.medical.domain.MedicalPhysicalExaminationEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.domain.MedicationEntity;
import org.apache.commons.lang3.StringUtils;

import java.beans.Transient;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Set<MedicalPhysicalForm> medicalPhysical;
    private String clinicalFinding;
    private String provisionalDifferentialDiagnosis;
    private MedicalLaboratoryEntity medicalLaboratory = new MedicalLaboratoryEntity();
    private MedicalRadiologyEntity medicalRadiology = new MedicalRadiologyEntity();
    private MedicationEntity medication = new MedicationEntity();
    private Map<Date, String> recordAccessed = new HashMap<>();

    @SuppressWarnings("unused")
    private MedicalRecordForm() {
        //Happy Bean

        medicalPhysical = new LinkedHashSet<MedicalPhysicalForm>() {{
            for (PhysicalExamEnum physicalExam : PhysicalExamEnum.values()) {
                add(new MedicalPhysicalForm().setPhysicalExam(physicalExam));
            }
        }};
    }

    public MedicalRecordForm(String queueUserId) {
        super();

        this.queueUserId = queueUserId;
        medicalPhysical = new LinkedHashSet<MedicalPhysicalForm>() {{
            for (PhysicalExamEnum physicalExam : PhysicalExamEnum.values()) {
                add(new MedicalPhysicalForm().setPhysicalExam(physicalExam));
            }
        }};
    }

    /**
     * Used when populating historical record.
     */
    public MedicalRecordForm(String queueUserId, List<MedicalPhysicalExaminationEntity> medicalPhysicalExaminations) {
        super();

        this.queueUserId = queueUserId;
        this.medicalPhysical = new LinkedHashSet<>();
        for (MedicalPhysicalExaminationEntity medicalPhysicalExamination : medicalPhysicalExaminations) {
            MedicalPhysicalForm medicalPhysicalForm = new MedicalPhysicalForm()
                    .setPhysicalExam(medicalPhysicalExamination.getPhysicalExam())
                    .setValue(medicalPhysicalExamination.getValue());

            medicalPhysical.add(medicalPhysicalForm);
        }
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

    public Set<MedicalPhysicalForm> getMedicalPhysical() {
        return medicalPhysical;
    }

    public MedicalRecordForm setMedicalPhysical(Set<MedicalPhysicalForm> medicalPhysical) {
        this.medicalPhysical = medicalPhysical;
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

    public MedicalLaboratoryEntity getMedicalLaboratory() {
        return medicalLaboratory;
    }

    public MedicalRecordForm setMedicalLaboratory(MedicalLaboratoryEntity medicalLaboratory) {
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

    public MedicationEntity getMedication() {
        return medication;
    }

    public MedicalRecordForm setMedication(MedicationEntity medication) {
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
