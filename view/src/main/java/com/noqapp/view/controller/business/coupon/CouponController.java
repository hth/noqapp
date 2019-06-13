package com.noqapp.view.controller.business.coupon;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.repository.UserProfileManager;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CouponService;
import com.noqapp.view.form.business.CouponForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-06-11 09:29
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/coupon")
public class CouponController {
    private static final Logger LOG = LoggerFactory.getLogger(CouponController.class);

    private String nextPage;
    private String upcomingPage;
    private String couponFlow;
    private String couponForClientFlow;

    private UserProfileManager userProfileManager;
    private CouponService couponService;
    private BusinessUserService businessUserService;

    @Autowired
    public CouponController(
        @Value("${nextPage:/business/coupon/landing}")
        String nextPage,

        @Value("${nextPage:/business/coupon/upcoming}")
        String upcomingPage,

        @Value("${couponFlow:redirect:/store/coupon/couponBusiness.htm}")
        String couponFlow,

        @Value("${couponForClientFlow:redirect:/store/coupon/couponClient.htm}")
        String couponForClientFlow,

        UserProfileManager userProfileManager,
        CouponService couponService,
        BusinessUserService businessUserService
    ) {
        this.nextPage = nextPage;
        this.upcomingPage = upcomingPage;
        this.couponFlow = couponFlow;
        this.couponForClientFlow = couponForClientFlow;

        this.userProfileManager = userProfileManager;
        this.couponService = couponService;
        this.businessUserService = businessUserService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("couponForm")
        CouponForm couponForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on active coupon page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        List<CouponEntity> coupons = couponService.findActiveByBizNameId(businessUser.getBizName().getId());
        for (CouponEntity coupon : coupons) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(coupon.getCouponIssuedByQID());
            coupon.setIssuedBy(userProfile.getName());
            couponForm.addCoupon(coupon);
        }

        return nextPage;
    }

    @GetMapping(value = "/upcoming", produces = "text/html;charset=UTF-8")
    public String upcoming(
        @ModelAttribute("couponForm")
        CouponForm couponForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on upcoming coupon page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        List<CouponEntity> coupons = couponService.findUpcomingByBizNameId(businessUser.getBizName().getId());
        for (CouponEntity coupon : coupons) {
            UserProfileEntity userProfile = userProfileManager.findByQueueUserId(coupon.getCouponIssuedByQID());
            coupon.setIssuedBy(userProfile.getName());
            couponForm.addCoupon(coupon);
        }

        return upcomingPage;
    }

    @GetMapping(value = "/newBusinessCoupon", produces = "text/html;charset=UTF-8")
    public String newBusinessCoupon() {
        LOG.info("Landed to publish new business coupon");
        return couponFlow;
    }

    @GetMapping(value = "/newClientCoupon", produces = "text/html;charset=UTF-8")
    public String newClientCoupon() {
        LOG.info("Landed to publish new client coupon");
        return couponForClientFlow;
    }
}
