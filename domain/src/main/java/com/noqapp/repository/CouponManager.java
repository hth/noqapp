package com.noqapp.repository;

import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.types.CouponGroupEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-09 13:38
 */
public interface CouponManager extends RepositoryManager<CouponEntity> {

    List<CouponEntity> findNearByCoupon(double longitude, double latitude);

    /** Gets currently active coupons. */
    List<CouponEntity> findActiveCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup);

    List<CouponEntity> findUpcomingCouponByBizNameId(String bizNameId, CouponGroupEnum couponGroup);

    long inActiveCouponWithDiscountId(String discountId);

    long countActiveBusinessCouponByDiscountId(String discountId);

    CouponEntity findById(String couponId);

    CouponEntity findById(String couponId, String bizNameId);

    List<CouponEntity> findActiveClientCouponByQid(String qid);

    List<CouponEntity> findActiveClientCouponByQid(String qid, String bizNameId);

    long countDiscountUsage(String discountId);

    boolean checkIfCouponExistsForQid(String discountId, String qid);

    List<CouponEntity> findAllGlobalCouponForClient(String bizNameId);

    boolean doesGlobalCouponTypeExists(String discountId);
}
