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
        @CompoundIndex (name = "biz_store_daily_stat_bs_bn_idx", def = "{'BS': 1, 'BN': 1}", unique = false)
})
public class BizStoreDailyStatEntity extends BaseEntity {

    @NotNull
    @Field ("BS")
    private String bizStoreId;

    @NotNull
    @Field ("BN")
    private String bizNameId;

    @NotNull
    @Field ("TS")
    private long totalServiceTime;

    @NotNull
    @Field ("CS")
    private int totalCustomerServed;

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

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public void setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
    }

    public long getTotalServiceTime() {
        return totalServiceTime;
    }

    public void setTotalServiceTime(long totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
    }

    public int getTotalCustomerServed() {
        return totalCustomerServed;
    }

    public void setTotalCustomerServed(int totalCustomerServed) {
        this.totalCustomerServed = totalCustomerServed;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }

    public int getTotalCustomerRated() {
        return totalCustomerRated;
    }

    public void setTotalCustomerRated(int totalCustomerRated) {
        this.totalCustomerRated = totalCustomerRated;
    }

    public long getTotalHoursSaved() {
        return totalHoursSaved;
    }

    public void setTotalHoursSaved(long totalHoursSaved) {
        this.totalHoursSaved = totalHoursSaved;
    }

    @Override
    public String toString() {
        return "BizStoreDailyStatEntity{" +
                "bizStoreId='" + bizStoreId + '\'' +
                ", bizNameId='" + bizNameId + '\'' +
                ", totalServiceTime=" + totalServiceTime +
                ", totalCustomerServed=" + totalCustomerServed +
                ", totalRating=" + totalRating +
                ", totalCustomerRated=" + totalCustomerRated +
                ", totalHoursSaved=" + totalHoursSaved +
                '}';
    }
}
