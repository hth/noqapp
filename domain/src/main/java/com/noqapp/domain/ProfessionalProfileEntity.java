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
import java.util.LinkedList;
import java.util.List;
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
    private List<NameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @Field("LI")
    private List<NameDatePair> licenses = new LinkedList<>();

    @Field("AW")
    private List<NameDatePair> awards = new LinkedList<>();

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

    public List<NameDatePair> getEducation() {
        return education;
    }

    public ProfessionalProfileEntity setEducation(List<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<NameDatePair> getLicenses() {
        return licenses;
    }

    public ProfessionalProfileEntity setLicenses(List<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public ProfessionalProfileEntity setAwards(List<NameDatePair> awards) {
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
}
