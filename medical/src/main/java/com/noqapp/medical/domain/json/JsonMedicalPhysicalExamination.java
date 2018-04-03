package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.PhysicalExamEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 4/3/18 6:06 PM
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
public class JsonMedicalPhysicalExamination extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicalPhysicalExamination.class);

    @JsonProperty("pe")
    private PhysicalExamEnum physicalExam;

    @JsonProperty ("va")
    private String value;

    @JsonProperty("tr")
    private String testResult;

    public PhysicalExamEnum getPhysicalExam() {
        return physicalExam;
    }

    public JsonMedicalPhysicalExamination setPhysicalExam(PhysicalExamEnum physicalExam) {
        this.physicalExam = physicalExam;
        return this;
    }

    public String getValue() {
        return value;
    }

    public JsonMedicalPhysicalExamination setValue(String value) {
        this.value = value;
        return this;
    }

    public String getTestResult() {
        return testResult;
    }

    public JsonMedicalPhysicalExamination setTestResult(String testResult) {
        this.testResult = testResult;
        return this;
    }
}
