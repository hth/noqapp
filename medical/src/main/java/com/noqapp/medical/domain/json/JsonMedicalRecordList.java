package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 4/3/18 10:38 PM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonMedicalRecordList extends AbstractDomain {

    @JsonProperty("mrs")
    private List<JsonMedicalRecord> jsonMedicalRecords = new LinkedList<>();

    public List<JsonMedicalRecord> getJsonMedicalRecords() {
        return jsonMedicalRecords;
    }

    public JsonMedicalRecordList setJsonMedicalRecords(List<JsonMedicalRecord> jsonMedicalRecords) {
        this.jsonMedicalRecords = jsonMedicalRecords;
        return this;
    }

    public JsonMedicalRecordList addJsonMedicalRecords(JsonMedicalRecord jsonMedicalRecord) {
        this.jsonMedicalRecords.add(jsonMedicalRecord);
        return this;
    }
}
