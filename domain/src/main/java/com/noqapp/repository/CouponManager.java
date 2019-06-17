package com.noqapp.repository;

import com.noqapp.domain.CouponEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 13:38
 */
public interface CouponManager extends RepositoryManager<CouponEntity> {

    /** Gets currently active coupons. */
    List<CouponEntity> findActiveBusinessCouponByBizNameId(String bizNameId);

    List<CouponEntity> findUpcomingBusinessCouponByBizNameId(String bizNameId);

    long inActiveCouponWithDiscountId(String discountId);

    long countActiveBusinessCouponWithDiscountId(String discountId);

    CouponEntity findById(String couponId);

    List<CouponEntity> findActiveClientCouponByQid(String qid);

    long countDiscountUsage(String discountId);

    List<CouponEntity> findActiveGlobalCoupon();
}
