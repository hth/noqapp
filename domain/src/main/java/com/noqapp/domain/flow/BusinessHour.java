package com.noqapp.domain.flow;

import com.noqapp.common.utils.Formatter;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.DayOfWeek;

/**
 * User: hitender
 * Date: 6/9/17 11:35 PM
 */
public class BusinessHour implements Serializable {
    private DayOfWeek dayOfWeek;
    private int startHourStore;
    private int endHourStore;
    private int tokenAvailableFrom;
    private int tokenNotAvailableFrom;
    private int lunchTimeStart;
    private int lunchTimeEnd;
    private int appointmentStartHour;
    private int appointmentEndHour;
    private boolean dayClosed = false;

    public BusinessHour(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public BusinessHour setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public int getStartHourStore() {
        return startHourStore;
    }

    public BusinessHour setStartHourStore(int startHourStore) {
        this.startHourStore = startHourStore;
        return this;
    }

    public int getEndHourStore() {
        return endHourStore;
    }

    public BusinessHour setEndHourStore(int endHourStore) {
        this.endHourStore = endHourStore;
        return this;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public BusinessHour setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public BusinessHour setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getLunchTimeStart() {
        return lunchTimeStart;
    }

    public BusinessHour setLunchTimeStart(int lunchTimeStart) {
        this.lunchTimeStart = lunchTimeStart;
        return this;
    }

    public int getLunchTimeEnd() {
        return lunchTimeEnd;
    }

    public BusinessHour setLunchTimeEnd(int lunchTimeEnd) {
        this.lunchTimeEnd = lunchTimeEnd;
        return this;
    }

    public int getAppointmentStartHour() {
        return appointmentStartHour;
    }

    public BusinessHour setAppointmentStartHour(int appointmentStartHour) {
        this.appointmentStartHour = appointmentStartHour;
        return this;
    }

    public int getAppointmentEndHour() {
        return appointmentEndHour;
    }

    public BusinessHour setAppointmentEndHour(int appointmentEndHour) {
        this.appointmentEndHour = appointmentEndHour;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public void setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
    }
    
    /** Used for displaying store hours on JSP. */
    @Transient
    public String getStartHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(startHourStore);
    }

    /** Used for displaying store hours on JSP. */
    @Transient
    public String getEndHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(endHourStore);
    }

    @Transient
    public String getAppointmentStartHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(appointmentStartHour);
    }

    /** Used for displaying store hours on JSP. */
    @Transient
    public String getAppointmentEndHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(appointmentEndHour);
    }


    /** Used for displaying store hours on JSP. */
    @Transient
    public String getTokenAvailableFromAsString() {
        return Formatter.convertMilitaryTo12HourFormat(tokenAvailableFrom);
    }

    /** Used for displaying store hours on JSP. */
    @Transient
    public String getTokenNotAvailableFromAsString() {
        return Formatter.convertMilitaryTo12HourFormat(tokenNotAvailableFrom);
    }

    /** Used for displaying store hours on JSP. */
    @Transient
    public String getLunchTimeStartAsString() {
        if (lunchTimeStart == 0) {
            return "N/A";
        }
        return Formatter.convertMilitaryTo12HourFormat(lunchTimeStart);
    }

    /** Used for displaying store hours on JSP. */
    @Transient
    public String getLunchTimeEndAsString() {
        if (lunchTimeEnd == 0) {
            return "N/A";
        }
        return Formatter.convertMilitaryTo12HourFormat(lunchTimeEnd);
    }

    @Override
    public String toString() {
        return "BusinessHour{" +
                "dayOfWeek=" + dayOfWeek +
                ", startHourStore=" + startHourStore +
                ", endHourStore=" + endHourStore +
                ", tokenAvailableFrom=" + tokenAvailableFrom +
                ", tokenNotAvailableFrom=" + tokenNotAvailableFrom +
                ", dayClosed=" + dayClosed +
                '}';
    }
}
