package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2019-01-23 18:25
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
public class JsonPreferredBusinessBucket extends AbstractDomain {

    private List<JsonPreferredBusinessList> jsonPreferredBusinessLists = new ArrayList<>();

    public List<JsonPreferredBusinessList> getJsonPreferredBusinessLists() {
        return jsonPreferredBusinessLists;
    }

    public JsonPreferredBusinessBucket setJsonPreferredBusinessLists(List<JsonPreferredBusinessList> jsonPreferredBusinessLists) {
        this.jsonPreferredBusinessLists = jsonPreferredBusinessLists;
        return this;
    }

    public JsonPreferredBusinessBucket addJsonPreferredBusinessList(JsonPreferredBusinessList jsonPreferredBusinessList) {
        this.jsonPreferredBusinessLists.add(jsonPreferredBusinessList);
        return this;
    }
}
