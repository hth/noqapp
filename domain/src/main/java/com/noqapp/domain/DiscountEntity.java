package com.noqapp.domain;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.types.DiscountTypeEnum;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * User: hitender
 * Date: 2019-06-09 13:43
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "DISCOUNT")
@CompoundIndexes(value = {
    @CompoundIndex(name = "discount_idx", def = "{'BN': 1}", unique = false),
})
public class DiscountEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field("DN")
    private String discountName;

    @Field("DD")
    private String discountDescription;

    @Field("DA")
    private int discountAmount;

    @Field("DT")
    private DiscountTypeEnum discountType;

    @Field("MI")
    private Date markedInActiveDate;

    public String getBizNameId() {
        return bizNameId;
    }

    public DiscountEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public DiscountEntity setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public DiscountEntity setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public DiscountEntity setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public DiscountEntity setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }

    public Date getMarkedInActiveDate() {
        return markedInActiveDate;
    }

    public DiscountEntity setMarkedInActiveDate(Date markedInActiveDate) {
        this.markedInActiveDate = markedInActiveDate;
        return this;
    }

    @Transient
    public long getCanDeletedAfterDays() {
        return DateUtil.getDaysBetween(
            DateUtil.asLocalDate(DateUtil.nowMidnightDate()),
            DateUtil.asLocalDate(isActive() ? DateUtil.nowMidnightDate() : markedInActiveDate).plusYears(1));

    }
}
