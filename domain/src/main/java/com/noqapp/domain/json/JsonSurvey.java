package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: hitender
 * Date: 10/22/19 9:15 AM
 */
@SuppressWarnings ({
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
public class JsonSurvey extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonSurvey.class);

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("qr")
    private String codeQR;

    @JsonProperty ("did")
    private String did;

    @JsonProperty("qr")
    private int overallRating;

    @JsonProperty("dr")
    private String[] detailedResponse;

    @JsonProperty("id")
    private String questionnaireId;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonSurvey setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonSurvey setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public JsonSurvey setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getDid() {
        return did;
    }

    public JsonSurvey setDid(String did) {
        this.did = did;
        return this;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public JsonSurvey setOverallRating(int overallRating) {
        this.overallRating = overallRating;
        return this;
    }

    public String[] getDetailedResponse() {
        return detailedResponse;
    }

    public JsonSurvey setDetailedResponse(String[] detailedResponse) {
        this.detailedResponse = detailedResponse;
        return this;
    }

    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public JsonSurvey setQuestionnaireId(String questionnaireId) {
        this.questionnaireId = questionnaireId;
        return this;
    }
}
