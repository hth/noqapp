package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.CouponEntity;
import com.noqapp.service.CouponService;
import com.noqapp.view.controller.access.LandingController;
import com.noqapp.view.form.business.CouponForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 2019-06-12 02:29
 */
@Component
public class CouponFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(CouponFlowValidator.class);

    private CouponService couponService;

    @Autowired
    public CouponFlowValidator(CouponService couponService) {
        this.couponService = couponService;
    }

    public String validateDiscount(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate coupon title={}", couponForm.getDiscountId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponForm.getDiscountId())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("discountId")
                    .defaultText("There are no discount available. Please first create discount & then create coupons based on these discounts")
                    .build());
            status = "failure";
        } else {
            List<CouponEntity> coupons = couponService.findExistingCouponWithDiscountId(couponForm.getDiscountId());
            if (coupons.size() > 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("discountId")
                        .defaultText(coupons.size() + " existing that refers to discount name " + couponForm.getDiscountId())
                        .build());


                couponForm.setCoupons(coupons);
                status = "failure";
            }
        }

        return status;
    }

    public String validateCoupon(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate coupon title={}", couponForm.getDiscountId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponForm.getCouponStartDate())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("couponStartDate")
                    .defaultText("Please select coupon start date")
                    .build());

            status = "failure";
        } else {
            LocalDate publishDate = LocalDate.parse(couponForm.getCouponStartDate());
            Date publish = Date.from(publishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date now = Date.from(Instant.now());
            if (publish.before(now)) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("couponStartDate")
                        .defaultText("Cannot select coupon start date in past")
                        .build());

                status = "failure";
            }
        }

        if (StringUtils.isBlank(couponForm.getCouponEndDate())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("couponEndDate")
                    .defaultText("Please select coupon end date")
                    .build());

            status = "failure";
        } else {
            LocalDate endDate = LocalDate.parse(couponForm.getCouponEndDate());
            Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date now = Date.from(Instant.now());
            if (end.before(now)) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("couponEndDate")
                        .defaultText("Cannot select coupon end date in past")
                        .build());

                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(couponForm.getCouponStartDate()) && StringUtils.isNotBlank(couponForm.getCouponEndDate())) {
            LocalDate publishDate = LocalDate.parse(couponForm.getCouponStartDate());
            Date publish = Date.from(publishDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            LocalDate endDate = LocalDate.parse(couponForm.getCouponEndDate());
            Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (end.before(publish) || end.compareTo(publish) == 0) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("couponEndDate")
                        .defaultText("Coupon end date should be in future and after start date")
                        .build());

                status = "failure";
            }
        }

        if (status.equalsIgnoreCase("success")) {
            LocalDate endDate = LocalDate.parse(couponForm.getCouponEndDate());
            if (DateUtil.getDaysBetween(LocalDate.now(), endDate) > 365) {
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("couponEndDate")
                        .defaultText("Coupon end date cannot exceed 365 days of validity")
                        .build());

                status = "failure";
            }
        }

        return status;
    }
}
