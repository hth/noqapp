package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 4/29/20 9:34 PM
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
public class JsonGlobalMedicalDataList extends AbstractDomain {

    @JsonProperty("gmd")
    private List<JsonGlobalMedicalData> jsonGlobalMedicalDataList = new ArrayList<>();

    public List<JsonGlobalMedicalData> getJsonGlobalMedicalDataList() {
        return jsonGlobalMedicalDataList;
    }

    public JsonGlobalMedicalDataList setJsonGlobalMedicalDataList(List<JsonGlobalMedicalData> jsonGlobalMedicalDataList) {
        this.jsonGlobalMedicalDataList = jsonGlobalMedicalDataList;
        return this;
    }
}
