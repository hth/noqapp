package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.helper.CommonHelper;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PurchaseOrderStateEnum;
import com.noqapp.domain.types.QueueStatusEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 2/27/17 12:14 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
@JsonInclude (JsonInclude.Include.NON_NULL)
public class JsonTokenAndQueue extends AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(JsonTokenAndQueue.class);

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty("gh")
    private String geoHash;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty("di")
    private String displayImage;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty ("p")
    private String storePhone;

    @JsonProperty("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("f")
    private int tokenAvailableFrom;

    /* Store business start hour. */
    @JsonProperty ("b")
    private int startHour;

    /* Store business end hour. */
    @JsonProperty ("e")
    private int endHour;

    @JsonProperty ("o")
    private String topic;

    @JsonProperty ("s")
    private int servingNumber;

    @JsonProperty ("l")
    private int lastNumber;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("os")
    private PurchaseOrderStateEnum purchaseOrderState;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("hr")
    private int hoursSaved;

    @JsonProperty ("u")
    private String createDate;

    @JsonProperty ("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    public JsonTokenAndQueue() {
        //Required default constructor
    }

    /* For Active Queue. */
    public JsonTokenAndQueue(int token, String qid, QueueStatusEnum queueStatus, JsonQueue jsonQueue, JsonPurchaseOrder jsonPurchaseOrder) {
        this.codeQR = jsonQueue.getCodeQR();
        this.geoHash = jsonQueue.getGeoHash();
        this.businessName = jsonQueue.getBusinessName();
        //this.displayImage = jsonQueue.getStoreServiceImages().isEmpty() ? "" : jsonQueue.getStoreServiceImages().iterator().next();
        this.displayImage = ""; //TODO(hth) replace later
        this.displayName = jsonQueue.getDisplayName();
        this.storeAddress = jsonQueue.getStoreAddress();
        this.area = jsonQueue.getArea();
        this.town = jsonQueue.getTown();
        this.countryShortName = jsonQueue.getCountryShortName();
        this.storePhone = jsonQueue.getStorePhone();
        this.businessType = jsonQueue.getBusinessType();
        this.tokenAvailableFrom = jsonQueue.getTokenAvailableFrom();
        this.startHour = jsonQueue.getStartHour();
        this.endHour = jsonQueue.getEndHour();
        this.topic = jsonQueue.getTopic();
        this.queueUserId = qid;
        this.servingNumber = jsonQueue.getServingNumber();
        this.lastNumber = jsonQueue.getLastNumber();
        //Skipped ratingCount
        //Skipped hoursSaved
        this.serviceEndTime = jsonQueue.getServiceEndTime();
        this.createDate = jsonQueue.getCreated();

        this.queueStatus = queueStatus;
        this.token = token;
        //Keeping purchaseOrderState for sake of Mobile DB as it does not accepts null or blank
        this.purchaseOrderState = jsonPurchaseOrder == null ? PurchaseOrderStateEnum.IN : jsonPurchaseOrder.getPresentOrderState();
        this.jsonPurchaseOrder = jsonPurchaseOrder;
    }

    /* For Historical Queues. */
    public JsonTokenAndQueue(QueueEntity queue, BizStoreEntity bizStore, JsonPurchaseOrder jsonPurchaseOrder) {
        String bannerImage = CommonHelper.getBannerImage(bizStore);
        LOG.info("Banner for queue image={} bizStore name={}", bannerImage, bizStore.getDisplayName());

        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        this.codeQR = queue.getCodeQR();
        this.geoHash = bizStore.getGeoPoint().getGeohash();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = bizStore.getDisplayName();
        this.displayImage = bannerImage;
        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
        this.storePhone = bizStore.getPhone();
        this.businessType = bizStore.getBusinessType();
        this.tokenAvailableFrom = bizStore.getTokenAvailableFrom(zonedDateTime.getDayOfWeek());
        this.startHour = bizStore.getStartHour(zonedDateTime.getDayOfWeek());
        this.endHour = bizStore.getEndHour(zonedDateTime.getDayOfWeek());
        this.topic = bizStore.getTopic();
        this.queueUserId = queue.getQueueUserId();
        //Skipped serving number
        //Skipped last number
        this.serviceEndTime = DateFormatUtils.format(queue.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.ratingCount = queue.getRatingCount();
        this.hoursSaved = queue.getHoursSaved();
        this.createDate = DateFormatUtils.format(queue.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        //Skipped queueStatus
        this.token = queue.getTokenNumber();
        //Keeping purchaseOrderState for sake of Mobile DB as it does not accepts null or blank
        this.purchaseOrderState = jsonPurchaseOrder == null ? PurchaseOrderStateEnum.IN : jsonPurchaseOrder.getPresentOrderState();
        this.jsonPurchaseOrder = jsonPurchaseOrder;
    }

    /* For Active Order. */
    public JsonTokenAndQueue(PurchaseOrderEntity purchaseOrder, TokenQueueEntity tokenQueue, BizStoreEntity bizStore) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        this.codeQR = purchaseOrder.getCodeQR();
        this.geoHash = bizStore.getGeoPoint().getGeohash();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = bizStore.getDisplayName();
        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
        this.storePhone = bizStore.getPhone();
        this.businessType = bizStore.getBusinessType();
        this.tokenAvailableFrom = bizStore.getTokenAvailableFrom(zonedDateTime.getDayOfWeek());
        this.startHour = bizStore.getStartHour(zonedDateTime.getDayOfWeek());
        this.endHour = bizStore.getEndHour(zonedDateTime.getDayOfWeek());
        this.topic = bizStore.getTopic();
        this.queueUserId = purchaseOrder.getQueueUserId();
        this.servingNumber = tokenQueue.getCurrentlyServing();
        this.lastNumber = tokenQueue.getLastNumber();
        //Skipped ratingCount
        //Skipped hoursSaved
        this.serviceEndTime = null == purchaseOrder.getServiceEndTime() ? "N/A" : DateFormatUtils.format(purchaseOrder.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.createDate = DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        //Keeping queueStatus for sake of Mobile DB as it does not accepts null or blank
        this.queueStatus = QueueStatusEnum.S;
        this.token = purchaseOrder.getTokenNumber();
        this.purchaseOrderState = purchaseOrder.getPresentOrderState();
        this.jsonPurchaseOrder = null;
    }

    /* For Historical Orders. */
    public JsonTokenAndQueue(PurchaseOrderEntity purchaseOrder, BizStoreEntity bizStore) {
        String bannerImage = CommonHelper.getBannerImage(bizStore);
        LOG.info("For purchase order={} store={} bannerImage={}", purchaseOrder.getId(), bizStore.getId(), bannerImage);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        this.codeQR = purchaseOrder.getCodeQR();
        this.geoHash = bizStore.getGeoPoint().getGeohash();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = bizStore.getDisplayName();
        this.displayImage = bannerImage;
        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
        this.storePhone = bizStore.getPhone();
        this.businessType = bizStore.getBusinessType();
        this.tokenAvailableFrom = bizStore.getTokenAvailableFrom(zonedDateTime.getDayOfWeek());
        this.startHour = bizStore.getStartHour(zonedDateTime.getDayOfWeek());
        this.endHour = bizStore.getEndHour(zonedDateTime.getDayOfWeek());
        this.topic = bizStore.getTopic();
        this.queueUserId = purchaseOrder.getQueueUserId();
        //Skipped serving number
        //Skipped last number
        this.serviceEndTime = null == purchaseOrder.getServiceEndTime() ? "N/A" : DateFormatUtils.format(purchaseOrder.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.ratingCount = purchaseOrder.getRatingCount();
        //Skipped hoursSaved
        this.createDate = DateFormatUtils.format(purchaseOrder.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        //Keeping queueStatus for sake of Mobile DB as it does not accepts null or blank
        this.queueStatus = QueueStatusEnum.S;
        this.token = purchaseOrder.getTokenNumber();
        this.purchaseOrderState = purchaseOrder.getPresentOrderState();
        this.jsonPurchaseOrder = null;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public BusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public String getTopic() {
        return topic;
    }

    public int getServingNumber() {
        return servingNumber;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public int getToken() {
        return token;
    }

    public QueueStatusEnum getQueueStatus() {
        return queueStatus;
    }

    public PurchaseOrderStateEnum getPurchaseOrderState() {
        return purchaseOrderState;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getHoursSaved() {
        return hoursSaved;
    }

    public String getCreateDate() {
        return createDate;
    }

    public JsonPurchaseOrder getJsonPurchaseOrder() {
        return jsonPurchaseOrder;
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueue{" +
            "codeQR='" + codeQR + '\'' +
            ", geoHash='" + geoHash + '\'' +
            ", businessName='" + businessName + '\'' +
            ", displayImage='" + displayImage + '\'' +
            ", displayName='" + displayName + '\'' +
            ", storeAddress='" + storeAddress + '\'' +
            ", area='" + area + '\'' +
            ", town='" + town + '\'' +
            ", countryShortName='" + countryShortName + '\'' +
            ", storePhone='" + storePhone + '\'' +
            ", businessType=" + businessType +
            ", tokenAvailableFrom=" + tokenAvailableFrom +
            ", startHour=" + startHour +
            ", endHour=" + endHour +
            ", topic='" + topic + '\'' +
            ", servingNumber=" + servingNumber +
            ", lastNumber=" + lastNumber +
            ", token=" + token +
            ", queueUserId='" + queueUserId + '\'' +
            ", queueStatus=" + queueStatus +
            ", purchaseOrderState=" + purchaseOrderState +
            ", serviceEndTime='" + serviceEndTime + '\'' +
            ", ratingCount=" + ratingCount +
            ", hoursSaved=" + hoursSaved +
            ", createDate='" + createDate + '\'' +
            ", jsonPurchaseOrder=" + jsonPurchaseOrder +
            '}';
    }
}
