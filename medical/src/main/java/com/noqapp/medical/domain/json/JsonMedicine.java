package com.noqapp.medical.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.MedicationRouteEnum;
import com.noqapp.domain.types.MedicationWithFoodEnum;
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
public class JsonMedicine extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonMedicine.class);

    @JsonProperty("na")
    private String name;

    @JsonProperty("st")
    private int strength;

    @JsonProperty("ti")
    private int times;

    @JsonProperty("mf")
    private MedicationWithFoodEnum medicationWithFood;

    @JsonProperty ("mr")
    private MedicationRouteEnum medicationRoute;

    public String getName() {
        return name;
    }

    public JsonMedicine setName(String name) {
        this.name = name;
        return this;
    }

    public int getStrength() {
        return strength;
    }

    public JsonMedicine setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public int getTimes() {
        return times;
    }

    public JsonMedicine setTimes(int times) {
        this.times = times;
        return this;
    }

    public MedicationWithFoodEnum getMedicationWithFood() {
        return medicationWithFood;
    }

    public JsonMedicine setMedicationWithFood(MedicationWithFoodEnum medicationWithFood) {
        this.medicationWithFood = medicationWithFood;
        return this;
    }

    public MedicationRouteEnum getMedicationRoute() {
        return medicationRoute;
    }

    public JsonMedicine setMedicationRoute(MedicationRouteEnum medicationRoute) {
        this.medicationRoute = medicationRoute;
        return this;
    }
}
