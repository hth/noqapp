package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

    @JsonProperty("na")
    private String name;

    @JsonProperty("tr")
    private String testResult;

    public String getName() {
        return name;
    }

    public JsonMedicalRadiologyXRay setName(String name) {
        this.name = name;
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
