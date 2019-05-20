package com.noqapp.domain;

import com.noqapp.domain.types.AdvertisementDisplayEnum;
import com.noqapp.domain.types.AdvertisementTypeEnum;
import com.noqapp.domain.types.ValidateStatusEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-05-16 13:33
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "ADVERTISEMENT")
@CompoundIndexes(value = {
    @CompoundIndex(name = "advertisement_biz_idx", def = "{'BN': 1}", unique = false),
    @CompoundIndex(name = "advertisement_biz_cor_cs_idx", def = "{'COR': '2d', 'BN': 1}"),
})
public class AdvertisementEntity extends BaseEntity {

    @Field ("BN")
    private String bizNameId;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    /* To show how much area to cover for advertisement. */
    @Field("RA")
    private int radius;

    @Field("TI")
    private String title;

    @Field("SD")
    private String shortDescription;

    @Field("IU")
    private List<String> imageUrls;

    @Field("TC")
    private List<String> termsAndConditions;

    @Field("AT")
    private AdvertisementTypeEnum advertisementType;

    @Field("AD")
    private AdvertisementDisplayEnum advertisementDisplay;

    @Field ("QID")
    private String queueUserId;

    @Field ("VB")
    private String validateByQid;

    @Field ("VS")
    private ValidateStatusEnum validateStatus;

    @Field("PD")
    private Date publishDate;

    @Field("ED")
    private Date endDate;

    /* When article is flagged by community. */
    @Field("FC")
    private int flagCount;

    public AdvertisementEntity() {
        //Default constructor, required to keep bean happy
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public AdvertisementEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public AdvertisementEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public int getRadius() {
        return radius;
    }

    public AdvertisementEntity setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AdvertisementEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public AdvertisementEntity setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public AdvertisementEntity setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public AdvertisementEntity addImageUrl(String imageUrl) {
        if(this.imageUrls == null) {
            this.imageUrls = new LinkedList<>();
            this.imageUrls.add(imageUrl);
        } else {
            this.imageUrls.add(imageUrl);
        }
        return this;
    }

    public List<String> getTermsAndConditions() {
        return termsAndConditions;
    }

    public AdvertisementEntity setTermsAndConditions(List<String> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
        return this;
    }

    public AdvertisementTypeEnum getAdvertisementType() {
        return advertisementType;
    }

    public AdvertisementEntity setAdvertisementType(AdvertisementTypeEnum advertisementType) {
        this.advertisementType = advertisementType;
        return this;
    }

    public AdvertisementDisplayEnum getAdvertisementDisplay() {
        return advertisementDisplay;
    }

    public AdvertisementEntity setAdvertisementDisplay(AdvertisementDisplayEnum advertisementDisplay) {
        this.advertisementDisplay = advertisementDisplay;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public AdvertisementEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public AdvertisementEntity setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public ValidateStatusEnum getValidateStatus() {
        return validateStatus;
    }

    public AdvertisementEntity setValidateStatus(ValidateStatusEnum validateStatus) {
        this.validateStatus = validateStatus;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public AdvertisementEntity setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public AdvertisementEntity setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getFlagCount() {
        return flagCount;
    }

    public AdvertisementEntity setFlagCount(int flagCount) {
        this.flagCount = flagCount;
        return this;
    }
}
