package com.noqapp.search.elastic.domain;

import com.noqapp.common.utils.AbstractDomain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * hitender
 * 4/6/18 12:38 PM
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
public class StoreHourElastic extends AbstractDomain {

    @JsonProperty("DW")
    private int dayOfWeek;

    @JsonProperty("TF")
    private int tokenAvailableFrom;

    @JsonProperty("SH")
    private int startHour;

    @JsonProperty("AS")
    private int appointmentStartHour;

    @JsonProperty("TE")
    private int tokenNotAvailableFrom;

    @JsonProperty("EH")
    private int endHour;

    @JsonProperty("AE")
    private int appointmentEndHour;

    @JsonProperty("DC")
    private boolean dayClosed = false;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public StoreHourElastic setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public StoreHourElastic setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public StoreHourElastic setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getAppointmentStartHour() {
        return appointmentStartHour;
    }

    public StoreHourElastic setAppointmentStartHour(int appointmentStartHour) {
        this.appointmentStartHour = appointmentStartHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public StoreHourElastic setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public StoreHourElastic setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public int getAppointmentEndHour() {
        return appointmentEndHour;
    }

    public StoreHourElastic setAppointmentEndHour(int appointmentEndHour) {
        this.appointmentEndHour = appointmentEndHour;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public StoreHourElastic setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    @Override
    public String toString() {
        return "StoreHourElastic{" +
            "dayOfWeek=" + dayOfWeek +
            ", tokenAvailableFrom=" + tokenAvailableFrom +
            ", startHour=" + startHour +
            ", tokenNotAvailableFrom=" + tokenNotAvailableFrom +
            ", endHour=" + endHour +
            ", dayClosed=" + dayClosed +
            '}';
    }
}
