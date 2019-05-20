package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonProfessionalProfile;
import com.noqapp.domain.types.AdvertisementTypeEnum;
import com.noqapp.domain.types.AdvertisementViewerTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private List<String> termsAndConditions = new ArrayList<>();

    @JsonProperty("at")
    private AdvertisementTypeEnum advertisementType;

    @JsonProperty("n")
    private String businessName;

    @JsonProperty("av")
    private AdvertisementViewerTypeEnum advertisementViewerType;

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

    public List<String> getTermsAndConditions() {
        return termsAndConditions;
    }

    public JsonAdvertisement setTermsAndConditions(List<String> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
        return this;
    }

    public AdvertisementTypeEnum getAdvertisementType() {
        return advertisementType;
    }

    public JsonAdvertisement setAdvertisementType(AdvertisementTypeEnum advertisementType) {
        this.advertisementType = advertisementType;
        return this;
    }

    public String getBusinessName() {
        return businessName;
    }

    public JsonAdvertisement setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public AdvertisementViewerTypeEnum getAdvertisementViewerType() {
        return advertisementViewerType;
    }

    public JsonAdvertisement setAdvertisementViewerType(AdvertisementViewerTypeEnum advertisementViewerType) {
        this.advertisementViewerType = advertisementViewerType;
        return this;
    }
}
