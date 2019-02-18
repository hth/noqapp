package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-02-18 11:24
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
public class JsonMedicalPathologyList extends AbstractDomain {

    @JsonProperty("rr")
    private String recordReferenceId;

    @JsonProperty("mps")
    private List<JsonMedicalPathology> jsonMedicalPathologies = new LinkedList<>();

    /** Image resides inside this record. */
    @JsonProperty("im")
    private List<String> images;

    /** Doctors observation. */
    @JsonProperty("ob")
    private String observation;

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public JsonMedicalPathologyList setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public List<JsonMedicalPathology> getJsonMedicalPathologies() {
        return jsonMedicalPathologies;
    }

    public JsonMedicalPathologyList setJsonMedicalPathologies(List<JsonMedicalPathology> jsonMedicalPathologies) {
        this.jsonMedicalPathologies = jsonMedicalPathologies;
        return this;
    }

    public JsonMedicalPathologyList addJsonMedicalPathologies(JsonMedicalPathology jsonMedicalPathology) {
        this.jsonMedicalPathologies.add(jsonMedicalPathology);
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public JsonMedicalPathologyList setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public JsonMedicalPathologyList setObservation(String observation) {
        this.observation = observation;
        return this;
    }
}
