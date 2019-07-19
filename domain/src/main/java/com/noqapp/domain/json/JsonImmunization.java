package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * User: hitender
 * Date: 2019-07-19 08:17
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
public class JsonImmunization extends AbstractDomain {

    @JsonProperty("in")
    private String name;

    @JsonProperty("dd")
    private String dueDate;

    @JsonProperty("is")
    private boolean status;

    public String getName() {
        return name;
    }

    public JsonImmunization setName(String name) {
        this.name = name;
        return this;
    }

    public String getDueDate() {
        return dueDate;
    }

    public JsonImmunization setDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public boolean isStatus() {
        return status;
    }

    public JsonImmunization setStatus(boolean status) {
        this.status = status;
        return this;
    }
}
