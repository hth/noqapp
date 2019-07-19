package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-19 08:36
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
public class JsonImmunizationList extends AbstractDomain {

    @JsonProperty("ims")
    private List<JsonImmunization> jsonImmunizations = new ArrayList<>();

    public List<JsonImmunization> getJsonImmunizations() {
        return jsonImmunizations;
    }

    public JsonImmunizationList setJsonImmunizations(List<JsonImmunization> jsonImmunizations) {
        this.jsonImmunizations = jsonImmunizations;
        return this;
    }

    public JsonImmunizationList addJsonImmunization(JsonImmunization jsonImmunization) {
        this.jsonImmunizations.add(jsonImmunization);
        return this;
    }
}
