package com.noqapp.service;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonCoupon;
import com.noqapp.domain.json.JsonCouponList;
import com.noqapp.domain.json.JsonPurchaseOrder;
import com.noqapp.domain.types.CouponGroupEnum;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.UserProfileManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-11 08:56
 */
@Service
public class CouponService {
    private static final Logger LOG = LoggerFactory.getLogger(CouponService.class);

    private CouponManager couponManager;
    private BizStoreManager bizStoreManager;
    private UserProfileManager userProfileManager;

    @Autowired
    public CouponService(
        CouponManager couponManager,
        BizStoreManager bizStoreManager,
        UserProfileManager userProfileManager
    ) {
        this.couponManager = couponManager;
        this.bizStoreManager = bizStoreManager;
        this.userProfileManager = userProfileManager;
    }

    public void save(CouponEntity coupon) {
        couponManager.save(coupon);
    }

    private List<CouponEntity> findNearByCoupon(double longitude, double latitude) {
        return couponManager.findNearByCoupon(longitude, latitude);
    }

    public List<CouponEntity> findActiveCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup) {
        return couponManager.findActiveCouponByBizNameId(bizNameId, couponGroup);
    }

    public List<CouponEntity> findUpcomingCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup) {
        return couponManager.findUpcomingCouponByBizNameId(bizNameId, couponGroup);
    }

    public long countActiveBusinessCouponByDiscountId(String discountId) {
        return couponManager.countActiveBusinessCouponByDiscountId(discountId);
    }

    public CouponEntity findById(String couponId) {
        return couponManager.findById(couponId);
    }

    JsonCoupon findByIdAsJson(String couponId) {
        Assert.hasText(couponId, "Coupon Id cannot be blank");
        CouponEntity coupon = couponManager.findById(couponId);
        return JsonCoupon.populate(coupon);
    }

    @Mobile
    public JsonCouponList findNearByCouponAsJson(double longitude, double latitude) {
        List<CouponEntity> coupons = findNearByCoupon(longitude, latitude);

        JsonCouponList jsonDiscountList = new JsonCouponList();
        for (CouponEntity coupon : coupons) {
            jsonDiscountList.addCoupon(JsonCoupon.populate(coupon));
        }

        return jsonDiscountList;
    }

    @Mobile
    public JsonCouponList findActiveCouponAsJson(String codeQR, CouponGroupEnum couponGroup) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        List<CouponEntity> coupons = findActiveCouponByBizNameId(bizStore.getBizName().getId(), couponGroup);

        JsonCouponList jsonDiscountList = new JsonCouponList();
        for (CouponEntity coupon : coupons) {
            jsonDiscountList.addCoupon(JsonCoupon.populate(coupon));
        }

        return jsonDiscountList;
    }

    @Mobile
    public JsonCouponList findActiveClientCouponByQidAsJson(String qid) {
        JsonCouponList jsonDiscountList = new JsonCouponList();
        List<String> qidOfFamily = populateWithFamily(qid);
        for (String familyQid : qidOfFamily) {
            List<CouponEntity> coupons = couponManager.findActiveClientCouponByQid(familyQid);
            for (CouponEntity coupon : coupons) {
                jsonDiscountList.addCoupon(JsonCoupon.populate(coupon));
            }
        }

        return jsonDiscountList;
    }

    private List<String> populateWithFamily(String qid) {
        UserProfileEntity userProfile = userProfileManager.findByQueueUserId(qid);
        List<String> dependents = userProfile.getQidOfDependents();
        if (null == dependents) {
            dependents = new ArrayList<String>() {{
                add(qid);
            }};
        } else {
            dependents.add(qid);
        }
        return dependents;
    }

    @Mobile
    public JsonCouponList findActiveClientCouponByQidAsJson(String qid, String codeQR) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        JsonCouponList jsonDiscountList = new JsonCouponList();
        List<String> qidOfFamily = populateWithFamily(qid);
        for (String familyQid : qidOfFamily) {
            List<CouponEntity> coupons = couponManager.findActiveClientCouponByQid(familyQid, bizStore.getBizName().getId());
            for (CouponEntity coupon : coupons) {
                jsonDiscountList.addCoupon(JsonCoupon.populate(coupon));
            }
        }

        return jsonDiscountList;
    }

    public boolean checkIfCouponExistsForQid(String discountId, String qid) {
        return couponManager.checkIfCouponExistsForQid(discountId, qid);
    }

    @Mobile
    public JsonPurchaseOrder addCouponInformationIfAny(JsonPurchaseOrder jsonPurchaseOrder) {
        if (StringUtils.isNotBlank(jsonPurchaseOrder.getCouponId())) {
            JsonCoupon jsonCoupon = findByIdAsJson(jsonPurchaseOrder.getCouponId());
            jsonPurchaseOrder.setJsonCoupon(jsonCoupon);
        }
        return jsonPurchaseOrder;
    }
}
