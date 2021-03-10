package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.medical.domain.MedicalPhysicalEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

/**
 * hitender
 * 6/16/18 7:25 PM
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
public class JsonMedicalPhysical extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicalMedicine.class);

    @JsonProperty("te")
    private String temperature;

    @JsonProperty("pl")
    private String pulse;

    @JsonProperty("bp")
    private String[] bloodPressure;

    @JsonProperty("ox")
    private String oxygen;

    @JsonProperty("rp")
    private String respiratory;

    @JsonProperty("wt")
    private String weight;

    @JsonProperty("ht")
    private String height;

    @JsonProperty("dbi")
    private String diagnosedById;

    @JsonProperty("pf")
    private boolean physicalFilled;

    public String getTemperature() {
        return temperature;
    }

    public JsonMedicalPhysical setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getPulse() {
        return pulse;
    }

    public JsonMedicalPhysical setPulse(String pulse) {
        this.pulse = pulse;
        return this;
    }

    public String[] getBloodPressure() {
        return bloodPressure;
    }

    public JsonMedicalPhysical setBloodPressure(String[] bloodPressure) {
        this.bloodPressure = bloodPressure;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public JsonMedicalPhysical setWeight(String weight) {
        this.weight = weight;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public JsonMedicalPhysical setHeight(String height) {
        this.height = height;
        return this;
    }

    public String getOxygen() {
        return oxygen;
    }

    public JsonMedicalPhysical setOxygen(String oxygen) {
        this.oxygen = oxygen;
        return this;
    }

    public String getRespiratory() {
        return respiratory;
    }

    public JsonMedicalPhysical setRespiratory(String respiratory) {
        this.respiratory = respiratory;
        return this;
    }

    public String getDiagnosedById() {
        return diagnosedById;
    }

    public JsonMedicalPhysical setDiagnosedById(String diagnosedById) {
        this.diagnosedById = diagnosedById;
        return this;
    }

    public boolean isPhysicalFilled() {
        return physicalFilled;
    }

    public JsonMedicalPhysical setPhysicalFilled(boolean physicalFilled) {
        this.physicalFilled = physicalFilled;
        return this;
    }

    @Transient
    public static JsonMedicalPhysical populateJsonMedicalPhysical(MedicalPhysicalEntity medicalPhysical) {
        return new JsonMedicalPhysical()
            .setTemperature(medicalPhysical.getTemperature())
            .setBloodPressure(medicalPhysical.getBloodPressure())
            .setPulse(medicalPhysical.getPulse())
            .setOxygen(medicalPhysical.getOxygen())
            .setRespiratory(medicalPhysical.getRespiratory())
            .setWeight(medicalPhysical.getWeight())
            .setHeight(medicalPhysical.getHeight())
            .setPhysicalFilled(true);
    }
}
