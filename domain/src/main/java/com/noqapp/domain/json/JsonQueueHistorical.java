package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.QueueUserStateEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.TimeZone;

/**
 * hitender
 * 10/5/18 9:40 AM
 */
@SuppressWarnings ({
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
public class JsonQueueHistorical extends AbstractDomain {

    @JsonProperty ("qr")
    private String codeQR;

    @JsonProperty ("qid")
    private String queueUserId;

    @JsonProperty ("tn")
    private int tokenNumber;

    @JsonProperty ("dt")
    private String displayToken;

    @JsonProperty ("dn")
    private String displayName;

    @JsonProperty ("bt")
    private BusinessTypeEnum businessType;

    @JsonProperty ("qs")
    private QueueUserStateEnum queueUserState;

    @JsonProperty ("ra")
    private int ratingCount;

    @JsonProperty ("hr")
    private int hoursSaved;

    @JsonProperty ("rv")
    private String review;

    @JsonProperty ("sn")
    private String serverName;

    @JsonProperty ("sb")
    private String serviceBeginTime;

    @JsonProperty ("se")
    private String serviceEndTime;

    @JsonProperty ("gq")
    private String guardianQid;

    @JsonProperty("u")
    private String created;

    @JsonProperty ("n")
    private String businessName;

    @JsonProperty("sa")
    private String storeAddress;

    @JsonProperty("ar")
    private String area;

    @JsonProperty("to")
    private String town;

    @JsonProperty("cs")
    private String countryShortName;

    @JsonProperty("di")
    private String displayImage;

    @JsonProperty("bci")
    private String bizCategoryId;

    @JsonProperty("bc")
    private String bizCategoryName;

    @JsonProperty ("po")
    private JsonPurchaseOrder jsonPurchaseOrder;

    public JsonQueueHistorical() {
        //Required default constructor
    }

    public JsonQueueHistorical(QueueEntity queue, BizStoreEntity bizStore, JsonPurchaseOrder jsonPurchaseOrder) {
        this.codeQR = queue.getCodeQR();
        this.queueUserId = queue.getQueueUserId();
        this.tokenNumber = queue.getTokenNumber();
        this.displayToken = queue.getDisplayToken();
        this.displayName = queue.getDisplayName();
        this.businessType = queue.getBusinessType();
        this.queueUserState = queue.getQueueUserState();
        this.ratingCount = queue.getRatingCount();
        this.hoursSaved = queue.getHoursSaved();
        this.review = queue.getReview();
        this.serverName = queue.getServerName();
        this.serviceBeginTime = queue.getServiceBeginTime() == null ? "" : DateFormatUtils.format(queue.getServiceBeginTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.serviceEndTime = queue.getServiceEndTime() == null ? "" : DateFormatUtils.format(queue.getServiceEndTime(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.guardianQid = queue.getGuardianQid();
        this.created = DateFormatUtils.format(queue.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        this.businessName = bizStore.getBizName().getBusinessName();
        this.storeAddress = bizStore.getAddress();
        this.area = bizStore.getArea();
        this.town = bizStore.getTown();
        this.countryShortName = bizStore.getCountryShortName();
        this.bizCategoryId = bizStore.getBizCategoryId();

        this.jsonPurchaseOrder = jsonPurchaseOrder;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public JsonQueueHistorical setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
        return this;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public JsonQueueHistorical setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
        return this;
    }
}
