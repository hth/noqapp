package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.helper.NameDatePair;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 5/30/18 10:26 AM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document(collection = "HEALTH_CARE_PROFILE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "health_care_profile_idx", def = "{'QID' : 1}", unique = true),
})
public class HealthCareProfileEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @NotNull
    @Field ("QR")
    private String codeQR;

    @Field("PS")
    private Date practiceStart;

    /* Required to mark as a valid profile. */
    @Field("ED")
    private List<NameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @Field("LI")
    private List<NameDatePair> licenses = new LinkedList<>();

    @Field("AW")
    private List<NameDatePair> awards = new LinkedList<>();

    @Field("PD")
    private String prescriptionDictionary;

    public HealthCareProfileEntity(@NotNull String queueUserId, @NotNull String codeQR) {
        this.queueUserId = queueUserId;
        this.codeQR = codeQR;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public HealthCareProfileEntity setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<NameDatePair> getEducation() {
        return education;
    }

    public HealthCareProfileEntity setEducation(List<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<NameDatePair> getLicenses() {
        return licenses;
    }

    public HealthCareProfileEntity setLicenses(List<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public HealthCareProfileEntity setAwards(List<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public String getPrescriptionDictionary() {
        return prescriptionDictionary;
    }

    public HealthCareProfileEntity setPrescriptionDictionary(String prescriptionDictionary) {
        this.prescriptionDictionary = prescriptionDictionary;
        return this;
    }
}
