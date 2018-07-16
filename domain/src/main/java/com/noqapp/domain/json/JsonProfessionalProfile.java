package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.DateUtil;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 5/31/18 2:21 PM
 */
@SuppressWarnings({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonProfessionalProfile extends AbstractDomain {

    @JsonProperty ("wp")
    private String webProfileId;

    @JsonProperty("ps")
    private String practiceStart;

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education;

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses;

    @JsonProperty("aw")
    private List<JsonNameDatePair> awards;

    @JsonIgnoreProperties
    private Set<String> managerAtStoreCodeQRs = new HashSet<>();

    @JsonProperty("st")
    private List<JsonStore> stores = new ArrayList<>();

    /* Stores all data generated by professionals. */
    @JsonProperty("dd")
    private String dataDictionary;

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonProfessionalProfile setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public String getPracticeStart() {
        return practiceStart;
    }

    public JsonProfessionalProfile setPracticeStart(String practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonProfessionalProfile setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public JsonProfessionalProfile setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<JsonNameDatePair> getAwards() {
        return awards;
    }

    public JsonProfessionalProfile setAwards(List<JsonNameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public Set<String> getManagerAtStoreCodeQRs() {
        return managerAtStoreCodeQRs;
    }

    public JsonProfessionalProfile setManagerAtStoreCodeQRs(Set<String> managerAtStoreCodeQRs) {
        this.managerAtStoreCodeQRs = managerAtStoreCodeQRs;
        return this;
    }

    public List<JsonStore> getStores() {
        return stores;
    }

    public JsonProfessionalProfile setStores(List<JsonStore> stores) {
        this.stores = stores;
        return this;
    }

    public JsonProfessionalProfile addStore(JsonStore store) {
        this.stores.add(store);
        return this;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public JsonProfessionalProfile setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
        return this;
    }

    @JsonIgnore
    public int experienceDuration() {
        if (StringUtils.isNotBlank(practiceStart)) {
            return DateUtil.getYearsBetween(DateUtil.convertToDate(practiceStart), new Date());
        }

        return 0;
    }
}
