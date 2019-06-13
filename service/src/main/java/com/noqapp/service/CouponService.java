package com.noqapp.service;

import static com.noqapp.common.utils.AbstractDomain.ISO8601_FMT;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.annotation.Mobile;
import com.noqapp.domain.json.JsonCoupon;
import com.noqapp.domain.json.JsonCouponList;
import com.noqapp.repository.BizStoreManager;
import com.noqapp.repository.CouponManager;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TimeZone;

/**
 * User: hitender
 * Date: 2019-06-11 08:56
 */
@Service
public class CouponService {
    private static final Logger LOG = LoggerFactory.getLogger(CouponService.class);

    private CouponManager couponManager;
    private BizStoreManager bizStoreManager;

    @Autowired
    public CouponService(
        CouponManager couponManager,
        BizStoreManager bizStoreManager
    ) {
        this.couponManager = couponManager;
        this.bizStoreManager = bizStoreManager;
    }

    public void save(CouponEntity coupon) {
        couponManager.save(coupon);
    }

    public List<CouponEntity> findActiveByBizNameId(String bizNameId) {
        return couponManager.findActiveByBizNameId(bizNameId);
    }

    public List<CouponEntity> findUpcomingByBizNameId(String bizNameId) {
        return couponManager.findUpcomingByBizNameId(bizNameId);
    }

    public List<CouponEntity> findExistingCouponWithDiscountId(String discountId) {
        return couponManager.findExistingCouponWithDiscountId(discountId);
    }

    public CouponEntity findById(String couponId) {
        return couponManager.findById(couponId);
    }

    @Mobile
    public JsonCouponList findAllCouponAsJson(String codeQR) {
        BizStoreEntity bizStore = bizStoreManager.findByCodeQR(codeQR);
        List<CouponEntity> coupons = findActiveByBizNameId(bizStore.getBizName().getId());

        JsonCouponList jsonDiscountList = new JsonCouponList();
        for (CouponEntity coupon : coupons) {
            jsonDiscountList.addCoupon(
                new JsonCoupon()
                    .setCouponId(coupon.getId())
                    .setBizNameId(coupon.getBizNameId())
                    .setCouponCode(coupon.getCouponCode())
                    .setDiscountName(coupon.getDiscountName())
                    .setDiscountDescription(coupon.getDiscountDescription())
                    .setDiscountAmount(coupon.getDiscountAmount())
                    .setDiscountType(coupon.getDiscountType())
                    .setCouponStartDate(DateFormatUtils.format(coupon.getCouponStartDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
                    .setCouponEndDate(DateFormatUtils.format(coupon.getCouponEndDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC")))
            );
        }

        return jsonDiscountList;
    }
}
