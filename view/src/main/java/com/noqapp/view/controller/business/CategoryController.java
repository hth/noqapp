package com.noqapp.view.controller.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizCategoryEntity;
import com.noqapp.domain.BizNameEntity;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.helper.QueueDetail;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.view.form.business.BusinessLandingForm;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
    private String business;
    private String storeByCategoryPage;

    private BizService bizService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreService businessUserStoreService;
    private BusinessCategoryValidator businessCategoryValidator;

    @Autowired
    public CategoryController(
            @Value("${nextPage:/business/category}")
            String nextPage,

            @Value ("${business:/business/landing.htm}")
            String business,

            @Value ("${storeByCategoryPage:/business/storeByCategory}")
            String storeByCategoryPage,

            BizService bizService,
            BusinessUserService businessUserService,
            BusinessUserStoreService businessUserStoreService,
            BusinessCategoryValidator businessCategoryValidator) {
        this.nextPage = nextPage;
        this.business = business;
        this.storeByCategoryPage = storeByCategoryPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.businessUserStoreService = businessUserStoreService;
        this.businessCategoryValidator = businessCategoryValidator;
    }

    /**
     * Loading landing page for business category.
     * Gymnastic for PRG.
     */
    @GetMapping
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
            categoryLanding.setBizCategoryId((ScrubbedInput) model.asMap().get("bizCategoryId"));
        } else {
            redirectAttrs.addFlashAttribute("categoryLanding", categoryLanding);
        }

        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
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
     */
    @PostMapping (value = "/add", params = {"add"})
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
     */
    @PostMapping (value = "/add", params = {"cancel_Add"})
    public String cancelAdd(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel business category qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:" + business;
    }

    /**
     * Edit landing category name.
     */
    @GetMapping (value = "/{bizCategoryId}/edit")
    public String editLanding(
            @PathVariable("bizCategoryId")
            ScrubbedInput bizCategoryId,

            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding,

            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on editing category business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        BizCategoryEntity bizCategory = bizService.findByBizCategoryId(bizCategoryId.getText());
        categoryLanding
                .setBizCategoryId(bizCategoryId)
                .setBizNameId(new ScrubbedInput(bizCategory.getBizNameId()))
                .setCategoryName(new ScrubbedInput(bizCategory.getCategoryName()));


        redirectAttrs.addFlashAttribute("categoryLanding", categoryLanding);
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * Edit landing category name.
     */
    @PostMapping(value = "/edit")
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
            redirectAttrs.addFlashAttribute("bizCategoryId", categoryLanding.getBizCategoryId());
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + ".htm";
        }

        bizService.updateBizCategoryName(categoryLanding.getBizCategoryId().getText(), categoryLanding.getCategoryName().getText());
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * On cancelling addition of new category.
     */
    @PostMapping (value = "/edit", params = {"cancel_Edit"})
    public String cancelEdit(
            @ModelAttribute ("categoryLanding")
            CategoryLandingForm categoryLanding
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel business category qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:" + business;
    }

    /**
     * List stores belonging to selected category name.
     */
    @GetMapping(value = "/{bizCategoryId}/storeByCategory")
    public String storeByCategory(
            @PathVariable("bizCategoryId")
            ScrubbedInput bizCategoryId,

            @ModelAttribute ("businessLandingForm")
            BusinessLandingForm businessLandingForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on editing category business page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        BizNameEntity bizName = businessUser.getBizName();
        businessLandingForm.setBizName(bizName.getBusinessName());
        businessLandingForm.setCategories(bizService.getBusinessCategoriesAsMap(businessUser.getBizName().getId()));
        List<BizStoreEntity> bizStores = bizService.getBizStoresByCategory(bizCategoryId.getText(), businessUser.getBizName().getId());
        businessLandingForm.setBizStores(bizStores);
        for (BizStoreEntity bizStore : bizStores) {
            QueueDetail queueDetail = new QueueDetail()
                    .setId(bizStore.getId())
                    .setAssignedToQueue(businessUserStoreService.findNumberOfPeopleAssignedToQueue(bizStore.getId()))
                    .setPendingApprovalToQueue(businessUserStoreService.findNumberOfPeoplePendingApprovalToQueue(bizStore.getId()));

            businessLandingForm.addQueueDetail(queueDetail);
        }

        return storeByCategoryPage;
    }
}
