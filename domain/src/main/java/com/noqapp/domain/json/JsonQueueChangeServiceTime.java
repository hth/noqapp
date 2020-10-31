package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.types.ServiceTimeChangeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 10/29/20 2:04 PM
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
public class JsonQueueChangeServiceTime extends AbstractDomain {

    @JsonProperty("t")
    private int token;

    @JsonProperty("osl")
    private String oldTimeSlotMessage;

    @JsonProperty("usl")
    private String updatedTimeSlotMessage;

    @JsonProperty("smu")
    private ServiceTimeChangeEnum serviceTimeChange;

    public int getToken() {
        return token;
    }

    public JsonQueueChangeServiceTime setToken(int token) {
        this.token = token;
        return this;
    }

    public String getOldTimeSlotMessage() {
        return oldTimeSlotMessage;
    }

    public JsonQueueChangeServiceTime setOldTimeSlotMessage(String oldTimeSlotMessage) {
        this.oldTimeSlotMessage = oldTimeSlotMessage;
        return this;
    }

    public String getUpdatedTimeSlotMessage() {
        return updatedTimeSlotMessage;
    }

    public JsonQueueChangeServiceTime setUpdatedTimeSlotMessage(String updatedTimeSlotMessage) {
        this.updatedTimeSlotMessage = updatedTimeSlotMessage;
        return this;
    }

    public ServiceTimeChangeEnum getServiceTimeChange() {
        return serviceTimeChange;
    }

    public JsonQueueChangeServiceTime setServiceTimeChange(ServiceTimeChangeEnum serviceTimeChange) {
        this.serviceTimeChange = serviceTimeChange;
        return this;
    }
}
