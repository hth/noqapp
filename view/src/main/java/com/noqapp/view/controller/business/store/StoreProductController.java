package com.noqapp.view.controller.business.store;

import static com.noqapp.view.controller.access.UserProfileController.getMultipartFiles;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.StoreProductEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ProductTypeEnum;
import com.noqapp.domain.types.UnitOfMeasurementEnum;
import com.noqapp.domain.types.medical.PharmacyCategoryEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.FileService;
import com.noqapp.service.StoreCategoryService;
import com.noqapp.service.StoreProductService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.form.StoreProductForm;
import com.noqapp.view.validator.CSVFileValidator;
import com.noqapp.view.validator.StoreProductValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    private ApiHealthService apiHealthService;
    private BizService bizService;
    private StoreCategoryService storeCategoryService;
    private StoreProductService storeProductService;
    private StoreProductValidator storeProductValidator;
    private BusinessUserStoreService businessUserStoreService;
    private FileService fileService;
    private CSVFileValidator csvFileValidator;

    @Autowired
    public StoreProductController(
            @Value("${nextPage:/business/storeProductLanding}")
            String nextPage,

            ApiHealthService apiHealthService,
            BizService bizService,
            StoreCategoryService storeCategoryService,
            StoreProductService storeProductService,
            StoreProductValidator storeProductValidator,
            BusinessUserStoreService businessUserStoreService,
            FileService fileService,
            CSVFileValidator csvFileValidator
    ) {
        this.nextPage = nextPage;

        this.apiHealthService = apiHealthService;
        this.bizService = bizService;
        this.storeCategoryService = storeCategoryService;
        this.storeProductService = storeProductService;
        this.storeProductValidator = storeProductValidator;
        this.businessUserStoreService = businessUserStoreService;
        this.fileService = fileService;
        this.csvFileValidator = csvFileValidator;
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
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
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

        Map<String, String> categories;
        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        switch (bizStore.getBusinessType()) {
            case PH:
                categories = PharmacyCategoryEnum.asMap();
                break;
            default:
                categories = storeCategoryService.getStoreCategoriesAsMap(storeId.getText());
        }
        storeProductForm
                .setDisplayName(new ScrubbedInput(bizStore.getDisplayName()))
                .setBizStoreId(storeId)
                .setStoreProducts(storeProductService.findAll(storeId.getText()))
                .setCategories(categories)
                .setProductTypes(ProductTypeEnum.values())
                .setUnitOfMeasurements(UnitOfMeasurementEnum.values())
                .setBusinessType(bizStore.getBusinessType());
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
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
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
                .setProductPrice(StringUtils.isBlank(storeProductForm.getProductPrice().getText())
                    ? 0 : new BigDecimal(storeProductForm.getProductPrice().getText()).multiply(new BigDecimal(100)).intValue())
                .setProductDiscount(StringUtils.isBlank(storeProductForm.getProductDiscount().getText())
                    ? 0 : new BigDecimal(storeProductForm.getProductDiscount().getText()).multiply(new BigDecimal(100)).intValue())
                .setProductInfo(null == storeProductForm.getProductInfo() ? null : storeProductForm.getProductInfo().getText())
                .setStoreCategoryId(null == storeProductForm.getStoreCategoryId() ? null : storeProductForm.getStoreCategoryId().getText())
                .setProductType(ProductTypeEnum.valueOf(storeProductForm.getProductType().getText()))
                .setUnitOfMeasurement(UnitOfMeasurementEnum.valueOf(storeProductForm.getUnitOfMeasurement().getText()))
                .setPackageSize(new BigDecimal(storeProductForm.getPackageSize().getText()).intValue())
                .setUnitValue(new BigDecimal(storeProductForm.getUnitValue().getText()).intValue());
        storeProductService.save(storeProduct);
        return "redirect:" + "/business/store/product/" + storeProductForm.getBizStoreId() + ".htm";
    }

    /** On cancelling addition of new product. */
    @PostMapping (value = "/add", params = {"cancel_Add"})
    public String cancelAdd() {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Cancel adding new product qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        return "redirect:/business/store/landing.htm";
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
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
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
                .setProductPrice(new ScrubbedInput(new BigDecimal(storeProduct.getProductPrice()).divide(new BigDecimal(100), MathContext.DECIMAL64).toString()))
                .setProductDiscount(new ScrubbedInput(new BigDecimal(storeProduct.getProductDiscount()).divide(new BigDecimal(100), MathContext.DECIMAL64).toString()))
                .setProductInfo(new ScrubbedInput(storeProduct.getProductInfo()))
                .setProductType(new ScrubbedInput(storeProduct.getProductType().name()))
                .setUnitOfMeasurement(new ScrubbedInput(storeProduct.getUnitOfMeasurement().name()))
                /*  When not store category is set. Which results in exception in JSP due to NULL. */
                .setStoreCategoryId(StringUtils.isBlank(storeProduct.getStoreCategoryId()) ? new ScrubbedInput("") : new ScrubbedInput(storeProduct.getStoreCategoryId()))
                .setUnitOfMeasurement(new ScrubbedInput(storeProduct.getUnitOfMeasurement().name()))
                .setPackageSize(new ScrubbedInput(storeProduct.getPackageSize()))
                .setUnitValue(new ScrubbedInput(storeProduct.getUnitValue()));

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
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
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
                .setProductPrice(null == storeProductForm.getProductPrice() ? 0 : new BigDecimal(storeProductForm.getProductPrice().getText()).multiply(new BigDecimal(100)).intValue())
                .setProductDiscount(null == storeProductForm.getProductDiscount() ? 0 : new BigDecimal(storeProductForm.getProductDiscount().getText()).multiply(new BigDecimal(100)).intValue())
                .setProductInfo(null == storeProductForm.getProductInfo() ? null : storeProductForm.getProductInfo().getText())
                .setStoreCategoryId(null == storeProductForm.getStoreCategoryId() ? null : storeProductForm.getStoreCategoryId().getText())
                .setProductType(ProductTypeEnum.valueOf(storeProductForm.getProductType().getText()))
                .setUnitOfMeasurement(UnitOfMeasurementEnum.valueOf(storeProductForm.getUnitOfMeasurement().getText()))
                .setPackageSize(new BigDecimal(storeProductForm.getPackageSize().getText()).intValue())
                .setUnitValue(new BigDecimal(storeProductForm.getUnitValue().getText()).intValue());
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

        return "redirect:/business/store/landing.htm";
    }

    /** Delete product. */
    @PostMapping(value = "/delete")
    public String delete(
            @ModelAttribute ("storeProductForm")
            StoreProductForm storeProductForm,

            HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
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

    /** Preferred product creates a new zip file for merchant app to download. */
    @PostMapping(value = "/preferredRefresh")
    public String preferredRefresh(
        @ModelAttribute ("storeProductForm")
        StoreProductForm storeProductForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeProductForm.getBizStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Forced refresh preferred product bizStoreId={} qid={} level={}",
            storeProductForm.getBizStoreId().getText(),
            queueUser.getQueueUserId(),
            queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        fileService.createPreferredBusinessFiles(storeProductForm.getBizStoreId().getText());
        return "redirect:/business/store/landing.htm";
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/bulk/{codeQR}")
    public String bulk(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on bulk upload page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.fileUploadForm",
                model.asMap().get("resultImage"));
        }
        redirectAttrs.addAttribute("codeQR", codeQR);

        apiHealthService.insert(
            "/bulk/{codeQR}",
            "bulk",
            StoreProductController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "/business/storeProductBulk";
    }

    @PostMapping(value = "/bulk/upload", params = "cancel_Upload")
    public String cancel() {
        return "redirect:/business/store/landing.htm";
    }

    @PostMapping(value = "/bulk/upload", params = "upload")
    public String upload(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        @RequestParam("codeQR")
        ScrubbedInput codeQR,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Uploading store product CSV qid={} codeQR={}", queueUser.getQueueUserId(), codeQR);

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                csvFileValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed CSV validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
                }

                try {
                    int recordsUpdated = storeProductService.bulkUpdateStoreProduct(multipartFile.getInputStream(), codeQR.getText(), queueUser.getQueueUserId());
                    redirectAttrs
                        .addFlashAttribute("uploadSuccess", true)
                        .addFlashAttribute("recordsUpdated", recordsUpdated);
                    return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
                } catch (CSVParsingException e) {
                    LOG.warn("Failed parsing CSV file codeQR={} reason={}", codeQR, e.getLocalizedMessage());
                    ObjectError error = new ObjectError("fileUploadForm.file","Failed to parser file");
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
                } catch (CSVProcessingException e) {
                    LOG.warn("Failed processing CSV file codeQR={} reason={}", codeQR, e.getLocalizedMessage());
                    ObjectError error = new ObjectError("fileUploadForm.file","Failed processing " + e.getLocalizedMessage());
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
                } catch (Exception e) {
                    LOG.error("document upload failed reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/bulk/upload",
                        "upload",
                        StoreProductController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
            }
        }
        return "redirect:/business/store/product/bulk/" + codeQR + ".htm";
    }

    /** Gets file of all products as zip in CSV format for preferred business store id. */
    @PostMapping(
        value = "/bulk/download",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void download(
        @RequestParam("codeQR")
        ScrubbedInput codeQR,

        HttpServletResponse response
    ) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Downloading store product CSV qid={} codeQR={}", queueUser.getQueueUserId(), codeQR);

        try {
            File file = storeProductService.bulkStoreProductCSVFile(codeQR.getText());
            if (file != null) {
                response.setHeader("Content-disposition", "attachment; filename=\"" +file.getName() + "\"");
                response.setContentType("text/csv");
                response.setContentLength((int)file.length());
                try (OutputStream out = response.getOutputStream()) {
                    FileUtils.copyFile(file, out);
                } catch (IOException e) {
                    LOG.error("Failed to get file for codeQR={} reason={}", codeQR, e.getLocalizedMessage(), e);
                }

                return;
            }

            LOG.warn("Failed getting preferred file for codeQR={}", codeQR);
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", ""));
            response.setContentLength(0);
        } catch (Exception e) {
            LOG.error("Failed getting preferred store qid={} codeQR={} message={}", queueUser.getQueueUserId(), codeQR, e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
        } finally {
            apiHealthService.insert(
                "/bulk/download",
                "download",
                StoreProductController.class.getName(),
                Duration.between(start, Instant.now()),
                methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }
}
