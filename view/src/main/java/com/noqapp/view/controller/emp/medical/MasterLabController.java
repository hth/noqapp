package com.noqapp.view.controller.emp.medical;

import static com.noqapp.view.controller.access.UserProfileController.getMultipartFiles;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.service.MasterLabService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.service.exceptions.FailedTransactionException;
import com.noqapp.view.form.emp.medical.MasterLabForm;
import com.noqapp.view.validator.CSVFileValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 2018-12-11 10:32
 */
@Controller
@RequestMapping(value = "/emp/medical/masterLab")
public class MasterLabController {
    private static final Logger LOG = LoggerFactory.getLogger(EmpMedicalLandingController.class);

    private String empMedicalLanding;

    private CSVFileValidator csvFileValidator;
    private MasterLabService masterLabService;
    private ApiHealthService apiHealthService;

    @Autowired
    public MasterLabController(
        @Value("${empMedicalLanding:/emp/medical/bulk}")
        String empMedicalLanding,

        CSVFileValidator csvFileValidator,
        MasterLabService masterLabService,
        ApiHealthService apiHealthService
    ) {
        this.empMedicalLanding = empMedicalLanding;

        this.csvFileValidator = csvFileValidator;
        this.masterLabService = masterLabService;
        this.apiHealthService = apiHealthService;
    }

    /**
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/bulk")
    public String bulk(
        @ModelAttribute("masterLabForm")
        MasterLabForm masterLabForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on bulk upload page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute("org.springframework.validation.BindingResult.masterLabForm", model.asMap().get("resultImage"));
        }

        apiHealthService.insert(
            "/bulk",
            "bulk",
            MasterLabController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "/emp/medical/bulk";
    }

    @PostMapping(value = "/bulk/upload", params = "cancel_Upload")
    public String cancel() {
        return "redirect:/emp/medical/landing.htm";
    }

    @PostMapping(value = "/bulk/upload", params = "upload")
    public String upload(
        @ModelAttribute("masterLabForm")
        MasterLabForm masterLabForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Uploading store product CSV qid={} healthCareService={}", queueUser.getQueueUserId(), masterLabForm.getHealthCareService());

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
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                }

                try {
                    int recordsUpdated = masterLabService.bulkUpdateStoreProduct(multipartFile.getInputStream(), masterLabForm.getHealthCareService());
                    redirectAttrs
                        .addFlashAttribute("uploadSuccess", true)
                        .addFlashAttribute("recordsUpdated", recordsUpdated);
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                } catch (CSVParsingException e) {
                    LOG.warn("Failed parsing CSV file healthCareService={} reason={}", masterLabForm.getHealthCareService(), e.getLocalizedMessage());
                    methodStatusSuccess = false;
                    ObjectError error = new ObjectError("file","Failed to parser file " + e.getLocalizedMessage());
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                } catch (CSVProcessingException e) {
                    LOG.warn("Failed processing CSV data healthCareService={} reason={}", masterLabForm.getHealthCareService(), e.getLocalizedMessage());
                    methodStatusSuccess = false;
                    ObjectError error = new ObjectError("file","Failed processing " + e.getLocalizedMessage());
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                } catch (FailedTransactionException e) {
                    LOG.error("Document upload transaction failed reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    methodStatusSuccess = false;
                    ObjectError error = new ObjectError("file", e.getLocalizedMessage());
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                } catch (Exception e) {
                    LOG.error("Document upload failed reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    methodStatusSuccess = false;
                    ObjectError error = new ObjectError("file","Failed processing " + e.getLocalizedMessage());
                    result.addError(error);
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
                } finally {
                    apiHealthService.insert(
                        "/bulk/upload",
                        "upload",
                        MasterLabController.class.getName(),
                        Duration.between(start, Instant.now()),
                        methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
                }
            }
        }
        return "redirect:" + "/emp/medical/masterLab/bulk" + ".htm";
    }
}
