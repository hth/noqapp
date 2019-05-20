package com.noqapp.view.controller.business.advertisement;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.AdvertisementEntity;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AdvertisementService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.FileService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.controller.emp.AdvertisementController;
import com.noqapp.view.form.business.AdvertisementForm;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 2019-05-16 23:19
 */
@SuppressWarnings ({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/business/advertisement")
public class BusinessAdvertisementController {
    private static final Logger LOG = LoggerFactory.getLogger(AdvertisementController.class);

    private String bucketName;
    private String nextPage;
    private String addNewBusinessAdvertisementFlow;

    private ImageValidator imageValidator;
    private BusinessUserService businessUserService;
    private AdvertisementService advertisementService;
    private FileService fileService;
    private ApiHealthService apiHealthService;

    @Autowired
    public BusinessAdvertisementController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${nextPage:/business/advertisement/landing}")
        String nextPage,

        @Value("${newAdvertisementFlow:redirect:/store/addNewBusinessAdvertisement.htm}")
        String addNewBusinessAdvertisementFlow,

        ImageValidator imageValidator,
        BusinessUserService businessUserService,
        AdvertisementService advertisementService,
        FileService fileService,
        ApiHealthService apiHealthService
    ) {
        this.bucketName = bucketName;
        this.nextPage = nextPage;
        this.addNewBusinessAdvertisementFlow = addNewBusinessAdvertisementFlow;

        this.imageValidator = imageValidator;
        this.businessUserService = businessUserService;
        this.advertisementService = advertisementService;
        this.fileService = fileService;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping(value = "/landing", produces = "text/html;charset=UTF-8")
    public String landing(
        @ModelAttribute("advertisementForm")
        AdvertisementForm advertisementForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null == businessUser) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landed on payout page qid={} level={}", queueUser.getQueueUserId(), queueUser.getUserLevel());
        /* Above condition to make sure users with right roles and access gets access. */

        advertisementForm.setAdvertisements(advertisementService.findAllAdvertisements(businessUser.getBizName().getId()));
        return nextPage;
    }

    @GetMapping(value = "/create", produces = "text/html;charset=UTF-8")
    public String create(RedirectAttributes redirectAttributes) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed to add new business advertisement {} for {}", queueUser.getQueueUserId(), businessUser.getBizName().getId());

        redirectAttributes.addFlashAttribute("bizNameId", businessUser.getBizName().getId());
        return addNewBusinessAdvertisementFlow;
    }

    @GetMapping(value = "/edit/{advertisementId}", produces = "text/html;charset=UTF-8")
    public String edit(
        @PathVariable("advertisementId")
        ScrubbedInput advertisementId,

        RedirectAttributes redirectAttributes
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        LOG.info("Landed to add new business advertisement {} for {}", queueUser.getQueueUserId(), businessUser.getBizName().getId());

        redirectAttributes.addFlashAttribute("advertisementId", advertisementId.getText());
        redirectAttributes.addFlashAttribute("bizNameId", businessUser.getBizName().getId());
        return addNewBusinessAdvertisementFlow;
    }

    @PostMapping(value = "/delete", produces = "text/html;charset=UTF-8")
    public String delete(
        @ModelAttribute("advertisementForm")
        AdvertisementForm advertisementForm,

        HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to delete advertisement {} for {}", queueUser.getQueueUserId(), advertisementForm.getAdvertisementId());

        AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementForm.getAdvertisementId());
        advertisement.markAsDeleted();
        advertisementService.save(advertisement);

        return "redirect:/business/advertisement/landing.htm";
    }

    /** For uploading article image. */
    @GetMapping(value = "/{advertisementId}/upload")
    public String upload(
        @PathVariable("advertisementId")
        ScrubbedInput advertisementId,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landing page to load advertisement images qid={} {}", queueUser.getQueueUserId(), advertisementId.getText());
        /* Above condition to make sure users with right roles and access gets access. */

        AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementId.getText());
        model.addAttribute("advertisementForm", AdvertisementForm.populate(advertisement));
        model.addAttribute("bucketName", bucketName);
        return "/business/advertisement/photo";
    }

    /** For uploading service image. */
    @PostMapping (value = "/upload", params = {"upload"})
    public String upload(
        @ModelAttribute("advertisementForm")
        AdvertisementForm advertisementForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest
    ) {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("uploading image qid={}", queueUser.getQueueUserId());
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
                    return "redirect:/business/advertisement/" + advertisementForm.getAdvertisementId() + "/upload.htm";
                }

                try {
                    processImage(advertisementForm.getAdvertisementId(), multipartFile);
                    return "redirect:/business/advertisement/" + advertisementForm.getAdvertisementId() + "/upload.htm";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        BusinessAdvertisementController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/business/advertisement/" + advertisementForm.getAdvertisementId() + "/upload.htm";
            }
        }
        return "redirect:/business/advertisement/" + advertisementForm.getAdvertisementId() + "/upload";
    }

    /** For uploading service image. */
    @PostMapping (value = "/upload", params = {"cancel_Upload"})
    public String upload() {
        return "redirect:/business/advertisement/landing.htm";
    }

    @PostMapping(value = "/deleteImage")
    public String deleteImage(
        @ModelAttribute("advertisementForm")
        AdvertisementForm advertisementForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Delete article image qid={}", queueUser.getQueueUserId());

        AdvertisementEntity advertisement = advertisementService.findAdvertisementById(advertisementForm.getAdvertisementId());
        fileService.deleteAdvertisementImage(advertisement, advertisementForm.getImageUrl());
        advertisement
            .setValidateStatus(ValidateStatusEnum.P)
            .getImageUrls().remove(advertisementForm.getImageUrl());
        advertisementService.save(advertisement);
        return "redirect:/business/advertisement/" + advertisementForm.getAdvertisementId() + "/upload.htm";
    }

    private void processImage(String advertisementId, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addAdvertisementImage(
                advertisementId,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                bufferedImage);
        }
    }
}
