package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

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

    @JsonProperty ("c")
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

    @JsonProperty ("q")
    private QueueStatusEnum queueStatus;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("hr")
    private int hoursSaved;

    @JsonProperty ("u")
    private String createDate;

    public JsonTokenAndQueue() {
        //Required default constructor
    }

    /* For Active Queue. */
    public JsonTokenAndQueue(int token, QueueStatusEnum queueStatus, JsonQueue jsonQueue) {
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
        this.servingNumber = jsonQueue.getServingNumber();
        this.lastNumber = jsonQueue.getLastNumber();
        //Skipped ratingCount
        //Skipped hoursSaved
        this.serviceEndTime = jsonQueue.getServiceEndTime();
        this.createDate = jsonQueue.getCreated();

        this.queueStatus = queueStatus;
        this.token = token;
    }

    public JsonTokenAndQueue(QueueEntity queue, BizStoreEntity bizStore) {
        String bannerImage;
        switch (bizStore.getBusinessType()) {
            case DO:
                bannerImage = bizStore.getBizName().getBusinessServiceImages().isEmpty() ? null : bizStore.getBizName().getBusinessServiceImages().iterator().next();
                break;
            default:
                bannerImage = bizStore.getStoreServiceImages().isEmpty() ? null : bizStore.getStoreServiceImages().iterator().next();
                if (StringUtils.isBlank(bannerImage)) {
                    bannerImage = bizStore.getBizName().getBusinessServiceImages().isEmpty() ? null : bizStore.getBizName().getBusinessServiceImages().iterator().next();
                }
        }

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
        //Skipped serving number
        //Skipped last number
        this.serviceEndTime = DateFormatUtils.format(queue.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.ratingCount = queue.getRatingCount();
        this.hoursSaved = queue.getHoursSaved();
        this.createDate = DateFormatUtils.format(queue.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        //Skipped queueStatus
        this.token = queue.getTokenNumber();
    }

    /* For Active Order. */
    public JsonTokenAndQueue(PurchaseOrderEntity queue, BizStoreEntity bizStore) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        this.codeQR = queue.getCodeQR();
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
        this.servingNumber = 0;
        this.lastNumber = 0;
        //Skipped ratingCount
        //Skipped hoursSaved
        this.serviceEndTime = null == queue.getServiceEndTime() ? "NA" : DateFormatUtils.format(queue.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.createDate = DateFormatUtils.format(queue.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        //TODO remove queueStatus
        this.queueStatus = QueueStatusEnum.S;
        this.token = queue.getTokenNumber();
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

    @Override
    public String toString() {
        return "JsonTokenAndQueue{" +
                "codeQR='" + codeQR + '\'' +
                ", geoHash='" + geoHash + '\'' +
                ", businessName='" + businessName + '\'' +
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
                ", queueStatus=" + queueStatus +
                ", serviceEndTime='" + serviceEndTime + '\'' +
                ", ratingCount=" + ratingCount +
                ", hoursSaved=" + hoursSaved +
                ", createDate='" + createDate + '\'' +
                '}';
    }

}
