package com.token.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.token.domain.AbstractDomain;

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

    @JsonProperty ("q")
    private boolean closeQueue;

    @JsonProperty ("t")
    private int token;

    @JsonProperty ("a")
    private boolean active;

    public JsonTokenAndQueue(int token, boolean active, JsonQueue jsonQueue) {
        this.codeQR = jsonQueue.getCodeQR();
        this.businessName = jsonQueue.getBusinessName();
        this.displayName = jsonQueue.getDisplayName();
        this.storeAddress = jsonQueue.getStoreAddress();
        this.storePhone = jsonQueue.getStorePhone();
        this.tokenAvailableFrom = jsonQueue.getTokenAvailableFrom();
        this.startHour = jsonQueue.getStartHour();
        this.endHour = jsonQueue.getEndHour();
        this.topic = jsonQueue.getTopic();
        this.servingNumber = jsonQueue.getServingNumber();
        this.lastNumber = jsonQueue.getLastNumber();
        this.closeQueue = jsonQueue.isCloseQueue();
        this.token = token;
        this.active = active;
    }
}
