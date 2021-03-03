package com.noqapp.domain.market;

import com.noqapp.common.utils.CommonUtil;
import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.BaseEntity;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * hitender
 * 12/30/20 3:02 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public abstract class MarketplaceEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceEntity.class);

    @Field("QID")
    private String queueUserId;

    @Field("BT")
    private BusinessTypeEnum businessType;

    /* Format Longitude and then Latitude. */
    @Field("COR")
    private double[] coordinate;

    @Field("PP")
    private int productPrice;

    @Field("TI")
    private String title;

    @Field("DS")
    private String description;

    @Field("PI")
    private Set<String> postImages = new LinkedHashSet<>();

    @Field("TG")
    private String tags;

    @Field("LC")
    private int likeCount;

    @Field("EC")
    private int expressedInterestCount;

    /** Marketplace location. */
    @Field("MA")
    private String address;

    @Field("MC")
    private String city;

    @Field("TO")
    private String town;

    @Field("CS")
    private String countryShortName;

    @Field("LM")
    private String landmark;

    @Field("PU")
    private Date publishUntil = DateUtil.plusDays(10);

    @Field ("VB")
    private String validateByQid;

    @Field("IP")
    private String ipAddress;

    public String getQueueUserId() {
        return queueUserId;
    }

    public MarketplaceEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MarketplaceEntity setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public MarketplaceEntity setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public MarketplaceEntity setProductPrice(int productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MarketplaceEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MarketplaceEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getPostImages() {
        return postImages;
    }

    public MarketplaceEntity setPostImages(Set<String> postImages) {
        this.postImages = postImages;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public MarketplaceEntity setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public MarketplaceEntity setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public int getExpressedInterestCount() {
        return expressedInterestCount;
    }

    public MarketplaceEntity setExpressedInterestCount(int expressedInterestCount) {
        this.expressedInterestCount = expressedInterestCount;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MarketplaceEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public MarketplaceEntity setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTown() {
        return town;
    }

    public MarketplaceEntity setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public MarketplaceEntity setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getLandmark() {
        return landmark;
    }

    public MarketplaceEntity setLandmark(String landmark) {
        this.landmark = landmark;
        return this;
    }

    public Date getPublishUntil() {
        return publishUntil;
    }

    public MarketplaceEntity setPublishUntil(Date publishUntil) {
        this.publishUntil = publishUntil;
        return this;
    }

    public String getValidateByQid() {
        return validateByQid;
    }

    public MarketplaceEntity setValidateByQid(String validateByQid) {
        this.validateByQid = validateByQid;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public MarketplaceEntity setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    @Transient
    public String getPriceForDisplay() {
        return CommonUtil.displayWithCurrencyCode(productPrice, countryShortName);
    }

    @Transient
    public GeoPointOfQ getGeoPointOfQ() {
        /* Latitude and then Longitude. */
        return new GeoPointOfQ(coordinate[1], coordinate[0]);
    }

    @Transient
    abstract public String getFieldValueForTag();
}
