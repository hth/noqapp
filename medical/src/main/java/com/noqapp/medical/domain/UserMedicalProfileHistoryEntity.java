package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.types.OccupationEnum;
import com.noqapp.domain.types.medical.BloodTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2019-01-13 15:19
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "USER_MEDICAL_PROFILE_H")
@CompoundIndexes(value = {
    @CompoundIndex(name = "user_medical_profile_h_idx", def = "{'QID' : 1}", unique = false),
})
public class UserMedicalProfileHistoryEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @Field("BT")
    private BloodTypeEnum bloodType;

    @Field("OC")
    private OccupationEnum occupation;

    @Field("PH")
    private String pastHistory;

    @Field("FH")
    private String familyHistory;

    @Field("KA")
    private String knownAllergies;

    @Field("MA")
    private String medicineAllergies;

    @Field("DA")
    private String dentalAnatomy;

    @Field("EB")
    private String editedByQID;

    public String getQueueUserId() {
        return queueUserId;
    }

    public UserMedicalProfileHistoryEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public UserMedicalProfileHistoryEntity setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public OccupationEnum getOccupation() {
        return occupation;
    }

    public UserMedicalProfileHistoryEntity setOccupation(OccupationEnum occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public UserMedicalProfileHistoryEntity setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public UserMedicalProfileHistoryEntity setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public UserMedicalProfileHistoryEntity setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getMedicineAllergies() {
        return medicineAllergies;
    }

    public UserMedicalProfileHistoryEntity setMedicineAllergies(String medicineAllergies) {
        this.medicineAllergies = medicineAllergies;
        return this;
    }

    public String getDentalAnatomy() {
        return dentalAnatomy;
    }

    public UserMedicalProfileHistoryEntity setDentalAnatomy(String dentalAnatomy) {
        this.dentalAnatomy = dentalAnatomy;
        return this;
    }

    public String getEditedByQID() {
        return editedByQID;
    }

    public UserMedicalProfileHistoryEntity setEditedByQID(String editedByQID) {
        this.editedByQID = editedByQID;
        return this;
    }
}
