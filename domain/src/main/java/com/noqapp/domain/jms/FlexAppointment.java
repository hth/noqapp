package com.noqapp.domain.jms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.gson.annotations.SerializedName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * hitender
 * 7/6/21 8:36 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlexAppointment implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(FlexAppointment.class);

    @SuppressWarnings({"unused"})
    @SerializedName("codeQR")
    private String codeQR;

    @SuppressWarnings({"unused"})
    @SerializedName("scheduleDate")
    private String scheduleDate;

    @SuppressWarnings({"unused"})
    @SerializedName("startTime")
    private int startTime;

    private FlexAppointment(String codeQR, String scheduleDate, int startTime) {
        this.codeQR = codeQR;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
    }

    public static FlexAppointment newInstance(String codeQR, String scheduleDate, int startTime) {
        return new FlexAppointment(codeQR, scheduleDate, startTime);
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public int getStartTime() {
        return startTime;
    }

    public String key() {
        return codeQR + ":" + scheduleDate + ":" + startTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FlexAppointment.class.getSimpleName() + "[", "]")
            .add("codeQR='" + codeQR + "'")
            .add("scheduleDate='" + scheduleDate + "'")
            .add("startTime='" + startTime + "'")
            .toString();
    }
}
