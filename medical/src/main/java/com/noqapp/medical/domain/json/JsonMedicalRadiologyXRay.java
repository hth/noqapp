package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.RadiologyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 4/3/18 6:07 PM
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
public class JsonMedicalRadiologyXRay extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicalRadiologyXRay.class);

    @JsonProperty("re")
    private RadiologyEnum radiology;

    @JsonProperty("tr")
    private String testResult;

    public RadiologyEnum getRadiology() {
        return radiology;
    }

    public JsonMedicalRadiologyXRay setRadiology(RadiologyEnum radiology) {
        this.radiology = radiology;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public JsonMedicalRadiologyXRay setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
