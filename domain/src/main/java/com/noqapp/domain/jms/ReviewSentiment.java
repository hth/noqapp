package com.noqapp.domain.jms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * hitender
 * 7/21/20 3:33 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewSentiment implements Serializable {
    @SuppressWarnings ({"unused"})
    @SerializedName("storeName")
    private String storeName;

    @SuppressWarnings ({"unused"})
    @SerializedName ("reviewerName")
    private String reviewerName;

    @SuppressWarnings ({"unused"})
    @SerializedName ("reviewerPhone")
    private String reviewerPhone;

    @SerializedName ("ratingCount")
    private int ratingCount;

    @SerializedName ("hourSaved")
    private int hourSaved;

    @SerializedName ("review")
    private String review;

    @SerializedName ("sentiment")
    private String sentiment;

    @SerializedName ("sentimentWatcherEmail")
    private String sentimentWatcherEmail;

    private ReviewSentiment(
        String storeName,
        String reviewerName,
        String reviewerPhone,
        int ratingCount,
        int hourSaved,
        String review,
        String sentiment,
        String sentimentWatcherEmail
    ) {
        this.storeName = storeName;
        this.reviewerName = reviewerName;
        this.reviewerPhone = reviewerPhone;
        this.ratingCount = ratingCount;
        this.hourSaved = hourSaved;
        this.review = review;
        this.sentiment = sentiment;
        this.sentimentWatcherEmail = sentimentWatcherEmail;
    }

    public static ReviewSentiment newInstance(
        String storeName,
        String reviewerName,
        String reviewerPhone,
        int ratingCount,
        int hourSaved,
        String review,
        String sentiment,
        String sentimentWatcherEmail
    ) {
        return new ReviewSentiment(
            storeName,
            reviewerName,
            reviewerPhone,
            ratingCount,
            hourSaved,
            review,
            sentiment,
            sentimentWatcherEmail);
    }

    public String getStoreName() {
        return storeName;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewerPhone() {
        return reviewerPhone;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getHourSaved() {
        return hourSaved;
    }

    public String getReview() {
        return review;
    }

    public String getSentiment() {
        return sentiment;
    }

    public String getSentimentWatcherEmail() {
        return sentimentWatcherEmail;
    }
}
