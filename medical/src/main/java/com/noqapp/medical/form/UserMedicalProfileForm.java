package com.noqapp.medical.form;

import com.noqapp.domain.types.OccupationEnum;
import com.noqapp.domain.types.medical.BloodTypeEnum;

/**
 * hitender
 * 2019-01-13 17:17
 */
public class UserMedicalProfileForm {

    private BloodTypeEnum bloodType;
    private OccupationEnum occupation;
    private String pastHistory;
    private String familyHistory;
    private String knownAllergies;
    private String medicineAllergies;

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public UserMedicalProfileForm setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public OccupationEnum getOccupation() {
        return occupation;
    }

    public UserMedicalProfileForm setOccupation(OccupationEnum occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public UserMedicalProfileForm setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public UserMedicalProfileForm setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public UserMedicalProfileForm setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getMedicineAllergies() {
        return medicineAllergies;
    }

    public UserMedicalProfileForm setMedicineAllergies(String medicineAllergies) {
        this.medicineAllergies = medicineAllergies;
        return this;
    }
}
