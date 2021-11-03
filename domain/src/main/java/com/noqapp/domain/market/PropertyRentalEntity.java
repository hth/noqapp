package com.noqapp.domain.market;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.marketplace.JsonPropertyRental;
import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * All fields are added to tag. Example IC_itemCondition: G_IC, P_IC, V_IC.
 * hitender
 * 12/30/20 7:02 PM
 */ 
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "MP_PROPERTY_RENTAL")
public class PropertyRentalEntity extends MarketplaceEntity {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyRentalEntity.class);

    @Field("BE")
    private int bedroom;

    @Field("BR")
    private int bathroom;

    @Field("CA")
    private int carpetArea;

    @Field("RT")
    private RentalTypeEnum rentalType;

    @Field("RA")
    private String rentalAvailableDay;

    /** Used when housing agent has been selected. Set only once. */
    @Field("HQ")
    private String housingAgentQID;

    /** Review submitted by owner against the housing agent for providing service. */
    @Field("HR")
    private String housingAgentReview;

    public int getBedroom() {
        return bedroom;
    }

    public PropertyRentalEntity setBedroom(int bedroom) {
        this.bedroom = bedroom;
        return this;
    }

    public int getBathroom() {
        return bathroom;
    }

    public PropertyRentalEntity setBathroom(int bathroom) {
        this.bathroom = bathroom;
        return this;
    }

    public int getCarpetArea() {
        return carpetArea;
    }

    public PropertyRentalEntity setCarpetArea(int carpetArea) {
        this.carpetArea = carpetArea;
        return this;
    }

    public RentalTypeEnum getRentalType() {
        return rentalType;
    }

    public PropertyRentalEntity setRentalType(RentalTypeEnum rentalType) {
        this.rentalType = rentalType;
        return this;
    }

    public String getRentalAvailableDay() {
        return rentalAvailableDay;
    }

    public PropertyRentalEntity setRentalAvailableDay(String rentalAvailableDay) {
        this.rentalAvailableDay = rentalAvailableDay;
        return this;
    }

    public String getHousingAgentQID() {
        return housingAgentQID;
    }

    public PropertyRentalEntity setHousingAgentQID(String housingAgentQID) {
        this.housingAgentQID = housingAgentQID;
        return this;
    }

    public String getHousingAgentReview() {
        return housingAgentReview;
    }

    public PropertyRentalEntity setHousingAgentReview(String housingAgentReview) {
        this.housingAgentReview = housingAgentReview;
        return this;
    }

    @Override
    @Transient
    public String getFieldValueForTag() {
        return bedroom + UNDER_SCORE + "BE" + " "
            + bathroom + UNDER_SCORE + "BR" + " "
            + carpetArea + UNDER_SCORE + "CA" + " "
            + rentalType.name() + UNDER_SCORE + "RT" + " "
            + rentalAvailableDay + UNDER_SCORE + "RA" + " "
            + displayPriceWithoutDecimal() + UNDER_SCORE + "PP"
            + (StringUtils.isNotBlank(getTags()) ? " " + getTags() : "");
    }

    @Override
    @Transient
    public String[] getFieldTags() {
        return new String[] {
            "Bedroom " + bedroom,
            "Bathroom " + bathroom,
            "Carpet-Area " + carpetArea + " sq ft",
            "Rent " + rentalType.getDescription(),
            "Available " + rentalAvailableDay
        };
    }

    @Mobile
    public JsonPropertyRental populateJson() {
        JsonPropertyRental jsonPropertyRental = new JsonPropertyRental()
            .setBedroom(bedroom)
            .setBathroom(bathroom)
            .setCarpetArea(carpetArea)
            .setRentalType(rentalType)
            .setRentalAvailableDay(rentalAvailableDay)
            .setHousingAgentQID(housingAgentQID)
            .setHousingAgentReview(housingAgentReview);

        jsonPropertyRental.setId(id)
            .setQueueUserId(getQueueUserId())
            .setBusinessType(getBusinessType())
            .setCoordinate(getCoordinate())
            .setProductPrice(getProductPrice())
            .setTitle(getTitle())
            .setDescription(getDescription())
            .setPostImages(getPostImages())
            .setTags(getTags())
            .setViewCount(getViewCount())
            .setAddress(getAddress())
            .setCity(getCity())
            .setTown(getTown())
            .setCountryShortName(getCountryShortName())
            .setLandmark(getLandmark())
            .setPublishUntil(getPublishUntil());

        return jsonPropertyRental;
    }
}
