package com.noqapp.domain.json.marketplace;

import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.StringJoiner;

/**
 * hitender
 * 3/7/21 12:06 PM
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
public class JsonPropertyRental extends JsonMarketplace {

    @JsonProperty("be")
    private int bedroom;

    @JsonProperty("br")
    private int bathroom;

    @JsonProperty("ca")
    private int carpetArea;

    @JsonProperty("rt")
    private RentalTypeEnum rentalType;

    @JsonProperty("ra")
    private String rentalAvailableDay;

    /** Used when housing agent has been selected. Set only once. */
    @JsonProperty("hq")
    private String housingAgentQID;

    /** Review submitted by owner against the housing agent for providing service. */
    @JsonProperty("hr")
    private String housingAgentReview;

    public int getBedroom() {
        return bedroom;
    }

    public JsonPropertyRental setBedroom(int bedroom) {
        this.bedroom = bedroom;
        return this;
    }

    public int getBathroom() {
        return bathroom;
    }

    public JsonPropertyRental setBathroom(int bathroom) {
        this.bathroom = bathroom;
        return this;
    }

    public int getCarpetArea() {
        return carpetArea;
    }

    public JsonPropertyRental setCarpetArea(int carpetArea) {
        this.carpetArea = carpetArea;
        return this;
    }

    public RentalTypeEnum getRentalType() {
        return rentalType;
    }

    public JsonPropertyRental setRentalType(RentalTypeEnum rentalType) {
        this.rentalType = rentalType;
        return this;
    }

    public String getRentalAvailableDay() {
        return rentalAvailableDay;
    }

    public JsonPropertyRental setRentalAvailableDay(String rentalAvailableDay) {
        this.rentalAvailableDay = rentalAvailableDay;
        return this;
    }

    public String getHousingAgentQID() {
        return housingAgentQID;
    }

    public JsonPropertyRental setHousingAgentQID(String housingAgentQID) {
        this.housingAgentQID = housingAgentQID;
        return this;
    }

    public String getHousingAgentReview() {
        return housingAgentReview;
    }

    public JsonPropertyRental setHousingAgentReview(String housingAgentReview) {
        this.housingAgentReview = housingAgentReview;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonPropertyRental.class.getSimpleName() + "[", "]")
            .add("bedroom=" + bedroom)
            .add("bathroom=" + bathroom)
            .add("carpetArea=" + carpetArea)
            .add("rentalType=" + rentalType)
            .add("rentalAvailableDay='" + rentalAvailableDay + "'")
            .add("housingAgentQID='" + housingAgentQID + "'")
            .add("housingAgentReview='" + housingAgentReview + "'")
            .toString();
    }
}
