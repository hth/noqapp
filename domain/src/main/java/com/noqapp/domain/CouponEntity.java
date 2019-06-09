package com.noqapp.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Coupon is code shared for a discount. Coupon code can change without affecting discount.
 *
 * User: hitender
 * Date: 2019-06-09 13:39
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Document(collection = "COUPON")
@CompoundIndexes(value = {
    @CompoundIndex(name = "coupon_idx", def = "{'BN': -1, 'DI': -1}", unique = false),
    @CompoundIndex(name = "coupon_code_idx", def = "{'CC': -1}", unique = true),
    @CompoundIndex(name = "coupon_code_qid_idx", def = "{'BN' : -1, 'CC': -1, 'QID': -1, 'BS' : -1}", unique = false, background = true, sparse = true),
})
public class CouponEntity extends BaseEntity {

    @Field("BN")
    private String bizNameId;

    @Field("DI")
    private String discountId;

    @Field("CC")
    private String couponCode;

    @Field("DN")
    private String discountName;

    @Field("DD")
    private String discountDescription;

    @Field("DA")
    private int discountAmount;

    @Field("SD")
    private Date couponStartDate;

    @Field("ED")
    private Date couponEndDate;

    @Field("MU")
    private boolean multiUse;

    @Field("QID")
    private String qid;

    @Field("BS")
    private String bizStoreId;

    @Field("IB")
    private String couponIssuedByQID;

    public String getBizNameId() {
        return bizNameId;
    }

    public CouponEntity setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getDiscountId() {
        return discountId;
    }

    public CouponEntity setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public CouponEntity setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public CouponEntity setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public CouponEntity setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public CouponEntity setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public Date getCouponStartDate() {
        return couponStartDate;
    }

    public CouponEntity setCouponStartDate(Date couponStartDate) {
        this.couponStartDate = couponStartDate;
        return this;
    }

    public Date getCouponEndDate() {
        return couponEndDate;
    }

    public CouponEntity setCouponEndDate(Date couponEndDate) {
        this.couponEndDate = couponEndDate;
        return this;
    }
}
