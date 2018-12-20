package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * hitender
 * 2018-12-20 10:37
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "STATS_VIGYAAPAN_STORE_DAILY")
@CompoundIndexes(value = {
    /* Business name with address and phone makes it a unique store. */
    @CompoundIndex(name = "stats_vig_store_daily_bs_bn_idx", def = "{'BS': 1, 'BN': 1, 'DW': 1}", unique = false),
    @CompoundIndex (name = "stats_vig_store_daily_qr_idx", def = "{'QR': -1}", unique = false, background = true)
})
public class StatsVigyaapanStoreDailyEntity extends BaseEntity {

    @NotNull
    @Field("BS")
    private String bizStoreId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field("QR")
    private String codeQR;

    @Field("TD")
    private int timesDisplayed;

    @Field ("DW")
    private int dayOfWeek;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public StatsVigyaapanStoreDailyEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public StatsVigyaapanStoreDailyEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public StatsVigyaapanStoreDailyEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getTimesDisplayed() {
        return timesDisplayed;
    }

    public StatsVigyaapanStoreDailyEntity setTimesDisplayed(int timesDisplayed) {
        this.timesDisplayed = timesDisplayed;
        return this;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public StatsVigyaapanStoreDailyEntity setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }
}
