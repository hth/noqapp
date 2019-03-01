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
public class JsonVigyaapanTVList extends AbstractDomain {

    @JsonProperty("vts")
    private List<JsonVigyaapanTV> jsonVigyaapanTVs = new ArrayList<>();

    public List<JsonVigyaapanTV> getJsonVigyaapanTVs() {
        return jsonVigyaapanTVs;
    }

    public JsonVigyaapanTVList setJsonVigyaapanTVs(List<JsonVigyaapanTV> jsonVigyaapanTVs) {
        this.jsonVigyaapanTVs = jsonVigyaapanTVs;
        return this;
    }

    public JsonVigyaapanTVList addJsonVigyaapanTV(JsonVigyaapanTV jsonVigyaapanTV) {
        this.jsonVigyaapanTVs.add(jsonVigyaapanTV);
        return this;
    }
}
