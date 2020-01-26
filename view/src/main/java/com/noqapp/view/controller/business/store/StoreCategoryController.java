package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.StoreCategoryEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.domain.types.medical.PharmacyCategoryEnum;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.view.form.StoreCategoryForm;
import com.noqapp.view.validator.StoreCategoryValidator;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 3/22/18 2:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/category")
public class StoreCategoryController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreCategoryController.class);

    private String nextPage;
    private BizService bizService;
    private BusinessUserService businessUserService;
    private StoreCategoryService storeCategoryService;
    private StoreCategoryValidator storeCategoryValidator;
    private BusinessUserStoreService businessUserStoreService;

    @Autowired
    public StoreCategoryController(
            @Value("${nextPage:/business/storeCategory}")
            String nextPage,

            BizService bizService,
            BusinessUserService businessUserService,
            StoreCategoryService storeCategoryService,
            StoreCategoryValidator storeCategoryValidator,
            BusinessUserStoreService businessUserStoreService
    ) {
        this.nextPage = nextPage;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.storeCategoryService = storeCategoryService;
        this.storeCategoryValidator = storeCategoryValidator;
        this.businessUserStoreService = businessUserStoreService;
    }

    @GetMapping(value = "/{storeId}", produces = "text/html;charset=UTF-8")
    public String landing(
            @PathVariable("storeId")
            ScrubbedInput storeId,

            @ModelAttribute("storeCategoryForm")
            StoreCategoryForm storeCategoryForm,

            Model model,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on store category page qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.storeCategoryForm", model.asMap().get("result"));
            storeCategoryForm.setStoreCategoryId((ScrubbedInput) model.asMap().get("storeCategoryId"));
        } else {
            redirectAttrs.addFlashAttribute("storeCategoryForm", storeCategoryForm);
        }

        Map<String, String> categories;
        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        switch (bizStore.getBusinessType()) {
            case HS:
                switch (HealthCareServiceEnum.valueOf(bizStore.getBizCategoryId())) {
                    case XRAY:
                        categories = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.XRAY);
                        storeCategoryForm
                            .setBizStoreId(storeId)
                            .setCategories(categories)
                            .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                            .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                            .setBusinessType(bizStore.getBusinessType());
                        break;
                    case SONO:
                        categories = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.SONO);
                        storeCategoryForm
                            .setBizStoreId(storeId)
                            .setCategories(categories)
                            .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                            .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                            .setBusinessType(bizStore.getBusinessType());
                        break;
                    case SCAN:
                        categories = LabCategoryEnum.asMapWithNameAsKey_Self(LabCategoryEnum.SCAN);
                        storeCategoryForm
                            .setBizStoreId(storeId)
                            .setCategories(categories)
                            .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                            .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                            .setBusinessType(bizStore.getBusinessType());
                        break;
                    case PHYS:
                        break;
                    case PATH:
                        categories = LabCategoryEnum.asMapWithNameAsKey();
                        storeCategoryForm
                            .setBizStoreId(storeId)
                            .setCategories(categories)
                            .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                            .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                            .setBusinessType(bizStore.getBusinessType());
                        break;
                    default:
                        LOG.error("Reached unsupported condition={}", bizStore.getBizCategoryId());
                        throw new UnsupportedOperationException("Reached unsupported condition " + bizStore.getBizCategoryId());
                }
                break;
            case PH:
                categories = PharmacyCategoryEnum.asMapWithNameAsKey();
                storeCategoryForm
                    .setBizStoreId(storeId)
                    .setCategories(categories)
                    .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                    .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                    .setBusinessType(bizStore.getBusinessType());
                break;
            default:
                categories = storeCategoryService.getStoreCategoriesAsMap(storeId.getText());
                storeCategoryForm
                    .setBizStoreId(storeId)
                    .setCategories(categories)
                    .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                    .setCategoryCounts(storeCategoryService.countCategoryUse(categories.keySet(), storeId.getText()))
                    .setBusinessType(bizStore.getBusinessType());
        }
        return nextPage;
    }

    /** Add new category. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("storeCategoryForm")
            StoreCategoryForm storeCategoryForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeCategoryForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Adding store category qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }

        storeCategoryValidator.validate(storeCategoryForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/business/store/category/" + storeCategoryForm.getBizStoreId() + ".htm";
        }

        BizStoreEntity bizStore = bizService.getByStoreId(storeCategoryForm.getBizStoreId().getText());
        StoreCategoryEntity storeCategory = new StoreCategoryEntity()
                .setBizNameId(bizStore.getBizName().getId())
                .setBizStoreId(storeCategoryForm.getBizStoreId().getText())
                .setCategoryName(storeCategoryForm.getCategoryName().getText());
        storeCategoryService.save(storeCategory);
        return "redirect:" + "/business/store/category/" + storeCategoryForm.getBizStoreId() + ".htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping (value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding store category qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/business/store/landing.htm";
    }

    /** Edit landing category name. */
    @GetMapping (value = "/{storeId}/{storeCategoryId}/edit")
    public String editLanding(
            @PathVariable("storeId")
            ScrubbedInput storeId,

            @PathVariable("storeCategoryId")
            ScrubbedInput storeCategoryId,

            @ModelAttribute ("storeCategoryForm")
            StoreCategoryForm storeCategoryForm,

            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on editing store category page storeCategoryId={} bizStoreId={} qid={} userLevel={}",
                storeCategoryId.getText(),
                storeId.getText(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        StoreCategoryEntity storeCategory = storeCategoryService.findOne(storeCategoryId.getText());
        storeCategoryForm
                .setStoreCategoryId(new ScrubbedInput(storeCategory.getId()))
                .setCategoryName(new ScrubbedInput(storeCategory.getCategoryName()));

        redirectAttrs.addFlashAttribute("storeCategoryForm", storeCategoryForm);
        return "redirect:" + "/business/store/category/" + storeId.getText() + ".htm";
    }

    /** Edit landing category name. */
    @PostMapping(value = "/edit")
    public String edit(
            @ModelAttribute ("storeCategoryForm")
            StoreCategoryForm storeCategoryForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeCategoryForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Edit store category id={} bizStoreId={} qid={} userLevel={}",
                storeCategoryForm.getStoreCategoryId(),
                storeCategoryForm.getBizStoreId(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        storeCategoryValidator.validate(storeCategoryForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("storeCategoryId", storeCategoryForm.getStoreCategoryId());
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/business/store/category/" + storeCategoryForm.getBizStoreId().getText() + ".htm";
        }

        StoreCategoryEntity storeCategory = storeCategoryService.findOne(storeCategoryForm.getStoreCategoryId().getText());
        storeCategory
                .setCategoryName(storeCategoryForm.getCategoryName().getText());
        storeCategoryService.save(storeCategory);
        return "redirect:" + "/business/store/category/" + storeCategoryForm.getBizStoreId().getText() + ".htm";
    }

    /** On cancelling edit of product. */
    @PostMapping (value = "/edit", params = {"cancel_Edit"})
    public String cancelEdit(
            @ModelAttribute ("storeCategoryForm")
            StoreCategoryForm storeCategoryForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel editing store category qid={} userLevel={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/business/store/landing.htm";
    }

    /** Delete store category. */
    @PostMapping(value = "/delete")
    public String delete(
            @ModelAttribute ("storeCategoryForm")
            StoreCategoryForm storeCategoryForm,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeCategoryForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Delete store category id={} qid={} userLevel={}",
                storeCategoryForm.getStoreCategoryId().getText(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        StoreCategoryEntity storeCategory = storeCategoryService.findOne(storeCategoryForm.getStoreCategoryId().getText());
        storeCategoryService.delete(storeCategory);
        return "redirect:" + "/business/store/category/" + storeCategoryForm.getBizStoreId().getText() + ".htm";
    }
}
