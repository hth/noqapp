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

    @Override
    @Transient
    public String getFieldValueForTag() {
        return bedroom + UNDER_SCORE + "BE" + " "
            + bathroom + UNDER_SCORE + "BR" + " "
            + carpetArea + UNDER_SCORE + "CA" + " "
            + rentalType.name() + UNDER_SCORE + "RT"
            + (StringUtils.isNotBlank(getTags()) ? " " + getTags() : "");
    }

    @Override
    @Transient
    public String[] getFieldTags() {
        return new String[] {
            "Bedroom " + bedroom,
            "Bathroom " + bathroom,
            "Carpet Area " + carpetArea + " sq ft",
            "Rent " + rentalType.getDescription()
        };
    }
}
