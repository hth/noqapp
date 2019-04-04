package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.CommonUtil;

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

    @JsonProperty("id")
    private String id;

    @JsonProperty("ra")
    private int ratingCount;

    @JsonProperty("rv")
    private String review;

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty ("nm")
    private String name;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("rs")
    private boolean reviewShow;

    @JsonProperty("c")
    private String created;

    public JsonReview() {
        //Required default constructor
    }

    public JsonReview(String id, int ratingCount, String review, String profileImage, String name, boolean reviewShow, String created) {
        this.id = id;
        this.ratingCount = ratingCount;
        this.review = review;
        this.profileImage = profileImage;
        this.name = CommonUtil.abbreviateName(name);
        this.reviewShow = reviewShow;
        this.created = created;
    }
}
