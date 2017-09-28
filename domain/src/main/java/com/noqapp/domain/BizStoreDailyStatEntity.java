package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
@Document (collection = "BIZ_STORE_DAILY_STAT")
@CompoundIndexes (value = {
        /* Business name with address and phone makes it a unique store. */
        @CompoundIndex (name = "biz_store_daily_stat_bs_bn_idx", def = "{'BS': 1, 'BN': 1}", unique = false),
        @CompoundIndex (name = "biz_store_daily_stat_qr_idx", def = "{'QR': -1}", unique = false, background = true)
})
public class BizStoreDailyStatEntity extends BaseEntity {

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

    @NotNull
    @Field ("TC")
    private int totalClient;

    @NotNull
    @Field ("ST")
    private long totalServiceTime;

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

    public String getBizStoreId() {
        return bizStoreId;
    }

    public BizStoreDailyStatEntity setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public BizStoreDailyStatEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public BizStoreDailyStatEntity setCodeQR(String codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public int getTotalServiced() {
        return totalServiced;
    }

    public BizStoreDailyStatEntity setTotalServiced(int totalServiced) {
        this.totalServiced = totalServiced;
        return this;
    }

    public int getTotalNoShow() {
        return totalNoShow;
    }

    public BizStoreDailyStatEntity setTotalNoShow(int totalNoShow) {
        this.totalNoShow = totalNoShow;
        return this;
    }

    public int getTotalAbort() {
        return totalAbort;
    }

    public BizStoreDailyStatEntity setTotalAbort(int totalAbort) {
        this.totalAbort = totalAbort;
        return this;
    }

    public int getTotalClient() {
        return totalClient;
    }

    public BizStoreDailyStatEntity setTotalClient(int totalClient) {
        this.totalClient = totalClient;
        return this;
    }

    public long getTotalServiceTime() {
        return totalServiceTime;
    }

    public BizStoreDailyStatEntity setTotalServiceTime(long totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
        return this;
    }

    public long getAverageServiceTime() {
        return averageServiceTime;
    }

    public BizStoreDailyStatEntity setAverageServiceTime(long averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
        return this;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public BizStoreDailyStatEntity setTotalRating(int totalRating) {
        this.totalRating = totalRating;
        return this;
    }

    public int getTotalCustomerRated() {
        return totalCustomerRated;
    }

    public BizStoreDailyStatEntity setTotalCustomerRated(int totalCustomerRated) {
        this.totalCustomerRated = totalCustomerRated;
        return this;
    }

    public long getTotalHoursSaved() {
        return totalHoursSaved;
    }

    public BizStoreDailyStatEntity setTotalHoursSaved(long totalHoursSaved) {
        this.totalHoursSaved = totalHoursSaved;
        return this;
    }

    @Override
    public String toString() {
        return "BizStoreDailyStatEntity{" +
                "bizStoreId='" + bizStoreId + '\'' +
                ", bizNameId='" + bizNameId + '\'' +
                ", codeQR='" + codeQR + '\'' +
                ", totalServiced=" + totalServiced +
                ", totalNoShow=" + totalNoShow +
                ", totalAbort=" + totalAbort +
                ", totalClient=" + totalClient +
                ", totalServiceTime=" + totalServiceTime +
                ", averageServiceTime=" + averageServiceTime +
                ", totalRating=" + totalRating +
                ", totalCustomerRated=" + totalCustomerRated +
                ", totalHoursSaved=" + totalHoursSaved +
                '}';
    }
}
