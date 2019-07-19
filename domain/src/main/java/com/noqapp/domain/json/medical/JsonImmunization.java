package com.noqapp.domain.json.medical;

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

    @JsonProperty("mn")
    private String name;

    @JsonProperty("md")
    private String immunizationDate;

    @JsonProperty("mh")
    private String header;

    @JsonProperty("dd")
    private String dueDate;

    public String getName() {
        return name;
    }

    public JsonImmunization setName(String name) {
        this.name = name;
        return this;
    }

    public String getImmunizationDate() {
        return immunizationDate;
    }

    public JsonImmunization setImmunizationDate(String immunizationDate) {
        this.immunizationDate = immunizationDate;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public JsonImmunization setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getDueDate() {
        return dueDate;
    }

    public JsonImmunization setDueDate(String dueDate) {
        this.dueDate = dueDate;
        return this;
    }
}
