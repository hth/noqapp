package com.noqapp.view.controller.business.discount;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.DiscountEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.repository.CouponManager;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.DiscountService;
import com.noqapp.view.form.business.DiscountForm;
import com.noqapp.view.validator.DiscountValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-06-09 19:16
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/discount")
public class DiscountController {
    private static final Logger LOG = LoggerFactory.getLogger(DiscountController.class);

    private String nextPage;
    private String discountPage;

    private DiscountValidator discountValidator;
    private DiscountService discountService;
    private BusinessUserService businessUserService;
    private CouponManager couponManager;

    @Autowired
    public DiscountController(
        @Value("${nextPage:/business/discount/landing}")
        String nextPage,

        @Value("${discountPage:/business/discount/discount}")
        String discountPage,

        DiscountValidator discountValidator,
        DiscountService discountService,
        BusinessUserService businessUserService,
        CouponManager couponManager
    ) {
        this.nextPage = nextPage;
        this.discountPage = discountPage;

        this.discountValidator = discountValidator;
        this.discountService = discountService;
        this.businessUserService = businessUserService;
        this.couponManager = couponManager;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("discountForm")
        DiscountForm discountForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on discount page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        List<DiscountEntity> discounts = discountService.findAll(businessUser.getBizName().getId());
        for (DiscountEntity discount : discounts) {
            discount.setUsageCount(couponManager.countDiscountUsage(discount.getId()));
        }
        discountForm.setDiscounts(discounts);
        return nextPage;
    }

    /** Gymnastic for PRG. */
    @GetMapping(value = "/add", produces = "text/html;charset=UTF-8")
    public String add(
        @ModelAttribute("discountForm")
        DiscountForm discountForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on adding new discount qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.discountForm", model.asMap().get("result"));
        }

        return discountPage;
    }

    @PostMapping(value = "/action", produces = "text/html;charset=UTF-8")
    public String action(
        @ModelAttribute("discountForm")
        DiscountForm discountForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Adding new discount qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        switch (discountForm.getActionType()) {
            case ADD:
                discountValidator.validate(discountForm, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("result", result);
                    LOG.warn("Failed validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/business/discount/add.htm";
                }

                int amount = 0;
                switch (discountForm.getDiscountType()) {
                    case F:
                        amount = discountForm.getDiscountAmount() * 100;
                        break;
                    case P:
                        amount = discountForm.getDiscountAmount();
                        break;
                }

                DiscountEntity discount = new DiscountEntity()
                    .setBizNameId(businessUser.getBizName().getId())
                    .setDiscountName(discountForm.getDiscountName())
                    .setDiscountDescription(discountForm.getDiscountDescription())
                    .setDiscountType(discountForm.getDiscountType())
                    .setCouponType(discountForm.getCouponType())
                    .setDiscountAmount(amount);
                discountService.save(discount);
                break;
            case INACTIVE:
                discountService.inActive(discountForm.getDiscountId());
                break;
            case REMOVE:
                discountService.removeDiscount(discountForm.getDiscountId());
                break;
        }

        return "redirect:/business/discount/landing.htm";
    }

    /** For uploading service image. */
    @PostMapping (value = "/action", params = {"cancel_Add"})
    public String upload() {
        return "redirect:/business/discount/landing.htm";
    }
}
