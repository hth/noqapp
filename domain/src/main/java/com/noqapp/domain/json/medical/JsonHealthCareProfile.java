package com.noqapp.domain.json.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

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
    private Date practiceStart = Date.from(Instant.now().minus(20, ChronoUnit.YEARS));

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    private List<JsonNameDatePair> education = new LinkedList<JsonNameDatePair>() {{
        add(new JsonNameDatePair().setName("KJ Somaiya").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
        add(new JsonNameDatePair().setName("Dako Baiya University").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
    }};

    /* Required to mark as a valid profile. */
    @JsonProperty("li")
    private List<JsonNameDatePair> licenses = new LinkedList<JsonNameDatePair>() {{
        add(new JsonNameDatePair().setName("M.B.B.S").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
        add(new JsonNameDatePair().setName("M.D").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
    }};

    @JsonProperty("aw")
    private List<JsonNameDatePair> awards = new LinkedList<JsonNameDatePair>() {{
        add(new JsonNameDatePair().setName("Awards Prestigious Award").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
        add(new JsonNameDatePair().setName("Animal Doctor Award").setMonthYear(DateFormatUtils.format(new Date(), ISO8601_FMT, TimeZone.getTimeZone("UTC"))));
    }};

    @JsonProperty("pd")
    private String prescriptionDictionary = "amoxycillin,penicillin";

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

    public String getPrescriptionDictionary() {
        return prescriptionDictionary;
    }

    public JsonHealthCareProfile setPrescriptionDictionary(String prescriptionDictionary) {
        this.prescriptionDictionary = prescriptionDictionary;
        return this;
    }
}
