package com.noqapp.view.controller.access;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;
import com.noqapp.service.market.PropertyService;
import com.noqapp.view.controller.business.store.PublishArticleController;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.validator.ImageValidator;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * hitender
 * 1/11/21 4:52 PM
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/marketplace")
public class MarketplaceController {
    private static final Logger LOG = LoggerFactory.getLogger(MarketplaceController.class);

    private String bucketName;
    private String postOnMarketplaceFlowActions;

    private AccountService accountService;
    private PropertyService propertyService;
    private FileService fileService;
    private ImageValidator imageValidator;
    private ApiHealthService apiHealthService;

    @Autowired
    public MarketplaceController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${postOnMarketplaceFlowActions:redirect:/access/postOnMarketplace.htm}")
        String postOnMarketplaceFlowActions,

        AccountService accountService,
        PropertyService propertyService,
        FileService fileService,
        ImageValidator imageValidator,
        ApiHealthService apiHealthService
    ) {
        this.bucketName = bucketName;
        this.postOnMarketplaceFlowActions = postOnMarketplaceFlowActions;

        this.accountService = accountService;
        this.propertyService = propertyService;
        this.fileService = fileService;
        this.imageValidator = imageValidator;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping(value = "/post")
    public String postOnMarketplace(RedirectAttributes redirectAttributes) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Requested post on marketplace {}", queueUser.getQueueUserId());
        if (accountService.accountOpenedInLast10Days(queueUser.getQueueUserId())) {
            redirectAttributes.addFlashAttribute("postingAllowed", true);
        } else {
            LOG.error("Cannot post to market place {}", queueUser.getQueueUserId());
            redirectAttributes.addFlashAttribute("postingAllowed", false);
        }
        return postOnMarketplaceFlowActions;
    }

    @GetMapping(value = "/edit/{businessTypeAsString}/{postId}")
    public String fetchPostOnMarketplace(
        @PathVariable("businessTypeAsString")
        String businessTypeAsString,

        @PathVariable("postId")
        String postId,

        RedirectAttributes redirectAttributes
    ) {
        LOG.info("Requested post on marketplace {}", postOnMarketplaceFlowActions);

        redirectAttributes.addFlashAttribute("businessTypeAsString", businessTypeAsString);
        redirectAttributes.addFlashAttribute("postId", postId);
        return postOnMarketplaceFlowActions;
    }

    /** For uploading article image. */
    @GetMapping(value = "/{businessTypeAsString}/{postId}/upload")
    public String upload(
        @PathVariable("businessTypeAsString")
        String businessTypeAsString,

        @PathVariable("postId")
        String postId,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (StringUtils.isBlank(postId)) {
            LOG.warn("Could not find postId={}", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landing page to load store images qid={}", queueUser.getQueueUserId());

        MarketplaceEntity marketplace;
        switch (BusinessTypeEnum.valueOf(businessTypeAsString)) {
            case PR:
                marketplace = propertyService.findOneById(postId);
                break;
            default:
                LOG.error("Reached unsupported condition={}", businessTypeAsString);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessTypeAsString);
        }
        model.addAttribute("bucketName", FtpService.marketBucketName(bucketName, marketplace.getBusinessType()));
        model.addAttribute("images", marketplace.getPostImages());
        model.addAttribute("postId", marketplace.getId());
        model.addAttribute("businessType", marketplace.getBusinessType().name());
        return "/access/marketplace/photo";
    }

    /** For uploading service image. */
    @PostMapping(value = "/uploadImage", params = {"upload"})
    public String upload(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Uploading market image qid={}", queueUser.getQueueUserId());
        /* Above condition to make sure users with right roles and access gets access. */

        String postId = httpServletRequest.getParameter("postId");
        String businessType = httpServletRequest.getParameter("businessType");

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
                    return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage.htm";
                }

                try {
                    processImage(queueUser.getQueueUserId(), postId, multipartFile);
                    return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage.htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        PublishArticleController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage.htm";
            }
        }
        return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage.htm";
    }

    /** For cancelling uploading service image. */
    @PostMapping (value = "/uploadImage", params = {"cancel_Upload"})
    public String upload() {
        return "redirect:/access/landing.htm";
    }

    @PostMapping(value = "/deleteImage")
    public String deleteImage(HttpServletRequest request) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Delete article image qid={}", queueUser.getQueueUserId());
        /* Above condition to make sure users with right roles and access gets access. */

        String postId = request.getParameter("postId");
        String businessType = request.getParameter("businessType");
        String imageId = request.getParameter("imageId");

        switch (BusinessTypeEnum.valueOf(businessType)) {
            case PR:
                PropertyEntity property = propertyService.findOneById(postId);
                if (property.getPostImages().contains(imageId)) {
                    fileService.deleteMarketImage(queueUser.getQueueUserId(), imageId, postId, property.getBusinessType());

                    property.getPostImages().remove(imageId);
                    propertyService.save(property);
                }
                break;
            default:
                LOG.error("Reached unsupported condition={}", businessType);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessType);
        }

        return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage.htm";
    }

    private void processImage(String qid, String postId, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addPropertyImage(
                qid,
                postId,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage);
        }
    }
}
