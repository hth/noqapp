package com.noqapp.view.controller.access.markertplace;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.common.utils.Validate;
import com.noqapp.domain.PointEarnedEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.market.HouseholdItemEntity;
import com.noqapp.domain.market.MarketplaceEntity;
import com.noqapp.domain.market.PropertyRentalEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.BusinessTypeEnum;
import com.noqapp.domain.types.PointActivityEnum;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.repository.PointEarnedManager;
import com.noqapp.search.elastic.helper.DomainConversion;
import com.noqapp.search.elastic.service.MarketplaceElasticService;
import com.noqapp.service.AccountService;
import com.noqapp.service.FileService;
import com.noqapp.service.FtpService;
import com.noqapp.service.exceptions.NotAValidObjectIdException;
import com.noqapp.service.market.HouseholdItemService;
import com.noqapp.service.market.PropertyRentalService;
import com.noqapp.view.controller.access.UserProfileController;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.form.marketplace.MarketplaceForm;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private String nextPage;
    private String nextPagePropertyRental;
    private String nextPageHouseholdItem;

    private PropertyRentalService propertyRentalService;
    private HouseholdItemService householdItemService;
    private FileService fileService;
    private ImageValidator imageValidator;
    private MarketplaceElasticService marketplaceElasticService;
    private AccountService accountService;
    private PointEarnedManager pointEarnedManager;
    private ApiHealthService apiHealthService;

    @Autowired
    public MarketplaceController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value("${nextPage:/access/marketplace/landing}")
        String nextPage,

        @Value("${nextPage:/access/marketplace/propertyRental}")
        String nextPagePropertyRental,

        @Value("${nextPage:/access/marketplace/householdItem}")
        String nextPageHouseholdItem,

        PropertyRentalService propertyRentalService,
        HouseholdItemService householdItemService,
        FileService fileService,
        ImageValidator imageValidator,
        MarketplaceElasticService marketplaceElasticService,
        AccountService accountService,
        PointEarnedManager pointEarnedManager,
        ApiHealthService apiHealthService
    ) {
        this.bucketName = bucketName;
        this.nextPage = nextPage;
        this.nextPagePropertyRental = nextPagePropertyRental;
        this.nextPageHouseholdItem = nextPageHouseholdItem;

        this.propertyRentalService = propertyRentalService;
        this.householdItemService = householdItemService;
        this.fileService = fileService;
        this.imageValidator = imageValidator;
        this.marketplaceElasticService = marketplaceElasticService;
        this.accountService = accountService;
        this.pointEarnedManager = pointEarnedManager;
        this.apiHealthService = apiHealthService;
    }

    @GetMapping(value = "/post")
    public String postOnMarketplace(RedirectAttributes redirectAttributes) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Requested sent for marketplace {}", queueUser.getQueueUserId());
        return nextPage;
    }

    /** For uploading image. */
    @GetMapping(value = "/{businessTypeAsString}/{postId}/uploadImage")
    public String upload(
        @PathVariable("businessTypeAsString")
        String businessTypeAsString,

        @PathVariable("postId")
        String postId,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model,
        RedirectAttributes redirectAttrs,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (StringUtils.isBlank(postId) || !Validate.isValidObjectId(postId)) {
            LOG.warn("Could not find postId={}", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }
        LOG.info("Landing page to load store images qid={}", queueUser.getQueueUserId());

        MarketplaceEntity marketplace;
        try {
            switch (BusinessTypeEnum.valueOf(businessTypeAsString)) {
                case PR:
                    marketplace = propertyRentalService.findOneById(postId);
                    break;
                case HI:
                    marketplace = householdItemService.findOneById(postId);
                    break;
                default:
                    LOG.error("Reached unsupported condition={}", businessTypeAsString);
                    throw new UnsupportedOperationException("Reached unsupported condition " + businessTypeAsString);
            }

            if (null == marketplace) {
                LOG.warn("Could not find postId={}", queueUser.getQueueUserId());
                response.sendError(SC_NOT_FOUND, "Could not find");
                return null;
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not find postId={}", queueUser.getQueueUserId());
            response.sendError(SC_NOT_FOUND, "Could not find");
            return null;
        }

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute(
                "org.springframework.validation.BindingResult.fileUploadForm",
                model.asMap().get("resultImage"));
        } else {
            redirectAttrs.addFlashAttribute("fileUploadForm", fileUploadForm);
        }

        model.addAttribute("bucketName", FtpService.marketBucketName(bucketName, marketplace.getBusinessType()));
        model.addAttribute("images", marketplace.getPostImages());
        model.addAttribute("postId", marketplace.getId());
        model.addAttribute("businessType", marketplace.getBusinessType().name());
        model.addAttribute("businessTypeAsString", marketplace.getBusinessType().getDescription());
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
        String businessType = httpServletRequest.getParameter("businessType").toUpperCase();

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
                    return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage";
                }

                try {
                    processImage(queueUser.getQueueUserId(), postId, BusinessTypeEnum.valueOf(businessType), multipartFile);
                    return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage";
                } catch (Exception e) {
                    LOG.error("Failed store image upload reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        MarketplaceController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage";
            }
        }
        return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage";
    }

    /** For cancelling uploading service image. */
    @PostMapping (value = "/uploadImage", params = {"cancel_Upload"})
    public String upload() {
        return "redirect:/access/landing";
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
                PropertyRentalEntity propertyRental = propertyRentalService.findOneById(postId);
                if (propertyRental.getPostImages().contains(imageId)) {
                    fileService.deleteMarketImage(queueUser.getQueueUserId(), imageId, postId, propertyRental.getBusinessType());

                    propertyRental.getPostImages().remove(imageId);
                    propertyRentalService.save(propertyRental);
                }
                break;
            case HI:
                HouseholdItemEntity householdItem = householdItemService.findOneById(postId);
                if (householdItem.getPostImages().contains(imageId)) {
                    fileService.deleteMarketImage(queueUser.getQueueUserId(), imageId, postId, householdItem.getBusinessType());

                    householdItem.getPostImages().remove(imageId);
                    householdItemService.save(householdItem);
                }
                break;
            default:
                LOG.error("Reached unsupported condition={}", businessType);
                throw new UnsupportedOperationException("Reached unsupported condition " + businessType);
        }

        return "redirect:/access/marketplace/" + businessType + "/" + postId + "/uploadImage";
    }

    private void processImage(String qid, String postId, BusinessTypeEnum businessType, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            fileService.addMarketplaceImage(
                qid,
                postId,
                FileUtil.createRandomFilenameOf24Chars() + FileUtil.getImageFileExtension(multipartFile.getOriginalFilename(), mimeType),
                businessType,
                bufferedImage);
        }
    }

    @PostMapping(
        value = "/boost",
        headers = "Accept=application/json",
        produces = "application/json"
    )
    @ResponseBody
    public String boostYourPost(
        @RequestParam("postId")
        ScrubbedInput postId,

        @RequestParam("businessTypeAsString")
        ScrubbedInput businessTypeAsString,

        HttpServletResponse response
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserPreferenceEntity userPreference;
        String text;
        switch (BusinessTypeEnum.valueOf(businessTypeAsString.getText())) {
            case HI:
                HouseholdItemEntity householdItem = householdItemService.findOneById(queueUser.getQueueUserId(), postId.getText());
                if (householdItem.isPostingExpired() || householdItem.getValidateStatus() != ValidateStatusEnum.A) {
                    text = "Cannot boost this post";
                    return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "FAILURE", text);
                } else {
                    userPreference = accountService.getEarnedPoint(queueUser.getQueueUserId());
                    if (PointActivityEnum.BOP.absolutePoint() < userPreference.getEarnedPoint()) {
                        pointEarnedManager.save(new PointEarnedEntity(queueUser.getQueueUserId(), PointActivityEnum.BOP));

                        householdItem.setBoost(10);
                        householdItemService.save(householdItem);
                        marketplaceElasticService.save(DomainConversion.getAsMarketplaceElastic(householdItem));

                        text = "Successfully boosted post";
                        return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "SUCCESS", text);
                    }

                    /* Not enough point to boost your post. */
                    text = "Not enough points to boost post. Reviews and Invite friends to earn points.";
                    return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "FAILURE", text);
                }
            case PR:
                PropertyRentalEntity propertyRental = propertyRentalService.findOneById(queueUser.getQueueUserId(), postId.getText());
                if (propertyRental.isPostingExpired() || propertyRental.getValidateStatus() != ValidateStatusEnum.A) {
                    text = "Cannot boost this post";
                    return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "FAILURE", text);
                } else {
                    userPreference = accountService.getEarnedPoint(queueUser.getQueueUserId());
                    if (PointActivityEnum.BOP.absolutePoint() < userPreference.getEarnedPoint()) {
                        pointEarnedManager.save(new PointEarnedEntity(queueUser.getQueueUserId(), PointActivityEnum.BOP));

                        propertyRental.setBoost(10);
                        propertyRentalService.save(propertyRental);
                        marketplaceElasticService.save(DomainConversion.getAsMarketplaceElastic(propertyRental));
                        text = "Successfully boosted post";
                        return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "SUCCESS", text);
                    }

                    /* Not enough point to boost your post. */
                    text = "Not enough points to boost post. Reviews and Invite friends to earn points.";
                    return String.format("{ \"postId\" : \"%s\", \"action\" : \"%s\", \"text\" : \"%s\"}", postId.getText(), "FAILURE", text);
                }
            default:
                LOG.error("Reached unsupported condition={} {} {}", businessTypeAsString.getText(), postId.getText(), queueUser.getQueueUserId());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessTypeAsString.getText());
        }
    }

    @GetMapping(value = "/{businessTypeEnum}/{id}/view", produces = "text/html;charset=UTF-8")
    public String view(
        @PathVariable("id")
        ScrubbedInput id,

        @PathVariable("businessTypeEnum")
        ScrubbedInput businessTypeEnum,

        @ModelAttribute ("marketplaceForm")
        MarketplaceForm marketplaceForm,

        Model model
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to marketplace view for {} {} by {}", id, businessTypeEnum, queueUser.getQueueUserId());

        try {
            if (!Validate.isValidObjectId(id.getText())) {
                LOG.error("Marketplace id should be ObjectId but is {}", id.getText());
                throw new NotAValidObjectIdException("Failed to validated id " + id.getText());
            }

            BusinessTypeEnum businessType = BusinessTypeEnum.valueOf(businessTypeEnum.getText().toUpperCase());
            model.addAttribute("bucketName", FtpService.marketBucketName(bucketName, businessType));
            switch (businessType) {
                case PR:
                    PropertyRentalEntity propertyRental = propertyRentalService.findOneById(queueUser.getQueueUserId(), id.getText());
                    if (null == propertyRental) {
                        LOG.warn("Not found {} {} {}", queueUser.getQueueUserId(), businessType, id.getText());
                        return "redirect:/access/landing";
                    }
                    marketplaceForm.setMarketplace(propertyRental);
                    return nextPagePropertyRental;
                case HI:
                    HouseholdItemEntity householdItem = householdItemService.findOneById(queueUser.getQueueUserId(), id.getText());
                    if (null == householdItem) {
                        LOG.warn("Not found {} {} {}", queueUser.getQueueUserId(), businessType, id.getText());
                        return "redirect:/access/landing";
                    }
                    marketplaceForm.setMarketplace(householdItem);
                    return nextPageHouseholdItem;
                default:
                    LOG.error("Reached unsupported condition {}", businessTypeEnum.getText());
                    throw new UnsupportedOperationException("Reached un-supported condition");
            }
        } catch (Exception e) {
            LOG.error("Failed updated status for marketplace id={} businessType={} qid={} reason={}",
                id.getText(),
                businessTypeEnum.getText(),
                queueUser.getQueueUserId(),
                e.getLocalizedMessage(),
                e);

            return "redirect:/access/landing";
        }
    }
}
