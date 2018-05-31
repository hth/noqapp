package com.noqapp.domain.json.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.helper.NameDatePair;

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
    private List<NameDatePair> education = new LinkedList<>();

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<NameDatePair> licenses = new LinkedList<>();

    @JsonProperty("aw")
    private List<NameDatePair> awards = new LinkedList<>();

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

    public List<NameDatePair> getEducation() {
        return education;
    }

    public JsonHealthCareProfile setEducation(List<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<NameDatePair> getLicenses() {
        return licenses;
    }

    public JsonHealthCareProfile setLicenses(List<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public JsonHealthCareProfile setAwards(List<NameDatePair> awards) {
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
