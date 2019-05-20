package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.types.AdvertisementTypeEnum;

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
public class JsonAdvertisement extends AbstractDomain {

    @JsonProperty("ai")
    private String advertisementId;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("sd")
    private String shortDescription;

    @JsonProperty("pp")
    private JsonProfessionalProfile jsonProfessionalProfile;

    @JsonProperty("iu")
    private List<String> imageUrls = new ArrayList<>();

    @JsonProperty("tc")
    private List<String> termAndConditions = new ArrayList<>();

    @JsonProperty("at")
    private AdvertisementTypeEnum advertisementType;

    @JsonProperty("ed")
    private String endDate;

    @JsonProperty("ei")
    private boolean endDateInitialized;

    public String getAdvertisementId() {
        return advertisementId;
    }

    public JsonAdvertisement setAdvertisementId(String advertisementId) {
        this.advertisementId = advertisementId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonAdvertisement setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public JsonAdvertisement setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public JsonProfessionalProfile getJsonProfessionalProfile() {
        return jsonProfessionalProfile;
    }

    public JsonAdvertisement setJsonProfessionalProfile(JsonProfessionalProfile jsonProfessionalProfile) {
        this.jsonProfessionalProfile = jsonProfessionalProfile;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public JsonAdvertisement setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public List<String> getTermAndConditions() {
        return termAndConditions;
    }

    public JsonAdvertisement setTermAndConditions(List<String> termAndConditions) {
        this.termAndConditions = termAndConditions;
        return this;
    }

    public AdvertisementTypeEnum getAdvertisementType() {
        return advertisementType;
    }

    public JsonAdvertisement setAdvertisementType(AdvertisementTypeEnum advertisementType) {
        this.advertisementType = advertisementType;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public JsonAdvertisement setEndDate(String endDate) {
        this.endDate = endDate;
        if (StringUtils.isNotBlank(endDate)) {
            this.endDateInitialized = true;
        }
        return this;
    }

    public boolean isEndDateInitialized() {
        return endDateInitialized;
    }

    public JsonAdvertisement setEndDateInitialized(boolean endDateInitialized) {
        this.endDateInitialized = endDateInitialized;
        return this;
    }
}
