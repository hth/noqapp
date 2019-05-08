package com.noqapp.medical.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.medical.JsonUserMedicalProfile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * User: hitender
 * Date: 2019-05-08 13:57
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
public class JsonMedicalProfile extends AbstractDomain {

    @JsonProperty("mps")
    private List<JsonMedicalPhysical> jsonMedicalPhysicals = new LinkedList<>();

    @JsonProperty("um")
    private JsonUserMedicalProfile jsonUserMedicalProfile;

    public List<JsonMedicalPhysical> getJsonMedicalPhysicals() {
        return jsonMedicalPhysicals;
    }

    public JsonMedicalProfile setJsonMedicalPhysicals(List<JsonMedicalPhysical> jsonMedicalPhysicals) {
        this.jsonMedicalPhysicals = jsonMedicalPhysicals;
        return this;
    }

    public JsonMedicalProfile addJsonMedicalPhysical(JsonMedicalPhysical jsonMedicalPhysical) {
        this.jsonMedicalPhysicals.add(jsonMedicalPhysical);
        return this;
    }

    public JsonUserMedicalProfile getJsonUserMedicalProfile() {
        return jsonUserMedicalProfile;
    }

    public JsonMedicalProfile setJsonUserMedicalProfile(JsonUserMedicalProfile jsonUserMedicalProfile) {
        this.jsonUserMedicalProfile = jsonUserMedicalProfile;
        return this;
    }
}
