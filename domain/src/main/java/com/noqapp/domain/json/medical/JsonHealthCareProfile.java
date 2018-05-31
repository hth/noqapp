package com.noqapp.domain.json.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
public class JsonHealthCareProfile extends AbstractDomain {

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty("ps")
    private Date practiceStart;

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses = new LinkedList<>();

    @JsonProperty("aw")
    private List<JsonNameDatePair> awards = new LinkedList<>();

    @JsonProperty("md")
    private String medicalDictionary;

    public String getCodeQR() {
        return codeQR;
    }

    public JsonHealthCareProfile setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public JsonHealthCareProfile setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonHealthCareProfile setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonNameDatePair> getLicenses() {
        return licenses;
    }

    public JsonHealthCareProfile setLicenses(List<JsonNameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<JsonNameDatePair> getAwards() {
        return awards;
    }

    public JsonHealthCareProfile setAwards(List<JsonNameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public String getMedicalDictionary() {
        return medicalDictionary;
    }

    public JsonHealthCareProfile setPrescriptionDictionary(String medicalDictionary) {
        this.medicalDictionary = medicalDictionary;
        return this;
    }
}
