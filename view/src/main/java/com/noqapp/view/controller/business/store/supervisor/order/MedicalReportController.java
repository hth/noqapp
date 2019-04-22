package com.noqapp.view.controller.business.store.supervisor.order;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.PurchaseOrderEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.domain.MedicalPathologyEntity;
import com.noqapp.medical.domain.MedicalRadiologyEntity;
import com.noqapp.medical.repository.MedicalPathologyManager;
import com.noqapp.medical.repository.MedicalRadiologyManager;
import com.noqapp.medical.service.MedicalFileService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.PurchaseOrderService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.form.business.MedicalReportForm;
import com.noqapp.view.validator.ImageAndPDFValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 2019-02-24 08:50
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/sup/order/medicalReport")
public class MedicalReportController {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalReportController.class);

    private String bucketName;
    private String uploadMedicalReport;

    private BizService bizService;
    private MedicalFileService medicalFileService;
    private PurchaseOrderService purchaseOrderService;
    private BusinessUserStoreService businessUserStoreService;
    private ImageAndPDFValidator imageAndPDFValidator;
    private ApiHealthService apiHealthService;

    private MedicalPathologyManager medicalPathologyManager;
    private MedicalRadiologyManager medicalRadiologyManager;

    @Autowired
    public MedicalReportController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${nextPage:/business/uploadMedicalReport}")
        String uploadMedicalReport,

        MedicalPathologyManager medicalPathologyManager,
        MedicalRadiologyManager medicalRadiologyManager,
        BizService bizService,
        MedicalFileService medicalFileService,
        PurchaseOrderService purchaseOrderService,
        BusinessUserStoreService businessUserStoreService,
        ImageAndPDFValidator imageAndPDFValidator,
        ApiHealthService apiHealthService
    ) {
        this.bucketName = bucketName;
        this.uploadMedicalReport = uploadMedicalReport;

        this.medicalPathologyManager = medicalPathologyManager;
        this.medicalRadiologyManager = medicalRadiologyManager;
        this.bizService = bizService;
        this.medicalFileService = medicalFileService;
        this.purchaseOrderService = purchaseOrderService;
        this.businessUserStoreService = businessUserStoreService;
        this.imageAndPDFValidator = imageAndPDFValidator;
        this.apiHealthService = apiHealthService;
    }

    /** Gymnastic for PRG. */
    @GetMapping(
        value = {"/current/{storeId}/{transactionId}", "/historical/{storeId}/{transactionId}"},
        produces = "text/html;charset=UTF-8")
    public String landing(
        @RequestHeader(value = "referer")
        String referer,

        @PathVariable("storeId")
        ScrubbedInput storeId,

        @PathVariable("transactionId")
        ScrubbedInput transactionId,

        @ModelAttribute("medicalReportForm")
        MedicalReportForm medicalReportForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), storeId.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on store category page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.medicalReportForm",
                model.asMap().get("resultImage"));
        }

        if (referer.contains("historical")) {
            PurchaseOrderEntity purchaseOrder = purchaseOrderService.findHistoricalByTransactionIdAndBizStore(transactionId.getText(), storeId.getText());
            if (purchaseOrder == null || purchaseOrder.getBusinessType() != BusinessTypeEnum.HS) {
                LOG.warn("Could not find transactionId={} qid={} having access as business user", transactionId.getText(), queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }
        } else {
            PurchaseOrderEntity purchaseOrder = purchaseOrderService.findByTransactionIdAndBizStore(transactionId.getText(), storeId.getText());
            if (purchaseOrder == null || purchaseOrder.getBusinessType() != BusinessTypeEnum.HS) {
                LOG.warn("Could not find transactionId={} qid={} having access as business user", transactionId.getText(), queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }
        }

        BizStoreEntity bizStore = bizService.getByStoreId(storeId.getText());
        medicalReportForm
            .setStoreId(storeId)
            .setTransactionId(transactionId)
            .setLabCategory(LabCategoryEnum.valueOf(bizStore.getBizCategoryId()))
            .setCodeQR(new ScrubbedInput(bizStore.getCodeQR()));

        MedicalRadiologyEntity medicalRadiology;
        MedicalPathologyEntity medicalPathology;
        switch (medicalReportForm.getLabCategory()) {
            case PATH:
                medicalPathology = medicalPathologyManager.findByTransactionId(transactionId.getText());
                medicalReportForm
                    .setRecordReferenceId(medicalPathology.getId())
                    .setImages(medicalPathology.getImages());
                break;
            case SONO:
            case SPEC:
            case SCAN:
            case XRAY:
            case MRI:
                medicalRadiology = medicalRadiologyManager.findByTransactionId(transactionId.getText());
                medicalReportForm
                    .setRecordReferenceId(medicalRadiology.getId())
                    .setImages(medicalRadiology.getImages());
                break;
            default:
                LOG.error("Reached unreachable condition {}", medicalReportForm.getLabCategory());
                throw new UnsupportedOperationException("Reached unreachable condition");
        }

        model.addAttribute("bucketName", bucketName);
        return uploadMedicalReport;
    }

    /** For uploading service image or pdf. */
    @PostMapping(value = "/upload", params = {"upload"})
    public String upload(
        @RequestHeader(value = "referer")
        String referer,

        @ModelAttribute("medicalReportForm")
        MedicalReportForm medicalReportForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), medicalReportForm.getStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Upload medical report for qid={} storeId={} transactionId={}", queueUser.getQueueUserId(), medicalReportForm.getStoreId(), medicalReportForm.getTransactionId());
        /* Above condition to make sure users with right roles and access gets access. */

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = UserProfileController.getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                imageAndPDFValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed validation");
                    //Re-direct to prevent resubmit
                    return redirectTo(referer, medicalReportForm.getStoreId().getText(), medicalReportForm.getTransactionId().getText());
                }

                try {
                    medicalFileService.processReport(medicalReportForm.getTransactionId().getText(), multipartFile, medicalReportForm.getLabCategory());
                    return redirectTo(referer, medicalReportForm.getStoreId().getText(),  medicalReportForm.getTransactionId().getText());
                } catch (Exception e) {
                    LOG.error("Failed medical report upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        MedicalReportController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return redirectTo(referer, medicalReportForm.getStoreId().getText(),  medicalReportForm.getTransactionId().getText());
            }
        }

        return redirectTo(referer, medicalReportForm.getStoreId().getText(),  medicalReportForm.getTransactionId().getText());
    }

    @PostMapping(value = "/upload", params = "cancel_Upload")
    public String cancel(
        @RequestHeader(value = "referer")
        String referer,

        @ModelAttribute("medicalReportForm")
        MedicalReportForm medicalReportForm
    ) {
        if (referer.contains("historical")) {
            return "redirect:/business/store/sup/historical/" + medicalReportForm.getCodeQR() + ".htm";
        } else {
            return "redirect:/business/store/sup/current/" + medicalReportForm.getCodeQR() + ".htm";
        }
    }

    @PostMapping(value = "/delete")
    public String delete(
        @RequestHeader(value = "referer")
        String referer,

        @ModelAttribute("medicalReportForm")
        MedicalReportForm medicalReportForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccessUsingStoreId(queueUser.getQueueUserId(), medicalReportForm.getStoreId().getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Delete medical lab report for qid={} storeId={} transactionId={}", queueUser.getQueueUserId(), medicalReportForm.getStoreId(), medicalReportForm.getTransactionId());
        /* Above condition to make sure users with right roles and access gets access. */

        medicalFileService.removeReport(
            queueUser.getQueueUserId(),
            medicalReportForm.getTransactionId().getText(),
            medicalReportForm.getFilename().getText(),
            medicalReportForm.getLabCategory());
        return redirectTo(referer, medicalReportForm.getStoreId().getText(),  medicalReportForm.getTransactionId().getText());
    }

    private String redirectTo(String referer, String storeId, String transactionId) {
        if (referer.contains("historical")) {
            return "redirect:/business/store/sup/order/medicalReport/historical/" + storeId + "/" + transactionId + ".htm";
        } else {
            return "redirect:/business/store/sup/order/medicalReport/current/" + storeId + "/" + transactionId + ".htm";
        }
    }
}
