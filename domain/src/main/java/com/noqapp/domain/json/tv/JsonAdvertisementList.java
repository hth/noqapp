package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2019-03-01 15:17
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
public class JsonAdvertisementList extends AbstractDomain {

    @JsonProperty("ads")
    private List<JsonAdvertisement> jsonAdvertisements = new ArrayList<>();

    public List<JsonAdvertisement> getJsonAdvertisements() {
        return jsonAdvertisements;
    }

    public JsonAdvertisementList setJsonAdvertisements(List<JsonAdvertisement> jsonAdvertisements) {
        this.jsonAdvertisements = jsonAdvertisements;
        return this;
    }

    public JsonAdvertisementList addJsonAdvertisement(JsonAdvertisement jsonAdvertisement) {
        this.jsonAdvertisements.add(jsonAdvertisement);
        return this;
    }
}
