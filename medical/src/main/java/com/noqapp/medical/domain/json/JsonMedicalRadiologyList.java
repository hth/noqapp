package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.medical.LabCategoryEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 2019-01-10 13:57
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
public class JsonMedicalRadiologyList extends AbstractDomain {

    @JsonProperty("rr")
    private String recordReferenceId;

    @JsonProperty("lc")
    private LabCategoryEnum labCategory;

    @JsonProperty("bs")
    private String bizStoreId;

    @JsonProperty("mps")
    private List<JsonMedicalRadiology> jsonMedicalRadiologies = new LinkedList<>();

    /** Image resides inside this record. */
    @JsonProperty("im")
    private List<String> images;

    /** Doctors observation. */
    @JsonProperty("ob")
    private String observation;

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public JsonMedicalRadiologyList setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public LabCategoryEnum getLabCategory() {
        return labCategory;
    }

    public JsonMedicalRadiologyList setLabCategory(LabCategoryEnum labCategory) {
        this.labCategory = labCategory;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public JsonMedicalRadiologyList setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public List<JsonMedicalRadiology> getJsonMedicalRadiologies() {
        return jsonMedicalRadiologies;
    }

    public JsonMedicalRadiologyList setJsonMedicalRadiologies(List<JsonMedicalRadiology> jsonMedicalRadiologies) {
        this.jsonMedicalRadiologies = jsonMedicalRadiologies;
        return this;
    }

    public JsonMedicalRadiologyList addJsonMedicalRadiologies(JsonMedicalRadiology jsonMedicalRadiology) {
        this.jsonMedicalRadiologies.add(jsonMedicalRadiology);
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public JsonMedicalRadiologyList setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public JsonMedicalRadiologyList setObservation(String observation) {
        this.observation = observation;
        return this;
    }
}
