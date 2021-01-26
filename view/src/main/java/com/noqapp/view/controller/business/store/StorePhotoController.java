package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.BizStoreElasticService;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserStoreService;
import com.noqapp.service.FileService;
import com.noqapp.service.StoreHourService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.controller.business.BusinessPhotoController;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.validator.ImageValidator;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 8/31/18 1:13 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/photo")
public class StorePhotoController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessPhotoController.class);

    private String bucketName;

    private BizService bizService;
    private BusinessUserStoreService businessUserStoreService;
    private FileService fileService;
    private StoreHourService storeHourService;
    private ApiHealthService apiHealthService;
    private ImageValidator imageValidator;
    private BizStoreElasticService bizStoreElasticService;

    @Autowired
    public StorePhotoController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        BizService bizService,
        BusinessUserStoreService businessUserStoreService,
        FileService fileService,
        StoreHourService storeHourService,
        ApiHealthService apiHealthService,
        ImageValidator imageValidator,
        BizStoreElasticService bizStoreElasticService
    ) {
        this.bucketName = bucketName;

        this.bizService = bizService;
        this.businessUserStoreService = businessUserStoreService;
        this.fileService = fileService;
        this.storeHourService = storeHourService;
        this.apiHealthService = apiHealthService;
        this.imageValidator = imageValidator;
        this.bizStoreElasticService = bizStoreElasticService;
    }

    /** For uploading service image. */
    @GetMapping(value = "/uploadServicePhoto/{codeQR}")
    public String uploadServicePhoto(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Landing page to load service or menu images");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on store image codeQR={} qid={} userLevel={}", codeQR.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.fileUploadForm",
                model.asMap().get("resultImage"));
        } else {
            redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm);
        }

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());
        model.addAttribute("images", bizStore.getStoreServiceImages());
        model.addAttribute("codeQR", codeQR.getText());
        model.addAttribute("bucketName", bucketName);
        return "/business/storeServicePhoto";
    }

    /** For uploading interior image. */
    @GetMapping(value = "/uploadInteriorPhoto/{codeQR}")
    public String uploadInteriorPhoto(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Landing page to load store interior exterior images");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Landed on store image codeQR={} qid={} userLevel={}", codeQR.getText(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.fileUploadForm",
                model.asMap().get("resultImage"));
        } else {
            redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm);
        }

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());
        model.addAttribute("images", bizStore.getStoreInteriorImages());
        model.addAttribute("codeQR", codeQR.getText());
        model.addAttribute("bucketName", bucketName);
        return "/business/storeInteriorPhoto";
    }

    @PostMapping(value = "/deleteServicePhoto")
    public String deleteServicePhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        RedirectAttributes redirectAttrs,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Delete store service image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String codeQR = request.getParameter("codeQR");
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Delete store image codeQR={} qid={} userLevel={}", codeQR, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        fileService.deleteImage(queueUser.getQueueUserId(), request.getParameter("storeServiceImage"), codeQR);

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        Set<String> images = bizStore.getStoreServiceImages();
        images.remove(request.getParameter("storeServiceImage"));
        bizService.saveStore(bizStore, "Deleted Store Menu/Service Image");
        bizStoreElasticService.save(DomainConversion.getAsBizStoreElastic(bizStore, storeHourService.findAllStoreHours(bizStore.getId())));
        bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
        redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm.setMessage("Store service image deleted successfully"));
        return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
    }

    @PostMapping(value = "/deleteInteriorPhoto")
    public String deleteInteriorPhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        RedirectAttributes redirectAttrs,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Delete store interior image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String codeQR = request.getParameter("codeQR");
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Delete store image codeQR={} qid={} userLevel={}", codeQR, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        fileService.deleteImage(queueUser.getQueueUserId(), request.getParameter("storeInteriorImage"), codeQR);

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        Set<String> images = bizStore.getStoreInteriorImages();
        images.remove(request.getParameter("storeInteriorImage"));
        bizService.saveStore(bizStore, "Delete Store Interior Image");
        bizStoreElasticService.save(DomainConversion.getAsBizStoreElastic(bizStore, storeHourService.findAllStoreHours(bizStore.getId())));
        bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
        redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm.setMessage("Store image deleted successfully"));
        return "redirect:/business/store/photo/uploadInteriorPhoto/" + codeQR + ".htm";
    }

    /** For uploading service image. */
    @PostMapping (value = "/uploadServicePhoto")
    public String uploadServicePhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("uploading image qid={}", queueUser.getQueueUserId());
        String codeQR = httpServletRequest.getParameter("codeQR");
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Upload store image codeQR={} qid={} userLevel={}", codeQR, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = UserProfileController.getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                imageValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
                }

                try {
                    processServiceImage(queueUser.getQueueUserId(), codeQR, multipartFile, true);
                    redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm.setMessage("Store service image uploaded successfully"));
                    return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/uploadServicePhoto",
                        "uploadServicePhoto",
                        StorePhotoController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
            }
        }
        return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
    }

    @PostMapping(value = "/uploadServicePhoto", params = "cancel_Upload")
    public String cancelUploadServicePhoto() {
        LOG.info("Cancel file upload for store service");
        return "redirect:/business/store/landing.htm";
    }

    /** For uploading service image. */
    @PostMapping (value = "/uploadInteriorPhoto")
    public String uploadInteriorPhoto(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("uploading image qid={}", queueUser.getQueueUserId());
        String codeQR = httpServletRequest.getParameter("codeQR");
        if (!businessUserStoreService.hasAccess(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        LOG.info("Upload store image codeQR={} qid={} userLevel={}", codeQR, queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = UserProfileController.getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                imageValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/business/store/photo/uploadInteriorPhoto/" + codeQR + ".htm";
                }

                try {
                    processServiceImage(queueUser.getQueueUserId(), codeQR, multipartFile, false);
                    redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm.setMessage("Store image uploaded successfully"));
                    return "redirect:/business/store/photo/uploadInteriorPhoto/" + codeQR + ".htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/uploadInteriorPhoto",
                        "uploadInteriorPhoto",
                        StorePhotoController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/store/photo/uploadInteriorPhoto/" + codeQR + ".htm";
            }
        }
        return "redirect:/business/store/photo/uploadInteriorPhoto/" + codeQR + ".htm";
    }

    @PostMapping(value = "/uploadInteriorPhoto", params = "cancel_Upload")
    public String cancelUploadInteriorPhoto() {
        LOG.info("Cancel file upload for store interior");
        return "redirect:/business/store/landing.htm";
    }

    private void processServiceImage(String qid, String codeQR, MultipartFile multipartFile, boolean service) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        BizStoreEntity bizStore = null;
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            bizStore = fileService.addStoreImage(
                qid,
                codeQR,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage,
                service);
        }

        if (null != bizStore) {
            bizStoreElasticService.save(DomainConversion.getAsBizStoreElastic(bizStore, storeHourService.findAllStoreHours(bizStore.getId())));
            bizStoreElasticService.updateSpatial(bizStore.getBizName().getId());
        }
    }
}
