package com.noqapp.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 6/15/17 10:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "STATS_BIZ_STORE_DAILY")
@CompoundIndexes (value = {
        /* Business name with address and phone makes it a unique store. */
        @CompoundIndex (name = "stats_biz_store_daily_bs_bn_idx", def = "{'BS': 1, 'BN': 1}", unique = false),
        @CompoundIndex (name = "stats_biz_store_daily_qr_idx", def = "{'QR': -1}", unique = false, background = true)
})
public class StatsBizStoreDailyEntity extends BaseEntity {

    @NotNull
    @Field ("BS")
    private String bizStoreId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field("QR")
    private String codeQR;

    @Field("TS")
    private int totalServiced;

    @Field("TN")
    private int totalNoShow;

    @Field("TA")
    private int totalAbort;

    /* Is sum of totalServiced, totalNoShow, totalAbort. */
    @NotNull
    @Field ("TC")
    private int totalClient;

    @NotNull
    @Field ("VS")
    private int clientsPreviouslyVisitedThisStore;

    @NotNull
    @Field ("VB")
    private int clientsPreviouslyVisitedThisBusiness;

    /* Time saved as Milli Seconds. */
    @NotNull
    @Field ("ST")
    private long totalServiceTime;

    /* Time saved as Milli Seconds. */
    @Field("AS")
    private long averageServiceTime;

    @NotNull
    @Field ("TR")
    private int totalRating;

    @NotNull
    @Field ("CR")
    private int totalCustomerRated;

    @NotNull
    @Field ("TH")
    private long totalHoursSaved;

    @Field ("FS")
    private String firstServicedOrSkipped;

    @Field ("LS")
    private String lastServicedOrSkipped;

    /* Temp field used only for mongo aggregation framework. */
    @Field("MN")
    private int monthOfYear;

    /* Temp field used only for mongo aggregation framework. */
    @Field("YY")
    private int year;

    public String getBizStoreId() {
        return bizStoreId;
    }

    public StatsBizStoreDailyEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public StatsBizStoreDailyEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public StatsBizStoreDailyEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getTotalServiced() {
        return totalServiced;
    }

    public StatsBizStoreDailyEntity setTotalServiced(int totalServiced) {
        this.totalServiced = totalServiced;
        return this;
    }

    public int getTotalNoShow() {
        return totalNoShow;
    }

    public StatsBizStoreDailyEntity setTotalNoShow(int totalNoShow) {
        this.totalNoShow = totalNoShow;
        return this;
    }

    public int getTotalAbort() {
        return totalAbort;
    }

    public StatsBizStoreDailyEntity setTotalAbort(int totalAbort) {
        this.totalAbort = totalAbort;
        return this;
    }

    public int getTotalClient() {
        return totalClient;
    }

    public StatsBizStoreDailyEntity setTotalClient(int totalClient) {
        this.totalClient = totalClient;
        return this;
    }

    public int getClientsPreviouslyVisitedThisStore() {
        return clientsPreviouslyVisitedThisStore;
    }

    public StatsBizStoreDailyEntity setClientsPreviouslyVisitedThisStore(int clientsPreviouslyVisitedThisStore) {
        this.clientsPreviouslyVisitedThisStore = clientsPreviouslyVisitedThisStore;
        return this;
    }

    public int getClientsPreviouslyVisitedThisBusiness() {
        return clientsPreviouslyVisitedThisBusiness;
    }

    public StatsBizStoreDailyEntity setClientsPreviouslyVisitedThisBusiness(int clientsPreviouslyVisitedThisBusiness) {
        this.clientsPreviouslyVisitedThisBusiness = clientsPreviouslyVisitedThisBusiness;
        return this;
    }

    public long getTotalServiceTime() {
        return totalServiceTime;
    }

    public StatsBizStoreDailyEntity setTotalServiceTime(long totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public StatsBizStoreDailyEntity setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public StatsBizStoreDailyEntity setTotalRating(int totalRating) {
        this.totalRating = totalRating;
        return this;
    }

    public int getTotalCustomerRated() {
        return totalCustomerRated;
    }

    public StatsBizStoreDailyEntity setTotalCustomerRated(int totalCustomerRated) {
        this.totalCustomerRated = totalCustomerRated;
        return this;
    }

    public long getTotalHoursSaved() {
        return totalHoursSaved;
    }

    public StatsBizStoreDailyEntity setTotalHoursSaved(long totalHoursSaved) {
        this.totalHoursSaved = totalHoursSaved;
        return this;
    }

    public String getFirstServicedOrSkipped() {
        return firstServicedOrSkipped;
    }

    public StatsBizStoreDailyEntity setFirstServicedOrSkipped(String firstServicedOrSkipped) {
        this.firstServicedOrSkipped = firstServicedOrSkipped;
        return this;
    }

    public String getLastServicedOrSkipped() {
        return lastServicedOrSkipped;
    }

    public StatsBizStoreDailyEntity setLastServicedOrSkipped(String lastServicedOrSkipped) {
        this.lastServicedOrSkipped = lastServicedOrSkipped;
        return this;
    }

    @Transient
    public int newClients() {
        return totalServiced - clientsPreviouslyVisitedThisStore;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public StatsBizStoreDailyEntity setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }

    public int getYear() {
        return year;
    }

    public StatsBizStoreDailyEntity setYear(int year) {
        this.year = year;
        return this;
    }

    @Override
    public String toString() {
        return "StatsBizStoreDailyEntity{" +
            "bizStoreId='" + bizStoreId + '\'' +
            ", bizNameId='" + bizNameId + '\'' +
            ", codeQR='" + codeQR + '\'' +
            ", totalServiced=" + totalServiced +
            ", totalNoShow=" + totalNoShow +
            ", totalAbort=" + totalAbort +
            ", totalClient=" + totalClient +
            ", clientsPreviouslyVisitedThisStore=" + clientsPreviouslyVisitedThisStore +
            ", clientsPreviouslyVisitedThisBusiness=" + clientsPreviouslyVisitedThisBusiness +
            ", totalServiceTime=" + totalServiceTime +
            ", averageServiceTime=" + averageServiceTime +
            ", totalRating=" + totalRating +
            ", totalCustomerRated=" + totalCustomerRated +
            ", totalHoursSaved=" + totalHoursSaved +
            ", monthOfYear=" + monthOfYear +
            ", year=" + year +
            '}';
    }
}
