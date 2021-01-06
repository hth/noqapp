package com.noqapp.view.controller.business.store;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.FileService;
import com.noqapp.service.PublishArticleService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.form.PublishArticleForm;
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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 2018-12-26 12:00
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/store/publishArticle")
public class PublishArticleController {
    private static final Logger LOG = LoggerFactory.getLogger(PublishArticleController.class);

    private String bucketName;
    private String nextPage;
    private String publishArticleFlow;

    private ImageValidator imageValidator;
    private PublishArticleService publishArticleService;
    private FileService fileService;
    private ApiHealthService apiHealthService;

    @Autowired
    public PublishArticleController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${nextPage:/business/publishArticle/landing}")
        String nextPage,

        @Value("${publishArticleFlow:redirect:/store/publishArticle.htm}")
        String publishArticleFlow,

        ImageValidator imageValidator,
        PublishArticleService publishArticleService,
        FileService fileService,
        ApiHealthService apiHealthService
    ) {
        this.bucketName = bucketName;
        this.nextPage = nextPage;
        this.publishArticleFlow = publishArticleFlow;

        this.imageValidator = imageValidator;
        this.publishArticleService = publishArticleService;
        this.fileService = fileService;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to publish article {}", queueUser.getQueueUserId());
        List<PublishArticleForm> publishArticleForms = new LinkedList<>();
        List<PublishArticleEntity> publishArticles = publishArticleService.findAll(queueUser.getQueueUserId());
        for (PublishArticleEntity publishArticle : publishArticles) {
            publishArticleForms.add(
                PublishArticleForm.newInstance()
                    .setTitle(publishArticle.getTitle())
                    .setBannerImage(publishArticle.getBannerImage())
                    .setPublishId(new ScrubbedInput(publishArticle.getId()))
                    .setActive(publishArticle.isActive())
                    .setValidateStatus(publishArticle.getValidateStatus())
                    .setPublishDate(publishArticle.getPublishDate()));

        }
        model.addAttribute("publishArticleForms", publishArticleForms);
        return nextPage;
    }

    @GetMapping(value = "/newArticle", produces = "text/html;charset=UTF-8")
    public String newArticle() {
        LOG.info("Landed to publish new article");
        return publishArticleFlow;
    }

    @PostMapping(value = "/action")
    public String actionQueueSupervisor(
        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        RedirectAttributes redirectAttrs
    ) {
        LOG.info("Action on article={} action={}", publishArticleForm.getPublishId(), publishArticleForm.getAction());
        switch (publishArticleForm.getAction().getText()) {
            case "EDIT":
                redirectAttrs.addFlashAttribute("publishId", publishArticleForm.getPublishId().getText());
                break;
            case "OFFLINE":
                publishArticleService.takeOffOrOnline(publishArticleForm.getPublishId().getText(), false);
                break;
            case "ONLINE":
                publishArticleService.takeOffOrOnline(publishArticleForm.getPublishId().getText(), true);
                break;
            case "DELETE":
                PublishArticleEntity publishArticle = publishArticleService.findOne(publishArticleForm.getPublishId().getText());
                publishArticle.markAsDeleted();
                publishArticle.inActive();
                publishArticleService.save(publishArticle);
                break;
            default:
                LOG.warn("Reached un-reachable condition {}", publishArticleForm.getAction());
                throw new UnsupportedOperationException("Failed to update as the value supplied is invalid");
        }

        String goToPage;
        switch (publishArticleForm.getAction().getText()) {
            case "EDIT":
                goToPage = publishArticleFlow;
                break;
            default:
                goToPage = "redirect:/business/store/publishArticle/landing.htm";
        }
        return goToPage;
    }

    /** For uploading article image. */
    @GetMapping(value = "/{publishId}/upload")
    public String upload(
        @PathVariable("publishId")
        String publishId,

        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landing page to load store images qid={}", queueUser.getQueueUserId());
        if (!publishArticleService.exists(publishArticleForm.getPublishId().getText(), queueUser.getQueueUserId())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        /* Above condition to make sure users with right roles and access gets access. */

        PublishArticleEntity publishArticle = publishArticleService.findOne(publishArticleForm.getPublishId().getText());
        publishArticleForm
            .setPublishId(new ScrubbedInput(publishId))
            .setBannerImage(publishArticle.getBannerImage());
        model.addAttribute("bucketName", bucketName);
        return "/business/publishArticle/photo";
    }

    /** For uploading service image. */
    @PostMapping (value = "/upload", params = {"upload"})
    public String upload(
        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("uploading image qid={}", queueUser.getQueueUserId());
        if (!publishArticleService.exists(publishArticleForm.getPublishId().getText(), queueUser.getQueueUserId())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
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
                    return "redirect:/business/store/publishArticle/" + publishArticleForm.getPublishId() + "/upload.htm";
                }

                try {
                    processImage(publishArticleForm.getPublishId().getText(), multipartFile);
                    return "redirect:/business/store/publishArticle/" + publishArticleForm.getPublishId() + "/upload.htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        PublishArticleController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/store/publishArticle/" + publishArticleForm.getPublishId() + "/upload.htm";
            }
        }
        return "redirect:/business/store/publishArticle/" + publishArticleForm.getPublishId() + "/upload";
    }

    /** For uploading service image. */
    @PostMapping (value = "/upload", params = {"cancel_Upload"})
    public String upload() {
        return "redirect:/business/store/publishArticle/landing.htm";
    }

    @PostMapping(value = "/deleteImage")
    public String deleteImage(
        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Delete article image qid={}", queueUser.getQueueUserId());
        if (!publishArticleService.exists(publishArticleForm.getPublishId().getText(), queueUser.getQueueUserId())) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        /* Above condition to make sure users with right roles and access gets access. */

        PublishArticleEntity publishArticle = publishArticleService.findOne(publishArticleForm.getPublishId().getText());
        fileService.deleteArticleImage(publishArticle);
        publishArticle
            .setBannerImage(null)
            .setValidateStatus(ValidateStatusEnum.I);
        publishArticleService.save(publishArticle);
        return "redirect:/business/store/publishArticle/landing.htm";
    }

    private void processImage(String publishId, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addArticleImage(
                publishId,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage);
        }
    }
}
