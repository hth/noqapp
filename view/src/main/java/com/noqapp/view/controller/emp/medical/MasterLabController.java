package com.noqapp.view.controller.emp.medical;

import static com.noqapp.view.controller.access.UserProfileController.getMultipartFiles;

import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.medical.service.MasterLabService;
import com.noqapp.service.FtpService;
import com.noqapp.service.exceptions.CSVParsingException;
import com.noqapp.service.exceptions.CSVProcessingException;
import com.noqapp.service.exceptions.FailedTransactionException;
import com.noqapp.view.form.emp.medical.MasterLabForm;
import com.noqapp.view.validator.CSVFileValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileUtil;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.OutputStream;
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

    /** Gymnastic for PRG. */
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
        return empMedicalLanding;
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
        HttpServletRequest httpServletRequest
    ) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Uploading data to master lab {} as CSV qid={}", masterLabForm.getHealthCareService(), queueUser.getQueueUserId());

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

    /** Gets file of all products as zip in CSV format for preferred business store id. */
    @PostMapping(
        value = "/bulk/download",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void download(HttpServletResponse response) {
        boolean methodStatusSuccess = true;
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Downloading master med lab data as CSV tar qid={}", queueUser.getQueueUserId());

        try (DefaultFileSystemManager manager = new StandardFileSystemManager()) {
            manager.init();
            FileObject fileObject = masterLabService.getMasterTarGZ(manager);
            if (fileObject != null && fileObject.getContent() != null) {
                response.setHeader("Content-disposition", "attachment; filename=\"" + com.noqapp.common.utils.FileUtil.getFileName(fileObject) + "\"");
                response.setContentType("application/gzip");
                response.setContentLength((int) fileObject.getContent().getSize());
                try (OutputStream out = response.getOutputStream()) {
                    out.write(FileUtil.getContent(fileObject));
                } catch (IOException e) {
                    LOG.error("Failed to get file for reason={}", e.getLocalizedMessage(), e);
                }

                return;
            }

            LOG.warn("Failed getting lab file");
            response.setContentType("application/gzip");
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", ""));
            response.setContentLength(0);
        } catch (FileSystemException e) {
            LOG.error("Failed to get directory={} reason={}", FtpService.MASTER_MEDICAL, e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
        } catch (Exception e) {
            LOG.error("Failed getting lab qid={} message={}", queueUser.getQueueUserId(), e.getLocalizedMessage(), e);
            methodStatusSuccess = false;
        } finally {
            apiHealthService.insert(
                "/api/m/h/lab/file",
                "file",
                MasterLabController.class.getName(),
                Duration.between(start, Instant.now()),
                methodStatusSuccess ? HealthStatusEnum.G : HealthStatusEnum.F);
        }
    }
}
