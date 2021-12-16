package com.noqapp.domain.json.marketplace;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.BusinessTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * hitender
 * 3/7/21 12:09 PM
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
public abstract class JsonMarketplace extends AbstractDomain {

    @JsonProperty("id")
    private String id;

    @JsonProperty("qid")
    private String queueUserId;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty("cor")
    private double[] coordinate;

    @JsonProperty("pp")
    private String productPrice;

    @JsonProperty("ti")
    private String title;

    @JsonProperty("ds")
    private String description;

    @JsonProperty("pi")
    private Set<String> postImages = new LinkedHashSet<>();

    @JsonProperty("tg")
    private String tags;

    @JsonProperty("vc")
    private int viewCount;

    /** Marketplace location. */
    @JsonProperty("ma")
    private String address;

    @JsonProperty("mc")
    private String city;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("lm")
    private String landmark;

    @JsonProperty("pu")
    private Date publishUntil;

    public String getId() {
        return id;
    }

    public JsonMarketplace setId(String id) {
        this.id = id;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public JsonMarketplace setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public JsonMarketplace setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public double[] getCoordinate() {
        return coordinate;
    }

    public JsonMarketplace setCoordinate(double[] coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public JsonMarketplace setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public JsonMarketplace setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JsonMarketplace setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getPostImages() {
        return postImages;
    }

    public JsonMarketplace setPostImages(Set<String> postImages) {
        this.postImages = postImages;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public JsonMarketplace setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public int getViewCount() {
        return viewCount;
    }

    public JsonMarketplace setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public JsonMarketplace setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public JsonMarketplace setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTown() {
        return town;
    }

    public JsonMarketplace setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public JsonMarketplace setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String getLandmark() {
        return landmark;
    }

    public JsonMarketplace setLandmark(String landmark) {
        this.landmark = landmark;
        return this;
    }

    public Date getPublishUntil() {
        return publishUntil;
    }

    public JsonMarketplace setPublishUntil(Date publishUntil) {
        this.publishUntil = publishUntil;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonMarketplace.class.getSimpleName() + "[", "]")
            .add("id='" + id + "'")
            .add("queueUserId='" + queueUserId + "'")
            .add("businessType=" + businessType)
            .add("coordinate=" + Arrays.toString(coordinate))
            .add("productPrice='" + productPrice + "'")
            .add("title='" + title + "'")
            .add("description='" + description + "'")
            .add("postImages=" + postImages)
            .add("tags='" + tags + "'")
            .add("viewCount=" + viewCount)
            .add("address='" + address + "'")
            .add("city='" + city + "'")
            .add("town='" + town + "'")
            .add("countryShortName='" + countryShortName + "'")
            .add("landmark='" + landmark + "'")
            .add("publishUntil=" + publishUntil)
            .toString();
    }
}
