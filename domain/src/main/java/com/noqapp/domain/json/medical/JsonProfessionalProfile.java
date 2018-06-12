package com.noqapp.domain.json.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.json.JsonStore;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
public class JsonProfessionalProfile extends AbstractDomain {

    @JsonProperty ("wp")
    private String webProfileId;

    @JsonProperty("ps")
    private Date practiceStart = Date.from(ZonedDateTime.now(ZoneOffset.UTC).minus(20, ChronoUnit.YEARS).toInstant());

    /* Required to mark as a valid profile. */
    @JsonProperty("ed")
    //TODO should be studies
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

    @JsonIgnoreProperties
    private Set<String> managerAtStoreCodeQRs = new HashSet<>();

    @JsonProperty("st")
    private List<JsonStore> stores = new ArrayList<>();

    @JsonProperty("dd")
    private String dataDictionary = "amoxycillin,penicillin";

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonProfessionalProfile setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public JsonProfessionalProfile setPracticeStart(Date practiceStart) {
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
        return DateUtil.getYearsBetween(practiceStart, new Date());
    }
}
