package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.medical.DailyFrequencyEnum;
import com.noqapp.domain.types.medical.PharmacyCategoryEnum;
import com.noqapp.medical.domain.MedicalMedicineEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

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

    @JsonProperty("pc")
    private String pharmacyCategory;

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

    public String getPharmacyCategory() {
        return pharmacyCategory;
    }

    public JsonMedicalMedicine setPharmacyCategory(String pharmacyCategory) {
        this.pharmacyCategory = pharmacyCategory;
        return this;
    }

    public static JsonMedicalMedicine fromMedicalMedicine(MedicalMedicineEntity medicalMedicine) {
        return new JsonMedicalMedicine()
                .setName(medicalMedicine.getName())
                .setStrength(medicalMedicine.getStrength())
                .setDailyFrequency(medicalMedicine.getDailyFrequency())
                .setCourse(medicalMedicine.getCourse())
                .setMedicationWithFood(medicalMedicine.getMedicationWithFood())
                .setPharmacyCategory(medicalMedicine.getMedicationType());
    }

    @Transient
    public int getTimes() {
        try {
            PharmacyCategoryEnum pharmacyCategory = PharmacyCategoryEnum.valueOf(this.pharmacyCategory);
            switch (pharmacyCategory) {
                case CA:
                case TA:
                    DailyFrequencyEnum dailyFrequencyEnum = DailyFrequencyEnum.valueOf(dailyFrequency);
                    return dailyFrequencyEnum.getTimes() * (StringUtils.isNotBlank(course) ? Integer.parseInt(course) : 1);
                default:
                    return 0;
            }

        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
}
