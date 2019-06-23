package com.noqapp.domain.json.sms.textlocal;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-23 23:39
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
public class BaseMessage extends AbstractDomain {

    @JsonProperty("errors")
    private List<FailureMessage> errors;

    @JsonProperty("warnings")
    private List<FailureMessage> warnings;

    @JsonProperty("status")
    private String status;

    public List<FailureMessage> getErrors() {
        return errors;
    }

    public BaseMessage setErrors(List<FailureMessage> errors) {
        this.errors = errors;
        return this;
    }

    public List<FailureMessage> getWarnings() {
        return warnings;
    }

    public BaseMessage setWarnings(List<FailureMessage> warnings) {
        this.warnings = warnings;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public BaseMessage setStatus(String status) {
        this.status = status;
        return this;
    }
}
