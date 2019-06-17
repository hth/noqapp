package com.noqapp.view.flow.merchant;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.RandomString;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.CouponGroupEnum;
import com.noqapp.domain.types.CouponTypeEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.BizService;
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
import java.util.LinkedList;
import java.util.List;

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
    private BizService bizService;
    private AccountService accountService;

    @Autowired
    public CouponFlowActions(
        DiscountService discountService,
        CouponService couponService,
        BusinessUserService businessUserService,
        BizService bizService,
        AccountService accountService
    ) {
        this.discountService = discountService;
        this.couponService = couponService;
        this.businessUserService = businessUserService;
        this.bizService = bizService;
        this.accountService = accountService;
    }

    /* All coupon starts from here. */
    @SuppressWarnings("unused")
    public CouponForm createNewBusinessCoupon(ExternalContext externalContext) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            throw new UnAuthorizedAccessException("Not authorized to access " + queueUser.getQueueUserId());
        }
        /* Above condition to make sure users with right roles and access gets access. */

        CouponForm couponForm = CouponForm.newInstance().setDiscounts(discountService.findAllActive(businessUser.getBizName().getId()));
        couponForm.setBizNamedId(businessUser.getBizName().getId());
        return couponForm;
    }

    /* Populates coupon for business use. */
    @SuppressWarnings("unused")
    public void populateBusinessCouponForm(CouponForm couponForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DiscountEntity discount = discountService.findById(couponForm.getDiscountId());
        BizNameEntity bizName = bizService.getByBizNameId(discount.getBizNameId());
        couponForm.setBizNamedId(discount.getBizNameId())
            .setCouponCode(RandomString.newInstance(6).nextString())
            .setDiscountName(discount.getDiscountName())
            .setDiscountDescription(discount.getDiscountDescription())
            .setDiscountAmount(discount.getDiscountAmount())
            .setDiscountType(discount.getDiscountType())
            .setCouponType(discount.getCouponType())
            .setMultiUse(true) //By default it should be multi use
            .setCoordinate(bizName.getCoordinate())
            .setCouponIssuedByQID(queueUser.getQueueUserId());
    }

    /* Finally save the business coupon. */
    @SuppressWarnings("unused")
    public void createBusinessCoupon(CouponForm couponForm) {
        CouponEntity coupon = populateCommonCoupon(couponForm);
        coupon.setCouponGroup(CouponGroupEnum.M);
        couponService.save(coupon);
    }

    /* Base on coupon type select the flow when creating client coupon. */
    @SuppressWarnings("unused")
    public String selectAppropriateFlow(CouponForm couponForm) {
        DiscountEntity discount = discountService.findById(couponForm.getDiscountId());
        return discount.getCouponType().getName();
    }

    /* Coupon for Individual person. */
    @SuppressWarnings("unused")
    public void populateWithGuardianDetail(CouponForm couponForm) {
        BizNameEntity bizName = bizService.getByBizNameId(couponForm.getBizNamedId());
        String phone = Formatter.phoneNumberWithCountryCode(couponForm.getPhoneRaw(), bizName.getCountryShortName());
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(phone);
        couponForm
            .setQid(userProfile.getQueueUserId())
            .setName(userProfile.getName())
            .setAddress(userProfile.getAddress());
    }

    /* Coupon for Whole family. */
    @SuppressWarnings("unused")
    public void populateWithFamilyDetail(CouponForm couponForm) {
        BizNameEntity bizName = bizService.getByBizNameId(couponForm.getBizNamedId());
        String phone = Formatter.phoneNumberWithCountryCode(couponForm.getPhoneRaw(), bizName.getCountryShortName());
        UserProfileEntity userProfile = accountService.checkUserExistsByPhone(phone);
        couponForm
            .setQid(userProfile.getQueueUserId())
            .setName(userProfile.getName())
            .setAddress(userProfile.getAddress());

        couponForm.setUserProfiles(new LinkedList<>());
        List<String> qidOfDependents = userProfile.getQidOfDependents();
        for (String dependentQid : qidOfDependents) {
            couponForm.addUserProfile(accountService.findProfileByQueueUserId(dependentQid));
        }

        couponForm.addUserProfile(userProfile);
    }

    public void populateClientCouponForm(CouponForm couponForm) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DiscountEntity discount = discountService.findById(couponForm.getDiscountId());
        couponForm.setBizNamedId(discount.getBizNameId())
            .setCouponCode(RandomString.newInstance(6).nextString())
            .setDiscountName(discount.getDiscountName())
            .setDiscountDescription(discount.getDiscountDescription())
            .setDiscountAmount(discount.getDiscountAmount())
            .setDiscountType(discount.getDiscountType())
            .setCouponType(discount.getCouponType())
            .setCouponIssuedByQID(queueUser.getQueueUserId());

        switch (discount.getCouponType()) {
            case G:
                BizNameEntity bizName = bizService.getByBizNameId(couponForm.getBizNamedId());
                couponForm
                    .setCoordinate(bizName.getCoordinate())
                    .setMultiUse(true);
                break;
            case F:
                couponForm.setMultiUse(true);
                break;
            case I:
                UserProfileEntity userProfile = accountService.findProfileByQueueUserId(couponForm.getQid());
                couponForm
                    .setQid(couponForm.getQid())
                    .setName(userProfile.getName())
                    .setAddress(userProfile.getAddress());
                break;
        }
    }

    public void createClientCoupon(CouponForm couponForm) {
        CouponEntity coupon = populateCommonCoupon(couponForm);
        if (couponForm.getCouponType() != CouponTypeEnum.G) {
            coupon.setQid(couponForm.getQid());
        }
        coupon.setCouponGroup(CouponGroupEnum.C);
        couponService.save(coupon);
    }

    private CouponEntity populateCommonCoupon(CouponForm couponForm) {
        return new CouponEntity()
            .setBizNameId(couponForm.getBizNamedId())
            .setDiscountId(couponForm.getDiscountId())
            .setCouponCode(couponForm.getCouponCode())
            .setDiscountName(couponForm.getDiscountName())
            .setDiscountDescription(couponForm.getDiscountDescription())
            .setDiscountAmount(couponForm.getDiscountAmount())
            .setDiscountType(couponForm.getDiscountType())
            .setCouponType(couponForm.getCouponType())
            .setCouponStartDate(DateUtil.convertToDate(couponForm.getCouponStartDate(), ZoneOffset.UTC))
            .setCouponEndDate(DateUtil.convertToDate(couponForm.getCouponEndDate(), ZoneOffset.UTC))
            .setMultiUse(couponForm.isMultiUse())
            .setCoordinate(couponForm.getCoordinate())
            .setCouponIssuedByQID(couponForm.getCouponIssuedByQID());
    }
}
