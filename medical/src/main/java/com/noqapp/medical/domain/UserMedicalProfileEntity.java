package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.helper.NameDateHealth;
import com.noqapp.domain.json.JsonNameDateHealth;
import com.noqapp.domain.types.OccupationEnum;
import com.noqapp.domain.types.medical.BloodTypeEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 5/25/18 8:44 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "USER_MEDICAL_PROFILE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "user_medical_profile_idx", def = "{'QID' : 1}", unique = true),
})
public class UserMedicalProfileEntity extends BaseEntity {

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

    @Field("PG")
    private boolean pregnant;

    @Field("DA")
    private String dentalAnatomy;

    @Field("EB")
    private String editedByQID;

    @Field("ER")
    private List<NameDateHealth> externalMedicalReports;

    @SuppressWarnings("unused")
    private UserMedicalProfileEntity() {}

    public UserMedicalProfileEntity(@NotNull String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public UserMedicalProfileEntity setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public OccupationEnum getOccupation() {
        return occupation;
    }

    public UserMedicalProfileEntity setOccupation(OccupationEnum occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public UserMedicalProfileEntity setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
        return this;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public UserMedicalProfileEntity setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
        return this;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public UserMedicalProfileEntity setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
        return this;
    }

    public String getMedicineAllergies() {
        return medicineAllergies;
    }

    public UserMedicalProfileEntity setMedicineAllergies(String medicineAllergies) {
        this.medicineAllergies = medicineAllergies;
        return this;
    }

    public boolean isPregnant() {
        return pregnant;
    }

    public UserMedicalProfileEntity setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
        return this;
    }

    public String getDentalAnatomy() {
        return dentalAnatomy;
    }

    public UserMedicalProfileEntity setDentalAnatomy(String dentalAnatomy) {
        this.dentalAnatomy = dentalAnatomy;
        return this;
    }

    public String getEditedByQID() {
        return editedByQID;
    }

    public UserMedicalProfileEntity setEditedByQID(String editedByQID) {
        this.editedByQID = editedByQID;
        return this;
    }

    public List<NameDateHealth> getExternalMedicalReports() {
        return externalMedicalReports;
    }

    public UserMedicalProfileEntity setExternalMedicalReports(List<NameDateHealth> externalMedicalReports) {
        this.externalMedicalReports = externalMedicalReports;
        return this;
    }

    public UserMedicalProfileEntity addExternalMedicalReport(NameDateHealth externalMedicalReport) {
        if (externalMedicalReports == null) {
            externalMedicalReports = new LinkedList<>();
        }
        this.externalMedicalReports.add(externalMedicalReport);
        return this;
    }

    @Transient
    public List<JsonNameDateHealth> getExternalMedicalReportsAsJson() {
        List<JsonNameDateHealth> jsonNameDateHealths = new ArrayList<>();
        if (null != externalMedicalReports) {
            for (NameDateHealth nameDateHealth : externalMedicalReports) {
                JsonNameDateHealth jsonNameDateHealth = new JsonNameDateHealth()
                    .setHealthCareService(nameDateHealth.getHealthCareService());

                jsonNameDateHealth
                    .setName(nameDateHealth.getName())
                    .setMonthYear(nameDateHealth.getMonthYear());

                jsonNameDateHealths.add(jsonNameDateHealth);
            }
        }

        return jsonNameDateHealths;
    }
}
