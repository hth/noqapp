package com.noqapp.domain.json;

import com.noqapp.common.utils.AbstractDomain;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.types.DiscountTypeEnum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.TimeZone;

/**
 * User: hitender
 * Date: 2019-06-12 06:48
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable",
    "unused"
})
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonCoupon extends AbstractDomain {

    @JsonProperty("ci")
    private String couponId;

    @JsonProperty("bn")
    private String bizNameId;

    @JsonProperty("cc")
    private String couponCode;

    @JsonProperty("dn")
    private String discountName;

    @JsonProperty("dd")
    private String discountDescription;

    @JsonProperty("da")
    private int discountAmount;

    @JsonProperty("dt")
    private DiscountTypeEnum discountType;

    @JsonProperty("sd")
    private String couponStartDate;

    @JsonProperty("ed")
    private String couponEndDate;

    @JsonProperty("mu")
    private boolean multiUse;

    @JsonProperty("qid")
    private String qid;

    public JsonCoupon() {
        //Required default constructor
    }

    public String getCouponId() {
        return couponId;
    }

    public JsonCoupon setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public String getBizNameId() {
        return bizNameId;
    }

    public JsonCoupon setBizNameId(String bizNameId) {
        this.bizNameId = bizNameId;
        return this;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public JsonCoupon setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public JsonCoupon setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public JsonCoupon setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public JsonCoupon setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public JsonCoupon setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }

    public String getCouponStartDate() {
        return couponStartDate;
    }

    public JsonCoupon setCouponStartDate(String couponStartDate) {
        this.couponStartDate = couponStartDate;
        return this;
    }

    public String getCouponEndDate() {
        return couponEndDate;
    }

    public JsonCoupon setCouponEndDate(String couponEndDate) {
        this.couponEndDate = couponEndDate;
        return this;
    }

    public boolean isMultiUse() {
        return multiUse;
    }

    public JsonCoupon setMultiUse(boolean multiUse) {
        this.multiUse = multiUse;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public JsonCoupon setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public static JsonCoupon populate(CouponEntity coupon) {
        if (null != coupon) {
            return new JsonCoupon()
                .setCouponId(coupon.getId())
                .setBizNameId(coupon.getBizNameId())
                .setCouponCode(coupon.getCouponCode())
                .setDiscountName(coupon.getDiscountName())
                .setDiscountDescription(coupon.getDiscountDescription())
                .setDiscountAmount(coupon.getDiscountAmount())
                .setDiscountType(coupon.getDiscountType())
                .setCouponStartDate(DateFormatUtils.format(coupon.getCouponStartDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
                .setCouponEndDate(DateFormatUtils.format(coupon.getCouponEndDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
                .setQid(coupon.getQid());
        } else {
            return new JsonCoupon();
        }
    }
}
