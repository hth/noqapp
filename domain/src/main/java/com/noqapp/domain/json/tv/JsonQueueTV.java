package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonNameDatePair;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-17 18:11
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
public class JsonQueueTV extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueueTV.class);

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty("ed")
    private List<JsonNameDatePair> education;

    @JsonProperty("qps")
    private List<JsonQueuedPersonTV> jsonQueuedPersonTVList = new ArrayList<>();

    public String getCodeQR() {
        return codeQR;
    }

    public JsonQueueTV setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public JsonQueueTV setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public List<JsonNameDatePair> getEducation() {
        return education;
    }

    public JsonQueueTV setEducation(List<JsonNameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<JsonQueuedPersonTV> getJsonQueuedPersonTVList() {
        return jsonQueuedPersonTVList;
    }

    public JsonQueueTV setJsonQueuedPersonTVList(List<JsonQueuedPersonTV> jsonQueuedPersonTVList) {
        this.jsonQueuedPersonTVList = jsonQueuedPersonTVList;
        return this;
    }

    public JsonQueueTV addJsonQueuedPersonTVList(JsonQueuedPersonTV jsonQueuedPersonTV) {
        this.jsonQueuedPersonTVList.add(jsonQueuedPersonTV);
        return this;
    }
}
