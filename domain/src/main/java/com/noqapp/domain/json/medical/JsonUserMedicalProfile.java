package com.noqapp.domain.json.medical;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.medical.BloodTypeEnum;

/**
 * hitender
 * 5/30/18 5:35 AM
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
public class JsonUserMedicalProfile extends AbstractDomain {

    @JsonProperty("bt")
    private BloodTypeEnum bloodType;

    public BloodTypeEnum getBloodType() {
        return bloodType;
    }

    public JsonUserMedicalProfile setBloodType(BloodTypeEnum bloodType) {
        this.bloodType = bloodType;
        return this;
    }
}
