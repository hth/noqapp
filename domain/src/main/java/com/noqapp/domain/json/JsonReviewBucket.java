package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-04-04 16:53
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
public class JsonReviewBucket extends AbstractDomain {

    @JsonProperty("rls")
    private List<JsonReviewList> jsonReviewLists = new ArrayList<>();

    public List<JsonReviewList> getJsonReviewLists() {
        return jsonReviewLists;
    }

    public JsonReviewBucket setJsonReviewLists(List<JsonReviewList> jsonReviewLists) {
        this.jsonReviewLists = jsonReviewLists;
        return this;
    }

    public JsonReviewBucket addJsonReviewList(JsonReviewList jsonReviewList) {
        this.jsonReviewLists.add(jsonReviewList);
        return this;
    }
}
