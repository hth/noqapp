package com.noqapp.domain.helper;

import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;

/**
 * hitender
 * 7/10/20 6:15 PM
 */
public class ExpectedHandlingTime {

    private Duration duration;
    private long averageServiceTime;
    private boolean closed;

    public Duration getDuration() {
        return duration;
    }

    public ExpectedHandlingTime setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public ExpectedHandlingTime setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public String getHours() {
        return String.valueOf(duration.toHours());
    }

    public String getMinutes() {
        return String.valueOf(duration.minusHours(duration.toHours()).toMinutes());
    }

    public boolean isClosed() {
        return closed;
    }

    public ExpectedHandlingTime setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }

    @Transient
    public String getAverageServiceTimeFormatted() {
        String time;
        if (averageServiceTime <= 0) {
            time = "N/A";
        } else {
            long seconds = averageServiceTime * 60;
            if (seconds > 60) {
                time = new BigDecimal(averageServiceTime).divide(new BigDecimal(60_000), MathContext.DECIMAL64).setScale(2, RoundingMode.CEILING) + " min(s)";
            } else {
                time = new BigDecimal(averageServiceTime).divide(new BigDecimal(1000), MathContext.DECIMAL64) + " sec(s)";
            }
        }

        return time;
    }
}
