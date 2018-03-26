package com.noqapp.view.controller.business.store;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.service.StoreProductService;
import com.noqapp.view.form.StoreProductForm;
import com.noqapp.view.validator.StoreProductValidator;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * hitender
 * 3/21/18 5:29 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/product")
public class StoreProductController {
    private static final Logger LOG = LoggerFactory.getLogger(StoreProductController.class);

    private String nextPage;

    private BizService bizService;
    private StoreCategoryService storeCategoryService;
    private StoreProductService storeProductService;
    private StoreProductValidator storeProductValidator;
    private BusinessUserStoreService businessUserStoreService;

    @Autowired
    public StoreProductController(
            @Value("${nextPage:/business/storeProductLanding}")
            String nextPage,

            BizService bizService,
            StoreCategoryService storeCategoryService,
            StoreProductService storeProductService,
            StoreProductValidator storeProductValidator,
            BusinessUserStoreService businessUserStoreService
    ) {
        this.nextPage = nextPage;

        this.bizService = bizService;
        this.storeCategoryService = storeCategoryService;
        this.storeProductService = storeProductService;
        this.storeProductValidator = storeProductValidator;
        this.businessUserStoreService = businessUserStoreService;
    }

    @GetMapping(value = "/{storeId}", produces = "text/html;charset=UTF-8")
    public String landing(
            @PathVariable("storeId")
            ScrubbedInput storeId,

            @ModelAttribute("storeProductForm")
            StoreProductForm storeProductForm,

            Model model,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on product page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.storeProductForm", model.asMap().get("result"));
            storeProductForm.setStoreProductId((ScrubbedInput) model.asMap().get("storeProductId"));
        } else {
            redirectAttrs.addFlashAttribute("storeProductForm", storeProductForm);
        }

        Map<String, String> categories = storeCategoryService.getStoreCategoriesAsMap(storeId.getText());
        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        storeProductForm
                .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                .setBizStoreId(storeId)
                .setStoreProducts(storeProductService.findAll(storeId.getText()))
                .setCategories(categories);

        return nextPage;
    }

    /** Add new product. */
    @PostMapping(value = "/add", params = {"add"})
    public String add(
            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Adding business product qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        storeProductValidator.validate(storeProductForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId() + ".htm";
        }

        StoreProductEntity storeProduct = new StoreProductEntity()
                .setBizStoreId(storeProductForm.getBizStoreId().getText())
                .setProductName(storeProductForm.getProductName().getText())
                .setProductPrice(null == storeProductForm.getProductPrice() ? null : storeProductForm.getProductPrice().getText())
                .setProductDescription(null == storeProductForm.getProductDescription() ? null : storeProductForm.getProductDescription().getText())
                .setStoreCategoryId(null == storeProductForm.getStoreCategoryId() ? null : storeProductForm.getStoreCategoryId().getText())
                .setProductFresh(storeProductForm.isProductFresh());
        storeProductService.save(storeProduct);
        return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId() + ".htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping (value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new product qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/business/landing.htm";
    }

    /** Edit landing category name. */
    @GetMapping (value = "/{storeId}/{storeProductId}/edit")
    public String editLanding(
            @PathVariable("storeId")
            ScrubbedInput storeId,

            @PathVariable("storeProductId")
            ScrubbedInput storeProductId,

            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm,

            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on editing product page storeProductId={} bizStoreId={} qid={} level={}",
                storeProductForm.getStoreProductId(),
                storeProductForm.getBizStoreId(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        StoreProductEntity storeProduct = storeProductService.findOne(storeProductId.getText());
        storeProductForm
                .setProductName(new ScrubbedInput(storeProduct.getProductName()))
                .setProductPrice(new ScrubbedInput(storeProduct.getProductPrice()))
                .setProductDescription(new ScrubbedInput(storeProduct.getProductDescription()))
                .setProductFresh(storeProduct.isProductFresh())
                /*  When not store category is set. Which results in exception in JSP due to NULL. */
                .setStoreCategoryId(StringUtils.isBlank(storeProduct.getStoreCategoryId()) ? new ScrubbedInput("") : new ScrubbedInput(storeProduct.getStoreCategoryId()));

        redirectAttrs.addFlashAttribute("storeProductForm", storeProductForm);
        return "redirect:" + "/business/store/product/" + storeId.getText() + ".htm";
    }

    /** Edit landing category name. */
    @PostMapping(value = "/edit")
    public String edit(
            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm,

            BindingResult result,
            RedirectAttributes redirectAttrs,
            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit product storeProductId={} bizStoreId={} qid={} level={}",
                storeProductForm.getStoreProductId(),
                storeProductForm.getBizStoreId(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        storeProductValidator.validate(storeProductForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("storeProductId", storeProductForm.getStoreProductId());
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId().getText() + ".htm";
        }

        StoreProductEntity storeProduct = storeProductService.findOne(storeProductForm.getStoreProductId().getText());
        storeProduct
                .setBizStoreId(storeProductForm.getBizStoreId().getText())
                .setProductName(storeProductForm.getProductName().getText())
                .setProductPrice(null == storeProductForm.getProductPrice() ? null : storeProductForm.getProductPrice().getText())
                .setProductDescription(null == storeProductForm.getProductDescription() ? null : storeProductForm.getProductDescription().getText())
                .setStoreCategoryId(null == storeProductForm.getStoreCategoryId() ? null : storeProductForm.getStoreCategoryId().getText())
                .setProductFresh(storeProductForm.isProductFresh());
        storeProductService.save(storeProduct);
        return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId().getText() + ".htm";
    }

    /** On cancelling edit of product. */
    @PostMapping (value = "/edit", params = {"cancel_Edit"})
    public String cancelEdit(
            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel product edit qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/business/landing.htm";
    }

    /** Delete product. */
    @PostMapping(value = "/delete")
    public String delete(
            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Delete product id={} qid={} level={}",
                storeProductForm.getStoreProductId().getText(),
                queueUser.getQueueUserId(),
                queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        StoreProductEntity storeProduct = storeProductService.findOne(storeProductForm.getStoreProductId().getText());
        storeProductService.delete(storeProduct);
        return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId().getText() + ".htm";
    }
}
