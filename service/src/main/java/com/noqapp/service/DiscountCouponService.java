package com.noqapp.service;

import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.DiscountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 19:05
 */
@Service
public class DiscountCouponService {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountCouponService.class);

    private DiscountManager discountManager;
    private CouponManager couponManager;

    @Autowired
    public DiscountCouponService(
        DiscountManager discountManager,
        CouponManager couponManager
    ) {
        this.discountManager = discountManager;
        this.couponManager = couponManager;
    }

    public void saveDiscount(DiscountEntity discount) {
        discountManager.save(discount);
    }

    public void saveDiscount(CouponEntity coupon) {
        couponManager.save(coupon);
    }

    public List<DiscountEntity> findAll(String bizNameId) {
        return discountManager.findAll(bizNameId);
    }

    public void inActive(String discountId) {
        discountManager.inActive(discountId);
    }

    public void removeDiscount(String discountId) {
        discountManager.removeById(discountId);
    }
}
