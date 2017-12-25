package com.noqapp.domain;

import com.noqapp.common.utils.Formatter;
import org.apache.commons.text.WordUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.DayOfWeek;

/**
 * User: hitender
 * Date: 6/9/17 5:43 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "STORE_HOUR")
@CompoundIndexes ({
        @CompoundIndex (name = "store_hour_idx", def = "{'BZ': 1, 'DW': 1}", unique = true)
})
public class StoreHourEntity extends BaseEntity {

    @Field ("BZ")
    private String bizStoreId;

    @Field ("DW")
    private int dayOfWeek;

    @Field ("TF")
    private int tokenAvailableFrom;

    @Field ("SH")
    private int startHour;

    @Field ("TE")
    private int tokenNotAvailableFrom;

    @Field ("EH")
    private int endHour;

    @Field ("PJ")
    private boolean preventJoining;

    @Field ("DC")
    private boolean dayClosed = false;

    /* TODO(hth) This includes temp day close and temp preventJoining. All this resets on next day. */
    /* When business delays the start in minutes. TODO(hth) to be implemented. */
    @Field ("DE")
    private int delayedInMinutes = 0;

    public StoreHourEntity() {
        //Default
    }

    public StoreHourEntity(String bizStoreId, int dayOfWeek) {
        this.bizStoreId = bizStoreId;
        this.dayOfWeek = dayOfWeek;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getTokenAvailableFrom() {
        return tokenAvailableFrom;
    }

    public StoreHourEntity setTokenAvailableFrom(int tokenAvailableFrom) {
        this.tokenAvailableFrom = tokenAvailableFrom;
        return this;
    }

    public int getStartHour() {
        return startHour;
    }

    public StoreHourEntity setStartHour(int startHour) {
        this.startHour = startHour;
        return this;
    }

    public int getTokenNotAvailableFrom() {
        return tokenNotAvailableFrom;
    }

    public StoreHourEntity setTokenNotAvailableFrom(int tokenNotAvailableFrom) {
        this.tokenNotAvailableFrom = tokenNotAvailableFrom;
        return this;
    }

    public int getEndHour() {
        return endHour;
    }

    public StoreHourEntity setEndHour(int endHour) {
        this.endHour = endHour;
        return this;
    }

    public boolean isPreventJoining() {
        return preventJoining;
    }

    public StoreHourEntity setPreventJoining(boolean preventJoining) {
        this.preventJoining = preventJoining;
        return this;
    }

    public boolean isDayClosed() {
        return dayClosed;
    }

    public StoreHourEntity setDayClosed(boolean dayClosed) {
        this.dayClosed = dayClosed;
        return this;
    }

    public int getDelayedInMinutes() {
        return delayedInMinutes;
    }

    public StoreHourEntity setDelayedInMinutes(int delayedInMinutes) {
        this.delayedInMinutes = delayedInMinutes;
        return this;
    }

    @Transient
    public int storeClosingHourOfDay() {
        return endHour / 100;
    }

    @Transient
    public int storeClosingMinuteOfDay() {
        return endHour % 100;
    }

    /**
     * Used for displaying store hours on JSP.
     *
     * @return
     */
    @Transient
    public String getStoreStartHourAsString() {
        return Formatter.convertMilitaryTo12HourFormat(startHour);
    }

    /**
     * Used for displaying store hours on JSP.
     * 
     * @return
     */
    @Transient
    public String getStoreEndHourAsString() {
        return Formatter.convertMilitaryTo12HourFormat(endHour);
    }

    /**
     * Used for displaying day of the week on JSP.
     *
     * @return
     */
    @Transient
    public String getDayOfTheWeekAsString() {
        return WordUtils.capitalizeFully(DayOfWeek.of(dayOfWeek).name());
    }

    @Override
    public String toString() {
        return "StoreHourEntity{" +
                "bizStoreId='" + bizStoreId + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", tokenAvailableFrom=" + tokenAvailableFrom +
                ", startHour=" + startHour +
                ", tokenNotAvailableFrom=" + tokenNotAvailableFrom +
                ", endHour=" + endHour +
                ", preventJoining=" + preventJoining +
                ", dayClosed=" + dayClosed +
                '}';
    }
}
