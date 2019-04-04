package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 10/10/18 10:18 PM
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
public class JsonReviewList extends AbstractDomain {

    @JsonProperty("d")
    private String displayName;

    @JsonProperty("rs")
    private List<JsonReview> jsonReviews = new ArrayList<>();

    @JsonProperty("ar")
    private int aggregateRatingCount;

    public String getDisplayName() {
        return displayName;
    }

    public JsonReviewList setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public List<JsonReview> getJsonReviews() {
        return jsonReviews;
    }

    public JsonReviewList setJsonReviews(List<JsonReview> jsonReviews) {
        this.jsonReviews = jsonReviews;
        return this;
    }

    public JsonReviewList addJsonReview(JsonReview jsonReview) {
        this.jsonReviews.add(jsonReview);
        return this;
    }

    public int getAggregateRatingCount() {
        return aggregateRatingCount;
    }

    public JsonReviewList setAggregateRatingCount(int aggregateRatingCount) {
        this.aggregateRatingCount = aggregateRatingCount;
        return this;
    }

    public JsonReviewList addRatingCount(int aggregateRatingCount) {
        this.aggregateRatingCount += aggregateRatingCount;
        return this;
    }
}
