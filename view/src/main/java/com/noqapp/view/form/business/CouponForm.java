package com.noqapp.view.form.business;

import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.types.ActionTypeEnum;
import com.noqapp.domain.types.DiscountTypeEnum;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 2019-06-11 10:16
 */
public class CouponForm implements Serializable {

    private String couponId;
    private ActionTypeEnum actionType;

    /*Flow. */
    private String discountId;
    private boolean ignore;
    private String bizNamedId;
    private String couponCode;
    private String discountName;
    private String discountDescription;
    private int discountAmount;
    private DiscountTypeEnum discountType;
    private String couponStartDate;
    private String couponEndDate;
    private boolean multiUse;
    private String qid;
    private String couponIssuedByQID;

    private List<CouponEntity> coupons = new LinkedList<>();
    private List<DiscountEntity> discounts = new LinkedList<>();
    private Map<String, String> discountTypes = DiscountTypeEnum.asMapWithNameAsKey();
    private String phoneRaw;
    private String name;
    private String address;

    public static CouponForm newInstance() {
        return new CouponForm();
    }

    public String getCouponId() {
        return couponId;
    }

    public CouponForm setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public CouponForm setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getDiscountId() {
        return discountId;
    }

    public CouponForm setDiscountId(String discountId) {
        this.discountId = discountId;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public CouponForm setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public String getBizNamedId() {
        return bizNamedId;
    }

    public CouponForm setBizNamedId(String bizNamedId) {
        this.bizNamedId = bizNamedId;
        return this;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public CouponForm setCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public String getDiscountName() {
        return discountName;
    }

    public CouponForm setDiscountName(String discountName) {
        this.discountName = discountName;
        return this;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public CouponForm setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
        return this;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public CouponForm setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public CouponForm setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
        return this;
    }

    public String getCouponStartDate() {
        return couponStartDate;
    }

    public CouponForm setCouponStartDate(String couponStartDate) {
        this.couponStartDate = couponStartDate;
        return this;
    }

    public String getCouponEndDate() {
        return couponEndDate;
    }

    public CouponForm setCouponEndDate(String couponEndDate) {
        this.couponEndDate = couponEndDate;
        return this;
    }

    public boolean isMultiUse() {
        return multiUse;
    }

    public CouponForm setMultiUse(boolean multiUse) {
        this.multiUse = multiUse;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public CouponForm setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public String getCouponIssuedByQID() {
        return couponIssuedByQID;
    }

    public CouponForm setCouponIssuedByQID(String couponIssuedByQID) {
        this.couponIssuedByQID = couponIssuedByQID;
        return this;
    }

    public List<CouponEntity> getCoupons() {
        return coupons;
    }

    public CouponForm setCoupons(List<CouponEntity> coupons) {
        this.coupons = coupons;
        return this;
    }

    public CouponForm addCoupon(CouponEntity coupon) {
        this.coupons.add(coupon);
        return this;
    }

    public List<DiscountEntity> getDiscounts() {
        return discounts;
    }

    public CouponForm setDiscounts(List<DiscountEntity> discounts) {
        this.discounts = discounts;
        return this;
    }

    public Map<String, String> getDiscountTypes() {
        return discountTypes;
    }

    public CouponForm setDiscountTypes(Map<String, String> discountTypes) {
        this.discountTypes = discountTypes;
        return this;
    }

    public String getPhoneRaw() {
        return phoneRaw;
    }

    public CouponForm setPhoneRaw(String phoneRaw) {
        this.phoneRaw = phoneRaw;
        return this;
    }

    public String getName() {
        return name;
    }

    public CouponForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CouponForm setAddress(String address) {
        this.address = address;
        return this;
    }
}
