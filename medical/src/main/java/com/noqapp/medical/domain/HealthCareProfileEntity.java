package com.noqapp.medical.domain;

import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.json.medical.JsonNameDatePair;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

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
    @Field ("WP")
    private String webProfileId;

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

    @Field ("MA")
    private Set<String> managerAtStoreCodeQRs = new HashSet<>();

    @Field("PD")
    private String prescriptionDictionary;

    public HealthCareProfileEntity(@NotNull String queueUserId, @NotNull String webProfileId) {
        this.queueUserId = queueUserId;
        this.webProfileId = webProfileId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getWebProfileId() {
        return webProfileId;
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

    public Set<String> getManagerAtStoreCodeQRs() {
        return managerAtStoreCodeQRs;
    }

    public HealthCareProfileEntity setManagerAtStoreCodeQRs(Set<String> managerAtStoreCodeQRs) {
        this.managerAtStoreCodeQRs = managerAtStoreCodeQRs;
        return this;
    }

    public HealthCareProfileEntity addManagerAtStoreCodeQR(String managerAtStoreCodeQR) {
        this.managerAtStoreCodeQRs.add(managerAtStoreCodeQR);
        return this;
    }

    public HealthCareProfileEntity removeManagerAtStoreCodeQR(String managerAtStoreCodeQR) {
        this.managerAtStoreCodeQRs.remove(managerAtStoreCodeQR);
        return this;
    }

    public String getPrescriptionDictionary() {
        return prescriptionDictionary;
    }

    public HealthCareProfileEntity setPrescriptionDictionary(String prescriptionDictionary) {
        this.prescriptionDictionary = prescriptionDictionary;
        return this;
    }

    @Transient
    public List<JsonNameDatePair> getEducationAsJson() {
        return getJsonNameDatePairs(education);
    }

    @Transient
    public List<JsonNameDatePair> getLicensesAsJson() {
        return getJsonNameDatePairs(licenses);
    }

    @Transient
    public List<JsonNameDatePair> getAwardsAsJson() {
        return getJsonNameDatePairs(awards);
    }

    private List<JsonNameDatePair> getJsonNameDatePairs(List<NameDatePair> nameDatePairs) {
        List<JsonNameDatePair> jsonNameDatePairs = new ArrayList<>();
        for(NameDatePair nameDatePair : nameDatePairs) {
            jsonNameDatePairs.add(new JsonNameDatePair()
                    .setName(nameDatePair.getName())
                    .setMonthYear(DateFormatUtils.format(nameDatePair.getMonthYear(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
        }

        return jsonNameDatePairs;
    }
}
