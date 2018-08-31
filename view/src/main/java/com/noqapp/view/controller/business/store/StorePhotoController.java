package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.BusinessUserStoreManager;
import com.noqapp.service.BizService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.FileService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.controller.business.AdminBusinessLandingController;
import com.noqapp.view.controller.business.BusinessServicePhotoController;
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
    private static final Logger LOG = LoggerFactory.getLogger(BusinessServicePhotoController.class);

    private String bucketName;

    private BizService bizService;
    private BusinessUserService businessUserService;
    private BusinessUserStoreManager businessUserStoreManager;
    private FileService fileService;
    private ApiHealthService apiHealthService;
    private ImageValidator imageValidator;

    @Autowired
    public StorePhotoController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        BizService bizService,
        BusinessUserService businessUserService,
        BusinessUserStoreManager businessUserStoreManager,
        FileService fileService,
        ApiHealthService apiHealthService,
        ImageValidator imageValidator
    ) {
        this.bucketName = bucketName;

        this.bizService = bizService;
        this.businessUserService = businessUserService;
        this.businessUserStoreManager = businessUserStoreManager;
        this.fileService = fileService;
        this.apiHealthService = apiHealthService;
        this.imageValidator = imageValidator;
    }

    /** For uploading service image. */
    @GetMapping(value = "/uploadServicePhoto/{codeQR}")
    public String uploadServicePhoto(
        @PathVariable("codeQR")
        ScrubbedInput codeQR,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        LOG.info("Loading store service image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        if (!businessUserStoreManager.hasAccessUsingStoreId(queueUser.getQueueUserId(), codeQR.getText())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit business bizId={} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.fileUploadForm",
                model.asMap().get("resultImage"));
        }

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR.getText());
        model.addAttribute("storeServiceImages", bizStore.getStoreServiceImages());
        model.addAttribute("codeQR", codeQR.getText());
        model.addAttribute("bucketName", bucketName);
        return "/business/storeServicePhoto";
    }

    @PostMapping(value = "/deleteServicePhoto")
    public String deleteServicePhoto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("Delete store service image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        String codeQR = request.getParameter("codeQR");
        if (!businessUserStoreManager.hasAccessUsingStoreId(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit business bizId={} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        LOG.info("Delete businessServiceImage={}", request.getParameter("storeServiceImage"));
        fileService.deleteImage(queueUser.getQueueUserId(), request.getParameter("storeServiceImage"), codeQR);

        BizStoreEntity bizStore = bizService.findByCodeQR(codeQR);
        Set<String> businessServiceImages = bizStore.getStoreServiceImages();
        businessServiceImages.remove(request.getParameter("storeServiceImage"));
        bizService.saveStore(bizStore);
        return "redirect:/business/store/photo/uploadServicePhoto/" + codeQR + ".htm";
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
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        String codeQR = httpServletRequest.getParameter("codeQR");
        if (!businessUserStoreManager.hasAccessUsingStoreId(queueUser.getQueueUserId(), codeQR)) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Edit business bizId={} qid={} level={}", businessUser.getBizName().getId(), queueUser.getQueueUserId(), queueUser.getUserLevel());
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
                    processServiceImage(queueUser.getQueueUserId(), codeQR, multipartFile);
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

    private void processServiceImage(String qid, String codeQR, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addStoreImage(
                qid,
                codeQR,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage);
        }
    }
}
