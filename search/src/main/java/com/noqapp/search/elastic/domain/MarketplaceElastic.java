package com.noqapp.search.elastic.domain;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.common.utils.CommonUtil;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stored in elasticsearch.
 * <p>
 * hitender
 * 2/27/21 8:07 AM
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketplaceElastic extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceElastic.class);

    public static final String TYPE = "MARKETPLACE".toLowerCase();
    public static final String INDEX = ElasticsearchClientConfiguration.INDEX + UNDER_SCORE + TYPE;

    @JsonProperty("id")
    private String id;

    @JsonProperty("BT")
    private BusinessTypeEnum businessType;

    @JsonProperty("PP")
    private String productPrice;

    @JsonProperty("TI")
    private String title;

    @JsonProperty("DS")
    private String description;

    @JsonProperty("PI")
    private List<String> postImages = new ArrayList<>();

    /** Tags are going to be category under business type. Like Rent has category of Apartment, House. */
    @JsonProperty("TG")
    private String tag;

    @JsonProperty("VC")
    private int viewCount;

    @JsonProperty("RA")
    private String rating;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("MC")
    private String city;

    @JsonProperty("TO")
    private String town;

    @JsonProperty("CS")
    private String countryShortName;

    /** Mostly used for display as most of the common data is listed as text here. */
    @JsonProperty("TS")
    private String[] fieldTags;

    public String getId() {
        return id;
    }

    public MarketplaceElastic setId(String id) {
        this.id = id;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public MarketplaceElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public MarketplaceElastic setProductPrice(String productPrice) {
        this.productPrice = productPrice;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MarketplaceElastic setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MarketplaceElastic setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getPostImages() {
        return postImages;
    }

    public MarketplaceElastic setPostImages(List<String> postImages) {
        this.postImages = postImages;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public MarketplaceElastic setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public int getViewCount() {
        return viewCount;
    }

    public MarketplaceElastic setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public String getRating() {
        return rating;
    }

    public MarketplaceElastic setRating(String rating) {
        this.rating = rating;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public MarketplaceElastic setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public MarketplaceElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getCity() {
        return city;
    }

    public MarketplaceElastic setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTown() {
        return town;
    }

    public MarketplaceElastic setTown(String town) {
        this.town = town;
        return this;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public MarketplaceElastic setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
        return this;
    }

    public String[] getFieldTags() {
        return fieldTags;
    }

    public MarketplaceElastic setFieldTags(String[] fieldTags) {
        this.fieldTags = fieldTags;
        return this;
    }
}
