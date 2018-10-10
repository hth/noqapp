package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 10/10/18 10:14 PM
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
public class JsonReview extends AbstractDomain {

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("rv")
    private String review;

    public int getRatingCount() {
        return ratingCount;
    }

    public JsonReview setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
        return this;
    }

    public String getReview() {
        return review;
    }

    public JsonReview setReview(String review) {
        this.review = review;
        return this;
    }

    public static JsonReview queueReview(QueueEntity o) {
        return new JsonReview()
            .setRatingCount(o.getRatingCount())
            .setReview(o.getReview());
    }

    public static JsonReview queueReview(PurchaseOrderEntity o) {
        return new JsonReview()
            .setRatingCount(o.getRatingCount())
            .setReview(o.getReview());
    }
}
