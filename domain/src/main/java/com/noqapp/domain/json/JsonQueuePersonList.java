package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 9/7/17 7:54 AM
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
public class JsonQueuePersonList extends AbstractDomain {

    @JsonProperty("qps")
    private List<JsonQueuedPerson> queuedPeople = new ArrayList<>();

    @JsonProperty("ac")
    private long appointmentCountForToday = 0;

    public List<JsonQueuedPerson> getQueuedPeople() {
        return queuedPeople;
    }

    public JsonQueuePersonList setQueuedPeople(List<JsonQueuedPerson> queuedPeople) {
        this.queuedPeople = queuedPeople;
        return this;
    }

    public long getAppointmentCountForToday() {
        return appointmentCountForToday;
    }

    public JsonQueuePersonList setAppointmentCountForToday(long appointmentCountForToday) {
        this.appointmentCountForToday = appointmentCountForToday;
        return this;
    }
}
