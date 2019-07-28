package com.noqapp.view.controller.business.payout;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.CouponEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.TransactionViaEnum;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.CouponService;
import com.noqapp.service.PayoutService;
import com.noqapp.view.form.business.CouponForm;
import com.noqapp.view.form.business.payout.HistoricalTransactionForm;
import com.noqapp.view.form.business.payout.PayoutLandingForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-03-30 10:58
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/payout")
public class PayoutLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(PayoutLandingController.class);

    private String nextPage;
    private String historicalTransactionPage;
    private String transactionOnDayPage;
    private String couponDetailPage;

    private BusinessUserService businessUserService;
    private PayoutService payoutService;
    private CouponService couponService;

    @Autowired
    public PayoutLandingController(
        @Value("${nextPage:/business/payout/landing}")
        String nextPage,

        @Value("${historicalTransactionPage:/business/payout/historical}")
        String historicalTransactionPage,

        @Value("${historicalTransactionPage:/business/payout/transactionOnDay}")
        String transactionOnDayPage,

        @Value("${couponDetailPage:/business/payout/couponDetail}")
        String couponDetailPage,

        BusinessUserService businessUserService,
        PayoutService payoutService,
        CouponService couponService
    ) {
        this.nextPage = nextPage;
        this.historicalTransactionPage = historicalTransactionPage;
        this.transactionOnDayPage = transactionOnDayPage;
        this.couponDetailPage = couponDetailPage;

        this.businessUserService = businessUserService;
        this.payoutService = payoutService;
        this.couponService = couponService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("payoutLandingForm")
        PayoutLandingForm payoutLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on payout page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        payoutLandingForm.setPurchaseOrders(payoutService.currentTransactions(businessUser.getBizName().getId()));
        return nextPage;
    }

    @GetMapping(value = "/historical", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("historicalTransactionForm")
        HistoricalTransactionForm historicalTransactionForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on payout page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String bizNameId = businessUser.getBizName().getId();
        historicalTransactionForm.setDurationInDays(45);

        List<PurchaseOrderEntity> purchaseOrderInternal = payoutService.computeEarning(bizNameId, TransactionViaEnum.I, 45);
        historicalTransactionForm.populate(purchaseOrderInternal);
        List<PurchaseOrderEntity> purchaseOrderExternal = payoutService.computeEarning(bizNameId, TransactionViaEnum.E, 45);
        historicalTransactionForm.populate(purchaseOrderExternal);
        List<PurchaseOrderEntity> purchaseOrderUnknown = payoutService.computeEarning(bizNameId, TransactionViaEnum.U, 45);
        historicalTransactionForm.populate(purchaseOrderUnknown);
        return historicalTransactionPage;
    }

    @GetMapping(value = "/transactionOnDay/{day}", produces = "text/html;charset=UTF-8")
    public String transactionOnDay(
        @PathVariable("day")
        ScrubbedInput day,

        @ModelAttribute("payoutLandingForm")
        PayoutLandingForm payoutLandingForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on payout page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String bizNameId = businessUser.getBizName().getId();
        List<PurchaseOrderEntity> purchaseOrders = payoutService.findTransactionOnDay(bizNameId, day.getText());
        payoutLandingForm.setPurchaseOrders(purchaseOrders);
        return transactionOnDayPage;
    }

    @GetMapping(value = "/coupon/{id}", produces = "text/html;charset=UTF-8")
    public String findById(
        @PathVariable("id")
        ScrubbedInput id,

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
        LOG.info("Landed on upcoming business coupon page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        String bizNameId = businessUser.getBizName().getId();
        CouponEntity coupon = couponService.findById(id.getText(), bizNameId);
        if (null != coupon) {
            couponForm
                .setCouponId(coupon.getId())
                .setDiscountId(coupon.getDiscountId())
                .setBizNamedId(coupon.getBizNameId())
                .setCouponCode(coupon.getCouponCode())
                .setDiscountName(coupon.getDiscountName())
                .setDiscountDescription(coupon.getDiscountDescription())
                .setDiscountAmount(coupon.getDiscountAmount())
                .setDiscountType(coupon.getDiscountType())
                .setCouponType(coupon.getCouponType())
                .setCouponStartDate(DateUtil.dateToString(coupon.getCouponStartDate()))
                .setCouponEndDate(DateUtil.dateToString(coupon.getCouponEndDate()))
                .setMultiUse(coupon.isMultiUse())
                .setQid(coupon.getQid())
                .setCouponIssuedByQID(coupon.getCouponIssuedByQID())
                .setPhoneRaw(coupon.getIssuedToPhone());
        }

        return couponDetailPage;
    }
}
