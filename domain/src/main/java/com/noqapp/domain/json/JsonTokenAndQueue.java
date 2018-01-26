package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.*;
import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueStatusEnum;
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

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty ("d")
    private String displayName;

    @JsonProperty ("sa")
    private String storeAddress;

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

    public JsonTokenAndQueue(int token, QueueStatusEnum queueStatus, JsonQueue jsonQueue) {
        this.codeQR = jsonQueue.getCodeQR();
        this.businessName = jsonQueue.getBusinessName();
        this.displayName = jsonQueue.getDisplayName();
        this.storeAddress = jsonQueue.getStoreAddress();
        this.countryShortName = jsonQueue.getCountryShortName();
        this.storePhone = jsonQueue.getStorePhone();
        this.businessType = jsonQueue.getBusinessType();
        this.tokenAvailableFrom = jsonQueue.getTokenAvailableFrom();
        this.startHour = jsonQueue.getStartHour();
        this.endHour = jsonQueue.getEndHour();
        this.topic = jsonQueue.getTopic();
        this.servingNumber = jsonQueue.getServingNumber();
        this.lastNumber = jsonQueue.getLastNumber();
        this.serviceEndTime = jsonQueue.getServiceEndTime();
        this.createDate = jsonQueue.getCreated();

        this.queueStatus = queueStatus;
        this.token = token;
    }

    public JsonTokenAndQueue(QueueEntity queue, BizStoreEntity bizStore) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(TimeZone.getTimeZone(bizStore.getTimeZone()).toZoneId());

        this.codeQR = queue.getCodeQR();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = queue.getDisplayName();
        this.storeAddress = bizStore.getAddress();
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

        this.token = queue.getTokenNumber();
    }

    @Override
    public String toString() {
        return "JsonTokenAndQueue{" +
                "codeQR='" + codeQR + '\'' +
                ", businessName='" + businessName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
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

    public String getCodeQR() {
        return codeQR;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStoreAddress() {
        return storeAddress;
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
}
