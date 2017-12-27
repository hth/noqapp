package com.noqapp.view.controller.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizCategoryEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.view.form.business.CategoryLandingForm;
import com.noqapp.view.validator.BusinessCategoryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * hitender
 * 12/20/17 4:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/category")
public class CategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryController.class);

    private String nextPage;
    private String businessPage;

    private BizService bizService;
    private BusinessUserService businessUserService;
    private BusinessCategoryValidator businessCategoryValidator;

    @Autowired
    public CategoryController(
            @Value("${nextPage:/business/category}")
            String nextPage,

            @Value ("${businessPage:/business/landing.htm}")
            String businessPage,

            BizService bizService,
            BusinessUserService businessUserService,
            BusinessCategoryValidator businessCategoryValidator) {
        this.nextPage = nextPage;
        this.businessPage = businessPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.businessCategoryValidator = businessCategoryValidator;
    }

    /**
     * Loading landing page for business category.
     * Gymnastic for PRG.
     *
     * @param categoryLanding
     * @return
     */
    @RequestMapping (method = RequestMethod.GET)
    public String landing(
            @ModelAttribute("categoryLanding")
            CategoryLandingForm categoryLanding,

            Model model,
            RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business category page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.categoryLanding", model.asMap().get("result"));
        } else {
            redirectAttrs.addFlashAttribute("categoryLanding", categoryLanding);
        }

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(queueUser.getQueueUserId());
        String bizNameId = businessUser.getBizName().getId();
        Map<String, String> categories = bizService.getBusinessCategoriesAsMap(bizNameId);
        categoryLanding
                .setBizNameId(new ScrubbedInput(bizNameId))
                .setCategories(categories)
                .setCategoryCounts(bizService.countCategoryUse(categories.keySet(), bizNameId));
        return nextPage;
    }

    /**
     * Add new category.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/add",
            method = RequestMethod.POST,
            params = {"add"}
    )
    public String add(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Adding business category qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        businessCategoryValidator.validate(categoryLanding, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + ".htm";
        }

        bizService.addCategory(categoryLanding.getCategoryName().getText(), categoryLanding.getBizNameId().getText());
        categoryLanding.setCategoryName(null);
        redirectAttrs.addFlashAttribute("categoryLanding", categoryLanding);
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * On cancelling addition of new category.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/add",
            method = RequestMethod.POST,
            params = {"cancel_Add"}
    )
    public String cancelAdd(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel business category qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:" + businessPage;
    }

    /**
     * Edit landing category name.
     *
     * @param categoryId
     * @return
     */
    @RequestMapping (value = "/{categoryId}/edit", method = RequestMethod.GET)
    public String editLanding(
            @PathVariable("categoryId")
            ScrubbedInput categoryId,

            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding,

            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on editing category business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        BizCategoryEntity bizCategory = bizService.findByBizCategoryId(categoryId.getText());
        categoryLanding
                .setCategoryId(categoryId)
                .setBizNameId(new ScrubbedInput(bizCategory.getBizNameId()))
                .setCategoryName(new ScrubbedInput(bizCategory.getCategoryName()));


        redirectAttrs.addFlashAttribute("categoryLanding", categoryLanding);
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * Edit landing category name.
     *
     * @param categoryLanding
     * @param redirectAttrs
     * @param response
     * @return
     */
    @RequestMapping (value = "/edit", method = RequestMethod.POST)
    public String edit(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding,

            BindingResult result,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on editing category business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        businessCategoryValidator.validate(categoryLanding, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + ".htm";
        }

        bizService.updateBizCategoryName(categoryLanding.getCategoryId().getText(), categoryLanding.getCategoryName().getText());
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * On cancelling addition of new category.
     *
     * @return
     * @throws IOException
     */
    @RequestMapping (
            value = "/edit",
            method = RequestMethod.POST,
            params = {"cancel_Edit"}
    )
    public String cancelEdit(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel business category qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:" + businessPage;
    }
}
