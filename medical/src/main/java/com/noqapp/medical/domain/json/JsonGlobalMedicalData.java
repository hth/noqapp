package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.medical.domain.MasterLabEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 4/29/20 9:33 PM
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
public class JsonGlobalMedicalData extends AbstractDomain {

    @JsonProperty("pn")
    private String productName;

    @JsonProperty("sn")
    private String productShortName;

    @JsonProperty("hs")
    private HealthCareServiceEnum healthCareService;

    public String getProductName() {
        return productName;
    }

    public JsonGlobalMedicalData setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public JsonGlobalMedicalData setProductShortName(String productShortName) {
        this.productShortName = productShortName;
        return this;
    }

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public JsonGlobalMedicalData setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }

    public static JsonGlobalMedicalData toJson(MasterLabEntity masterLab) {
        return new JsonGlobalMedicalData()
            .setProductName(masterLab.getProductName())
            .setProductShortName(masterLab.getProductShortName())
            .setHealthCareService(masterLab.getHealthCareService());
    }
}
