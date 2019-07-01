package com.noqapp.domain.json.tv;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.json.JsonProfessionalProfile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-07-01 12:33
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
public class JsonProfessionalProfileTVList extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonQueueTVList.class);

    @JsonProperty("pps")
    private List<JsonProfessionalProfile> jsonProfessionalProfileTV = new ArrayList<>();

    public List<JsonProfessionalProfile> getJsonProfessionalProfileTV() {
        return jsonProfessionalProfileTV;
    }

    public JsonProfessionalProfileTVList setJsonProfessionalProfileTV(List<JsonProfessionalProfile> jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV = jsonProfessionalProfileTV;
        return this;
    }

    public JsonProfessionalProfileTVList addJsonProfessionalProfileTV(JsonProfessionalProfile jsonProfessionalProfileTV) {
        this.jsonProfessionalProfileTV.add(jsonProfessionalProfileTV);
        return this;
    }
}
