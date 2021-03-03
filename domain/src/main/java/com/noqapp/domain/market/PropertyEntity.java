package com.noqapp.domain.market;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.domain.types.catgeory.RentalTypeEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * All fields are added to tag. Example IC_itemCondition: IC_G, IC_P, IC_V.
 * hitender
 * 12/30/20 7:02 PM
 */ 
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "MP_PROPERTY")
public class PropertyEntity extends MarketplaceEntity {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyEntity.class);

    @Field("BE")
    private int bedroom;

    @Field("BR")
    private int bathroom;

    @Field("CA")
    private int carpetArea;

    @Field("RT")
    private RentalTypeEnum rentalType;

    public int getBedroom() {
        return bedroom;
    }

    public PropertyEntity setBedroom(int bedroom) {
        this.bedroom = bedroom;
        return this;
    }

    public int getBathroom() {
        return bathroom;
    }

    public PropertyEntity setBathroom(int bathroom) {
        this.bathroom = bathroom;
        return this;
    }

    public int getCarpetArea() {
        return carpetArea;
    }

    public PropertyEntity setCarpetArea(int carpetArea) {
        this.carpetArea = carpetArea;
        return this;
    }

    public RentalTypeEnum getRentalType() {
        return rentalType;
    }

    public PropertyEntity setRentalType(RentalTypeEnum rentalType) {
        this.rentalType = rentalType;
        return this;
    }

    @Transient
    public String getFieldValueForTag() {
        return "BE" + UNDER_SCORE + bedroom + " "
            + "BR" + UNDER_SCORE + bathroom + " "
            + "CA" + UNDER_SCORE + carpetArea + " "
            + "RT" + UNDER_SCORE + rentalType.name()
            + (StringUtils.isNotBlank(getTags()) ? " " + getTags() : "");
    }
}
