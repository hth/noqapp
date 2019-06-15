package com.noqapp.view.flow.merchant.validator;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
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
    private AccountService accountService;
    private BizService bizService;

    @Autowired
    public CouponFlowValidator(
        CouponService couponService,
        AccountService accountService,
        BizService bizService
    ) {
        this.couponService = couponService;
        this.accountService = accountService;
        this.bizService = bizService;
    }

    public String validateBusinessDiscount(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate business coupon title={}", couponForm.getDiscountId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponForm.getDiscountId()) && couponForm.getDiscounts().isEmpty()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("discountId")
                    .defaultText("There are no discount available. Please first create discount & then create coupons based on these discounts")
                    .build());
            status = "failure";
        } else if (StringUtils.isBlank(couponForm.getDiscountId()) && !couponForm.getDiscounts().isEmpty()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("discountId")
                    .defaultText("Please select a discount")
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

    public String validateBusinessCoupon(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate business coupon title={}", couponForm.getDiscountId());
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
            if (publish.after(now)) {
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

    public String validateClientDiscount(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate coupon title={}", couponForm.getDiscountId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponForm.getDiscountId()) && couponForm.getDiscounts().isEmpty()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("discountId")
                    .defaultText("There are no discount available. Please first create discount & then create coupons based on these discounts")
                    .build());
            status = "failure";
        } else if (StringUtils.isBlank(couponForm.getDiscountId()) && !couponForm.getDiscounts().isEmpty()) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("discountId")
                    .defaultText("Please select a discount")
                    .build());
            status = "failure";
        }

        return status;
    }

    public String validateClient(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate client title={}", couponForm.getDiscountId());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(couponForm.getPhoneRaw())) {
            messageContext.addMessage(
                new MessageBuilder()
                    .error()
                    .source("phoneRaw")
                    .defaultText("Please provide with a phone number to issue personal coupon")
                    .build());
            status = "failure";
        } else {
            BizNameEntity bizName = bizService.getByBizNameId(couponForm.getBizNamedId());
            String phone = Formatter.phoneNumberWithCountryCode(couponForm.getPhoneRaw(), bizName.getCountryShortName());
            UserProfileEntity userProfile = accountService.checkUserExistsByPhone(phone);

            if (null == userProfile) {
                LOG.warn("Could not find user with phone {} {}", couponForm.getPhoneRaw(), couponForm.getBizNamedId());
                messageContext.addMessage(
                    new MessageBuilder()
                        .error()
                        .source("phoneRaw")
                        .defaultText("Could not find user with phone " + couponForm.getPhoneRaw())
                        .build());
                status = "failure";
            }
        }

        return status;
    }

    public String validateClientCoupon(CouponForm couponForm, MessageContext messageContext) {
        LOG.info("Validate client title={}", couponForm.getDiscountId());
        String status = validateBusinessCoupon(couponForm, messageContext);

        return status;
    }
}
