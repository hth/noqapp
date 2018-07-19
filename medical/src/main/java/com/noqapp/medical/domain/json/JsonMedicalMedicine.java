package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.medical.domain.MedicalMedicineEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 4/3/18 6:07 PM
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
public class JsonMedicalMedicine extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicalMedicine.class);

    @JsonProperty("na")
    private String name;

    @JsonProperty("st")
    private String strength;

    @JsonProperty("df")
    private String dailyFrequency;

    @JsonProperty("co")
    private String course;

    @JsonProperty("mf")
    private String medicationWithFood;

    @JsonProperty("mt")
    private String medicationType;

    public String getName() {
        return name;
    }

    public JsonMedicalMedicine setName(String name) {
        this.name = name;
        return this;
    }

    public String getStrength() {
        return strength;
    }

    public JsonMedicalMedicine setStrength(String strength) {
        this.strength = strength;
        return this;
    }

    public String getDailyFrequency() {
        return dailyFrequency;
    }

    public JsonMedicalMedicine setDailyFrequency(String dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
        return this;
    }

    public String getCourse() {
        return course;
    }

    public JsonMedicalMedicine setCourse(String course) {
        this.course = course;
        return this;
    }

    public String getMedicationWithFood() {
        return medicationWithFood;
    }

    public JsonMedicalMedicine setMedicationWithFood(String medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public String getMedicationType() {
        return medicationType;
    }

    public JsonMedicalMedicine setMedicationType(String medicationType) {
        this.medicationType = medicationType;
        return this;
    }

    public static JsonMedicalMedicine fromMedicalMedicine(MedicalMedicineEntity medicalMedicine) {
        return new JsonMedicalMedicine()
                .setName(medicalMedicine.getName())
                .setStrength(medicalMedicine.getStrength())
                .setDailyFrequency(medicalMedicine.getDailyFrequency())
                .setCourse(medicalMedicine.getCourse())
                .setMedicationWithFood(medicalMedicine.getMedicationWithFood())
                .setMedicationType(medicalMedicine.getMedicationType());
    }
}
