package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

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

    @JsonProperty ("pi")
    private String profileImage;

    @JsonProperty ("nm")
    private String name;

    public JsonReview() {
        //Required default constructor
    }

    public JsonReview(int ratingCount, String review, String profileImage, String name) {
        this.ratingCount = ratingCount;
        this.review = review;
        this.profileImage = profileImage;
        this.name = abbreviateName(name);
    }

    private String abbreviateName(String name) {
        if (name.contains(" ")) {
            String[] splits = name.split(" ");
            if (splits.length >= 1) {
                return splits[0] + splits[1].substring(0, 1);
            } else {
                return name;
            }
        } else {
            return name;
        }
    }
}
