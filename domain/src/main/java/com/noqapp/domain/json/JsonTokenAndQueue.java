package com.noqapp.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.noqapp.domain.AbstractDomain;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.QueueEntity;
import com.noqapp.domain.types.QueueStatusEnum;

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

    @JsonProperty ("u")
    private String createDate;

    public JsonTokenAndQueue(int token, QueueStatusEnum queueStatus, JsonQueue jsonQueue) {
        this.codeQR = jsonQueue.getCodeQR();
        this.businessName = jsonQueue.getBusinessName();
        this.displayName = jsonQueue.getDisplayName();
        this.storeAddress = jsonQueue.getStoreAddress();
        this.countryShortName = jsonQueue.getCountryShortName();
        this.storePhone = jsonQueue.getStorePhone();
        this.tokenAvailableFrom = jsonQueue.getTokenAvailableFrom();
        this.startHour = jsonQueue.getStartHour();
        this.endHour = jsonQueue.getEndHour();
        this.topic = jsonQueue.getTopic();
        this.servingNumber = jsonQueue.getServingNumber();
        this.lastNumber = jsonQueue.getLastNumber();
        this.createDate = jsonQueue.getCreated();

        this.queueStatus = queueStatus;
        this.token = token;
    }

    public JsonTokenAndQueue(QueueEntity queue, BizStoreEntity bizStore) {
        this.codeQR = queue.getCodeQR();
        this.businessName = bizStore.getBizName().getBusinessName();
        this.displayName = queue.getDisplayName();
        this.storeAddress = bizStore.getAddress();
        this.countryShortName = bizStore.getCountryShortName();
        this.storePhone = bizStore.getPhone();
        this.tokenAvailableFrom = bizStore.getTokenAvailableFrom();
        this.startHour = bizStore.getStartHour();
        this.endHour = bizStore.getEndHour();
        this.topic = bizStore.getTopic();
        //Skipped serving number
        //Skipped last number
        this.createDate = DateFormatUtils.format(queue.getCreated(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));

        this.token = queue.getTokenNumber();
    }
}
