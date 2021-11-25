package com.noqapp.search.elastic.domain;

import static com.noqapp.common.utils.Constants.UNDER_SCORE;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.shared.GeoPointOfQ;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.search.elastic.config.ElasticsearchClientConfiguration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 11/14/21 3:56 PM
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
public class PurchaseOrderElastic extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderElastic.class);

    public static final String TYPE = "PURCHASE_ORDER".toLowerCase();
    public static final String INDEX = ElasticsearchClientConfiguration.INDEX + UNDER_SCORE + TYPE;

    @JsonIgnore
    private String id;

    @JsonProperty("QID")
    private String queueUserId;

    @JsonProperty("BS")
    private String bizStoreId;

    @JsonProperty("COR")
    private GeoPointOfQ geoPointOfQ;

    @JsonProperty("GH")
    private String geoHash;

    @JsonProperty("OP")
    private String orderPrice;

    @JsonProperty("BT")
    private BusinessTypeEnum businessType;

    @JsonProperty("AR")
    private String area;

    @JsonProperty("TO")
    private String town;

    @JsonProperty("DT")
    private String district;

    @JsonProperty("ST")
    private String state;

    public String getId() {
        return id;
    }

    public PurchaseOrderElastic setId(String id) {
        this.id = id;
        return this;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public PurchaseOrderElastic setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public PurchaseOrderElastic setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public GeoPointOfQ getGeoPointOfQ() {
        return geoPointOfQ;
    }

    public PurchaseOrderElastic setGeoPointOfQ(GeoPointOfQ geoPointOfQ) {
        this.geoPointOfQ = geoPointOfQ;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public PurchaseOrderElastic setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public PurchaseOrderElastic setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public PurchaseOrderElastic setBusinessType(BusinessTypeEnum businessType) {
        this.businessType = businessType;
        return this;
    }

    public String getArea() {
        return area;
    }

    public PurchaseOrderElastic setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTown() {
        return town;
    }

    public PurchaseOrderElastic setTown(String town) {
        this.town = town;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public PurchaseOrderElastic setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getState() {
        return state;
    }

    public PurchaseOrderElastic setState(String state) {
        this.state = state;
        return this;
    }
}
