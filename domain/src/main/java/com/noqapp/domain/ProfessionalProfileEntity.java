package com.noqapp.domain;

import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.json.JsonNameDatePair;
import com.noqapp.domain.types.medical.FormVersionEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

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
@Document(collection = "PROFESSIONAL_PROFILE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "professional_profile_idx", def = "{'QID' : 1}", unique = true),
})
public class ProfessionalProfileEntity extends BaseEntity {

    @NotNull
    @Field("QID")
    private String queueUserId;

    @NotNull
    @Field ("WP")
    private String webProfileId;

    @Field("PS")
    private String practiceStart;

    @Field("AM")
    private String aboutMe;

    /* Required to mark as a valid profile. */
    @Field("ED")
    private Set<NameDatePair> education = new HashSet<>();

    /* Required to mark as a valid profile. */
    @Field("LI")
    private Set<NameDatePair> licenses = new HashSet<>();

    @Field("AW")
    private Set<NameDatePair> awards = new HashSet<>();

    @Field ("MA")
    private Set<String> managerAtStoreCodeQRs = new HashSet<>();

    @Field("DD")
    private String dataDictionary;

    @Field ("FV")
    private FormVersionEnum formVersion = FormVersionEnum.MFD1;

    @SuppressWarnings("unused")
    public ProfessionalProfileEntity() {
        //Default constructor, required to keep bean happy
    }

    public ProfessionalProfileEntity(@NotNull String queueUserId, @NotNull String webProfileId) {
        this.queueUserId = queueUserId;
        this.webProfileId = webProfileId;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public String getPracticeStart() {
        return practiceStart;
    }

    public ProfessionalProfileEntity setPracticeStart(String practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public ProfessionalProfileEntity setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
        return this;
    }

    public Set<NameDatePair> getEducation() {
        return education;
    }

    public ProfessionalProfileEntity setEducation(Set<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public Set<NameDatePair> getLicenses() {
        return licenses;
    }

    public ProfessionalProfileEntity setLicenses(Set<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public Set<NameDatePair> getAwards() {
        return awards;
    }

    public ProfessionalProfileEntity setAwards(Set<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public Set<String> getManagerAtStoreCodeQRs() {
        return managerAtStoreCodeQRs;
    }

    public ProfessionalProfileEntity setManagerAtStoreCodeQRs(Set<String> managerAtStoreCodeQRs) {
        this.managerAtStoreCodeQRs = managerAtStoreCodeQRs;
        return this;
    }

    public ProfessionalProfileEntity addManagerAtStoreCodeQR(String managerAtStoreCodeQR) {
        this.managerAtStoreCodeQRs.add(managerAtStoreCodeQR);
        return this;
    }

    public ProfessionalProfileEntity removeManagerAtStoreCodeQR(String managerAtStoreCodeQR) {
        this.managerAtStoreCodeQRs.remove(managerAtStoreCodeQR);
        return this;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public ProfessionalProfileEntity setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
        return this;
    }

    public FormVersionEnum getFormVersion() {
        return formVersion;
    }

    public ProfessionalProfileEntity setFormVersion(FormVersionEnum formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    @Transient
    public Set<JsonNameDatePair> getEducationAsJson() {
        return getJsonNameDatePairs(education);
    }

    @Transient
    public Set<JsonNameDatePair> getLicensesAsJson() {
        return getJsonNameDatePairs(licenses);
    }

    @Transient
    public Set<JsonNameDatePair> getAwardsAsJson() {
        return getJsonNameDatePairs(awards);
    }
}
