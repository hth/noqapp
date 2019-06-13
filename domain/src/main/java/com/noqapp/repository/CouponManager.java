package com.noqapp.repository;

import com.noqapp.domain.CouponEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 13:38
 */
public interface CouponManager extends RepositoryManager<CouponEntity> {

    /**
     * Gets currently active coupons.
     */
    List<CouponEntity> findActiveByBizNameId(String bizNameId);

    List<CouponEntity> findUpcomingByBizNameId(String bizNameId);

    long inActiveCouponWithDiscountId(String discountId);

    List<CouponEntity> findExistingCouponWithDiscountId(String discountId);

    CouponEntity findById(String couponId);
}
