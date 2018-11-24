package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.catgeory.MedicalDepartmentEnum;

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
 * 2018-11-23 11:51
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
public class JsonMasterLab extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMasterLab.class);

    @JsonProperty("pn")
    private String productName;

    @JsonProperty("sn")
    private String productShortName;

    @JsonProperty("hs")
    private HealthCareServiceEnum healthCareService;

    @JsonProperty("md")
    private List<MedicalDepartmentEnum> medicalDepartments  = new ArrayList<>();

    public String getProductName() {
        return productName;
    }

    public JsonMasterLab setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public JsonMasterLab setProductShortName(String productShortName) {
        this.productShortName = productShortName;
        return this;
    }

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public JsonMasterLab setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }

    public List<MedicalDepartmentEnum> getMedicalDepartments() {
        return medicalDepartments;
    }

    public JsonMasterLab setMedicalDepartments(List<MedicalDepartmentEnum> medicalDepartments) {
        this.medicalDepartments = medicalDepartments;
        return this;
    }
}
