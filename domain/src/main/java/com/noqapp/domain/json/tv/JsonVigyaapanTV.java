package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.types.VigyaapanTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2018-12-20 12:55
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
public class JsonVigyaapanTV extends AbstractDomain {

    @JsonProperty("vi")
    private String vigyaapanId;

    @JsonProperty("pp")
    private JsonProfessionalProfile jsonProfessionalProfile;

    @JsonProperty("iu")
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty("vt")
    private VigyaapanTypeEnum vigyaapanType;

    @JsonProperty("ed")
    private String endDate;

    @JsonProperty("ei")
    private boolean endDateInitialized;

    public String getVigyaapanId() {
        return vigyaapanId;
    }

    public JsonVigyaapanTV setVigyaapanId(String vigyaapanId) {
        this.vigyaapanId = vigyaapanId;
        return this;
    }

    public JsonProfessionalProfile getJsonProfessionalProfile() {
        return jsonProfessionalProfile;
    }

    public JsonVigyaapanTV setJsonProfessionalProfile(JsonProfessionalProfile jsonProfessionalProfile) {
        this.jsonProfessionalProfile = jsonProfessionalProfile;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public JsonVigyaapanTV setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public VigyaapanTypeEnum getVigyaapanType() {
        return vigyaapanType;
    }

    public JsonVigyaapanTV setVigyaapanType(VigyaapanTypeEnum vigyaapanType) {
        this.vigyaapanType = vigyaapanType;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public JsonVigyaapanTV setEndDate(String endDate) {
        this.endDate = endDate;
        if (StringUtils.isNotBlank(endDate)) {
            this.endDateInitialized = true;
        }
        return this;
    }

    public boolean isEndDateInitialized() {
        return endDateInitialized;
    }

    public JsonVigyaapanTV setEndDateInitialized(boolean endDateInitialized) {
        this.endDateInitialized = endDateInitialized;
        return this;
    }
}
