package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.RandomString;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CouponService;
import com.noqapp.service.DiscountService;
import com.noqapp.view.flow.merchant.exception.UnAuthorizedAccessException;
import com.noqapp.view.form.business.CouponForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;

import java.time.ZoneOffset;

/**
 * User: hitender
 * Date: 2019-06-11 23:54
 */
@Component
public class CouponFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(CouponFlowActions.class);

    private DiscountService discountService;
    private CouponService couponService;
    private BusinessUserService businessUserService;

    @Autowired
    public CouponFlowActions(
        DiscountService discountService,
        CouponService couponService,
        BusinessUserService businessUserService
    ) {
        this.discountService = discountService;
        this.couponService = couponService;
        this.businessUserService = businessUserService;
    }

    public CouponForm createNewBusinessCoupon(ExternalContext externalContext) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        return CouponForm.newInstance().setDiscounts(discountService.findAllActive(businessUser.getBizName().getId()));
    }

    public void populateCouponForm(CouponForm couponForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DiscountEntity discount = discountService.findById(couponForm.getDiscountId());
        couponForm.setBizNamedId(discount.getBizNameId())
            .setCouponCode(RandomString.newInstance(6).nextString())
            .setDiscountName(discount.getDiscountName())
            .setDiscountDescription(discount.getDiscountDescription())
            .setDiscountAmount(discount.getDiscountAmount())
            .setDiscountType(discount.getDiscountType())
            .setMultiUse(true)
            .setCouponIssuedByQID(queueUser.getQueueUserId());
    }

    public void createCoupon(CouponForm couponForm) {
        CouponEntity coupon = new CouponEntity()
            .setBizNameId(couponForm.getBizNamedId())
            .setDiscountId(couponForm.getDiscountId())
            .setCouponCode(couponForm.getCouponCode())
            .setDiscountName(couponForm.getDiscountName())
            .setDiscountDescription(couponForm.getDiscountDescription())
            .setDiscountAmount(couponForm.getDiscountAmount())
            .setDiscountType(couponForm.getDiscountType())
            .setCouponStartDate(DateUtil.convertToDate(couponForm.getCouponStartDate(), ZoneOffset.UTC))
            .setCouponEndDate(DateUtil.convertToDate(couponForm.getCouponEndDate(), ZoneOffset.UTC))
            .setMultiUse(couponForm.isMultiUse())
            .setCouponIssuedByQID(couponForm.getCouponIssuedByQID());

        couponService.save(coupon);
    }
}
