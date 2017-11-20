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
    private boolean dayClosed = false;

    public BusinessHour(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartHourStore() {
        return startHourStore;
    }

    public void setStartHourStore(int startHourStore) {
        this.startHourStore = startHourStore;
    }

    public int getEndHourStore() {
        return endHourStore;
    }

    public void setEndHourStore(int endHourStore) {
        this.endHourStore = endHourStore;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public void setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public void setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public void setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
    }
    
    /**
     * Used for displaying store hours on JSP.
     *
     * @return
     */
    @Transient
    public String getStartHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(startHourStore);
    }

    /**
     * Used for displaying store hours on JSP.
     *
     * @return
     */
    @Transient
    public String getEndHourStoreAsString() {
        return Formatter.convertMilitaryTo12HourFormat(endHourStore);
    }


    /**
     * Used for displaying store hours on JSP.
     *
     * @return
     */
    @Transient
    public String getTokenAvailableFromAsString() {
        return Formatter.convertMilitaryTo12HourFormat(tokenAvailableFrom);
    }

    /**
     * Used for displaying store hours on JSP.
     *
     * @return
     */
    @Transient
    public String getTokenNotAvailableFromAsString() {
        return Formatter.convertMilitaryTo12HourFormat(tokenNotAvailableFrom);
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
