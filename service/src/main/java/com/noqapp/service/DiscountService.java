package com.noqapp.service;

import com.noqapp.domain.DiscountEntity;
import com.noqapp.repository.CouponManager;
import com.noqapp.repository.DiscountManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 19:05
 */
@Service
public class DiscountService {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountService.class);

    private DiscountManager discountManager;
    private CouponManager couponManager;

    @Autowired
    public DiscountService(
        DiscountManager discountManager,
        CouponManager couponManager
    ) {
        this.discountManager = discountManager;
        this.couponManager = couponManager;
    }

    public void save(DiscountEntity discount) {
        discountManager.save(discount);
    }

    public List<DiscountEntity> findAll(String bizNameId) {
        return discountManager.findAll(bizNameId);
    }

    public List<DiscountEntity> findAllActive(String bizNameId) {
        List<DiscountEntity> discounts = discountManager.findAllActive(bizNameId);
        List<DiscountEntity> remove = new ArrayList<>();
        for (DiscountEntity discount : discounts) {
            switch (discount.getCouponType()) {
                case G:
                    /* Only for Global Coupon. */
                    if (couponManager.doesGlobalCouponTypeExists(discount.getId())) {
                        remove.add(discount);
                    }
                    break;
                case F:
                case I:
                    /* Do nothing for F and I as there can be multiple copies of it. */
                    break;
                default:
                    LOG.error("Reached un-reachable condition {}", discount.getCouponType());
                    throw new UnsupportedOperationException("Reached unsupported condition " + discount.getCouponType());
            }
        }

        discounts.removeAll(remove);
        return discounts;
    }

    public void inActive(String discountId) {
        long recordsModified = couponManager.inActiveCouponWithDiscountId(discountId);
        discountManager.inActive(discountId);
        LOG.info("Number of coupons marked inactive {} for {}", recordsModified, discountId);
    }

    public void removeDiscount(String discountId) {
        discountManager.removeById(discountId);
    }

    public DiscountEntity findById(String discountId) {
        return discountManager.findById(discountId);
    }
}
