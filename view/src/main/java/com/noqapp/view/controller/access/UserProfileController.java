package com.noqapp.view.controller.access;

import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserAddressEntity;
import com.noqapp.domain.UserPreferenceEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.helper.NameDatePair;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.PointActivityEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.FileService;
import com.noqapp.service.PointEarnedService;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.UserAddressService;
import com.noqapp.view.form.FileUploadForm;
import com.noqapp.view.form.ProfessionalProfileEditForm;
import com.noqapp.view.form.ProfessionalProfileForm;
import com.noqapp.view.form.UserProfileForm;
import com.noqapp.view.validator.ImageValidator;
import com.noqapp.view.validator.ProfessionalProfileValidator;
import com.noqapp.view.validator.UserProfileValidator;

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
 * 6/9/18 7:07 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/access/userProfile")
public class UserProfileController {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);

    /**
     * Refers to userProfile.jsp.
     */
    private String nextPage;
    private String awsEndPoint;
    private String awsBucket;

    private ApiHealthService apiHealthService;
    private AccountService accountService;
    private UserProfileValidator userProfileValidator;
    private ProfessionalProfileService professionalProfileService;
    private ProfessionalProfileValidator professionalProfileValidator;
    private FileService fileService;
    private BusinessUserService businessUserService;
    private UserAddressService userAddressService;
    private PointEarnedService pointEarnedService;
    private ImageValidator imageValidator;

    @Autowired
    public UserProfileController(
        @Value("${nextPage:/access/userProfile}")
        String nextPage,

        @Value("${aws.s3.endpoint}")
        String awsEndPoint,

        @Value("${aws.s3.bucketName}")
        String awsBucket,

        ApiHealthService apiHealthService,
        AccountService accountService,
        UserProfileValidator userProfileValidator,
        ProfessionalProfileService professionalProfileService,
        ProfessionalProfileValidator professionalProfileValidator,
        FileService fileService,
        BusinessUserService businessUserService,
        UserAddressService userAddressService,
        PointEarnedService pointEarnedService,
        ImageValidator imageValidator
    ) {
        this.nextPage = nextPage;
        this.awsEndPoint = awsEndPoint;
        this.awsBucket = awsBucket;

        this.apiHealthService = apiHealthService;
        this.accountService = accountService;
        this.userProfileValidator = userProfileValidator;
        this.professionalProfileService = professionalProfileService;
        this.professionalProfileValidator = professionalProfileValidator;
        this.fileService = fileService;
        this.businessUserService = businessUserService;
        this.userAddressService = userAddressService;
        this.pointEarnedService = pointEarnedService;
        this.imageValidator = imageValidator;
    }

    /**
     * Gymnastic for PRG.
     * @return
     */
    @GetMapping
    public String landing(
        @ModelAttribute("userProfileForm")
        UserProfileForm userProfileForm,

        @ModelAttribute("professionalProfileForm")
        ProfessionalProfileForm professionalProfileForm,

        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        Model model
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        populateProfile(userProfileForm, professionalProfileForm, queueUser.getQueueUserId(), model);

        /* Different binding for different form. */
        if (model.asMap().containsKey("resultImage")) {
            model.addAttribute("org.springframework.validation.BindingResult.fileUploadForm", model.asMap().get("resultImage"));
        }

        /* Different binding for different form. */
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.userProfileForm", model.asMap().get("result"));
        }

        apiHealthService.insert(
            "/",
            "landing",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return nextPage;
    }

    /** Supports read only profile for Admin. */
    @GetMapping(value = "/show")
    public String showProfileForBusinessAdmin(
        @ModelAttribute("userProfileForm")
        UserProfileForm userProfileForm,

        @ModelAttribute("professionalProfileForm")
        ProfessionalProfileForm professionalProfileForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String businessUserId = (String) model.asMap().get("businessUserId");
        LOG.info("Landed on profile page qid={} for businessUserId={}", queueUser.getQueueUserId(), businessUserId);
        BusinessUserEntity businessUser = businessUserService.findById(businessUserId);
        if (businessUser.getQueueUserId().equalsIgnoreCase(queueUser.getQueueUserId())) {
            LOG.warn("Cannot access self profile from here qid={}", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }
        populateProfile(userProfileForm, professionalProfileForm, businessUser.getQueueUserId(), model);

        apiHealthService.insert(
            "/",
            "landing",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return nextPage;
    }

    private void populateProfile(
        UserProfileForm userProfileForm,
        ProfessionalProfileForm professionalProfileForm,
        String queueUserId,
        Model model
    ) {
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUserId);
        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUserId);
        UserAddressEntity userAddress = userAddressService.findOneUserAddress(userProfile.getQueueUserId());
        UserPreferenceEntity userPreference = accountService.getEarnedPoint(userProfile.getQueueUserId());

        long reviewPointsEarned = pointEarnedService.totalReviewPoints(userProfile.getQueueUserId());
        long totalInvitePointsEarned = pointEarnedService.totalInvitePoints(userProfile.getQueueUserId());

        userProfileForm
            .setProfileImage(StringUtils.isBlank(userProfile.getProfileImage())
                ? "/static/internal/img/profile-image-192x192.png"
                : awsEndPoint + awsBucket + "/profile/" + userProfile.getProfileImage())
            .setGender(userProfile.getGender())
            .setEmail(new ScrubbedInput(userProfile.getEmail()))
            .setLastName(new ScrubbedInput(userProfile.getLastName()))
            .setFirstName(new ScrubbedInput(userProfile.getFirstName()))
            .setBirthday(new ScrubbedInput(userProfile.getBirthday()))
            .setAddress(null == userAddress ? new ScrubbedInput("") : new ScrubbedInput(userAddress.getAddress()))
            .setPhone(new ScrubbedInput(userProfile.getPhone()))
            .setTimeZone(StringUtils.isBlank(userProfile.getTimeZone()) ? new ScrubbedInput("N/A") : new ScrubbedInput(userProfile.getTimeZone()))
            .setEmailValidated(userAccount.isAccountValidated())
            .setPhoneValidated(userAccount.isPhoneValidated())
            .setUserPreference(userPreference)
            .setReviewPointsEarned(reviewPointsEarned)
            .setTotalInvitePointsEarned(totalInvitePointsEarned);

        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUserId);
        if (null != professionalProfile) {
            professionalProfileForm
                .setProfessionalProfile(true)
                .setPracticeStart(professionalProfile.getPracticeStart())
                .setAboutMe(professionalProfile.getAboutMe())
                .setEducation(professionalProfile.getEducation())
                .setLicenses(professionalProfile.getLicenses())
                .setAwards(professionalProfile.getAwards());

            //Gymnastic to show BindingResult errors if any
            if (model.asMap().containsKey("result")) {
                model.addAttribute("org.springframework.validation.BindingResult.professionalProfileForm", model.asMap().get("result"));
            }
        }
    }

    /** Updates basic profile. */
    @PostMapping(value = "/updateProfile")
    public String updateProfile(
        @ModelAttribute("userProfileForm")
        UserProfileForm userProfileForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        UserAddressEntity userAddress = userAddressService.findOneUserAddress(userProfile.getQueueUserId());

        userProfileValidator.validate(userProfileForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            return "redirect:" + "/access/userProfile";
        }

        //TODO(hth) to support change of address, this will need to be changed to flow
        RegisterUser registerUser = new RegisterUser()
            .setEmail(new ScrubbedInput(userProfile.getEmail()))
            .setAddress(null == userAddress ? new ScrubbedInput("") : new ScrubbedInput(userAddress.getAddress()))
            .setCountryShortName(new ScrubbedInput(userProfile.getCountryShortName()))
            .setPhone(new ScrubbedInput(userProfile.getPhoneRaw()))
            .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
            /* Only the field below are modified. */
            .setBirthday(userProfileForm.getBirthday())
            .setFirstName(userProfileForm.getFirstName())
            .setLastName(userProfileForm.getLastName())
            .setGender(userProfileForm.getGender())
            .setQueueUserId(userProfile.getQueueUserId());
        accountService.updateUserProfile(registerUser, userProfile.getEmail());

        apiHealthService.insert(
            "/updateProfile",
            "updateProfile",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "redirect:/access/userProfile";
    }

    /** Updated practising since of professional profile. */
    @PostMapping(value = "/updateProfessionalProfile")
    public String updateProfessionalProfile(
        @ModelAttribute("professionalProfileForm")
        ProfessionalProfileForm professionalProfileForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUser.getQueueUserId());

        professionalProfileValidator.validateProfessionalProfileForm(professionalProfileForm.setQid(queueUser.getQueueUserId()), result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/access/userProfile";
        }

        professionalProfile
            .setPracticeStart(professionalProfileForm.getPracticeStart())
            .setAboutMe(professionalProfileForm.getAboutMe());
        professionalProfileService.save(professionalProfile);

        apiHealthService.insert(
            "/updateProfessionalProfile",
            "updateProfessionalProfile",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "redirect:/access/userProfile";
    }

    /**
     * Adds awards, education and licenses to professional profile.
     * Gymnastic for PRG.
     */
    @GetMapping(value = "/userProfessionalDetail/{action}/modify")
    public String modify(
        @PathVariable("action")
        ScrubbedInput action,

        @ModelAttribute("professionalProfileEditForm")
        ProfessionalProfileEditForm professionalProfileEditForm,

        Model model
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUser.getQueueUserId());
        if (null == professionalProfile) {
            return "redirect:/access/userProfile";
        } else {
            professionalProfileEditForm.setProfessionalProfile(true);
            //Gymnastic to show BindingResult errors if any
            if (model.asMap().containsKey("result")) {
                model.addAttribute(
                    "org.springframework.validation.BindingResult.professionalProfileEditForm",
                    model.asMap().get("result"));
            } 
        }

        switch(action.getText()) {
            case "awards":
                professionalProfileEditForm
                    .setAction(action.getText())
                    .setNameDatePairs(professionalProfile.getAwards());
                break;
            case "education":
                professionalProfileEditForm
                    .setAction(action.getText())
                    .setNameDatePairs(professionalProfile.getEducation());
                break;
            case "licenses":
                professionalProfileEditForm
                    .setAction(action.getText())
                    .setNameDatePairs(professionalProfile.getLicenses());
                break;
            default:
                LOG.error("Reached unsupported condition qid={} action={}", queueUser.getQueueUserId(), action.getText());
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }

        apiHealthService.insert(
            "/userProfessionalDetail/{action}/modify",
            "modify",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "/access/userProfessionalDetailEdit";
    }

    /** After add its goes back to get the action for next action on the page. */
    @PostMapping(value = "/userProfessionalDetail/add", params = "add")
    public String add(
        @ModelAttribute("professionalProfileEditForm")
        ProfessionalProfileEditForm professionalProfileEditForm,

        BindingResult result,
        RedirectAttributes redirectAttrs
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        professionalProfileValidator.validate(professionalProfileEditForm.setQid(queueUser.getQueueUserId()), result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            LOG.warn("Failed validation");
            //Re-direct to prevent resubmit
            return "redirect:/access/userProfile/userProfessionalDetail/" + professionalProfileEditForm.getAction() + "/modify";
        }

        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUser.getQueueUserId());
        switch (professionalProfileEditForm.getAction()) {
            case "awards":
                professionalProfile.getAwards().add(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            case "education":
                professionalProfile.getEducation().add(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            case "licenses":
                professionalProfile.getLicenses().add(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            default:
                LOG.error("Reached unsupported condition qid={} action={}", queueUser.getQueueUserId(), professionalProfileEditForm.getAction());
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
        professionalProfileService.save(professionalProfile);

        apiHealthService.insert(
            "/userProfessionalDetail/add",
            "add",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "redirect:/access/userProfile/userProfessionalDetail/" + professionalProfileEditForm.getAction() + "/modify";
    }

    @PostMapping(value = "/userProfessionalDetail/add", params = "cancel")
    public String cancel() {
        return "redirect:/access/userProfile";
    }

    @PostMapping(value = "/userProfessionalDetail/delete")
    public String delete(
        @ModelAttribute("professionalProfileEditForm")
        ProfessionalProfileEditForm professionalProfileEditForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUser.getQueueUserId());

        switch (professionalProfileEditForm.getAction()) {
            case "awards":
                professionalProfile.getAwards().remove(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            case "education":
                professionalProfile.getEducation().remove(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            case "licenses":
                professionalProfile.getLicenses().remove(
                    new NameDatePair()
                        .setName(professionalProfileEditForm.getName())
                        .setMonthYear(professionalProfileEditForm.getMonthYear()));
                break;
            default:
                LOG.error("Reached unsupported condition qid={} action={}", queueUser.getQueueUserId(), professionalProfileEditForm.getAction());
                throw new UnsupportedOperationException("Un-supported action. Contact Support");
        }
        professionalProfileService.save(professionalProfile);

        apiHealthService.insert(
            "/userProfessionalDetail/delete",
            "delete",
            UserProfileController.class.getName(),
            Duration.between(start, Instant.now()),
            HealthStatusEnum.G);
        return "redirect:/access/userProfile/userProfessionalDetail/" + professionalProfileEditForm.getAction() + "/modify";
    }

    @PostMapping(value = "/upload", params = "cancel_Upload")
    public String cancelUpload() {
        return "redirect:/";
    }

    /**
     * For uploading profile image.
     * Note: Cancel profile upload does not exists as this upload of profile can be from multiple places.
     */
    @PostMapping (value = "/upload", params = "upload")
    public String upload(
        @ModelAttribute("fileUploadForm")
        FileUploadForm fileUploadForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletRequest httpServletRequest
    ) {
        Instant start = Instant.now();
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Uploading profile image qid={}", queueUser.getQueueUserId());

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                imageValidator.validate(multipartFile, result);
                if (result.hasErrors()) {
                    redirectAttrs.addFlashAttribute("resultImage", result);
                    LOG.warn("Failed image validation");
                    //Re-direct to prevent resubmit
                    return "redirect:/access/userProfile";
                }

                try {
                    processProfileImage(queueUser.getQueueUserId(), multipartFile);
                    return "redirect:" + nextPage;
                } catch (Exception e) {
                    LOG.error("document upload failed reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                        "/upload",
                        "upload",
                        LandingController.class.getName(),
                        Duration.between(start, Instant.now()),
                        HealthStatusEnum.F);
                }

                return "redirect:" + nextPage;
            }
        }
        return "redirect:" + nextPage;
    }

    private void processProfileImage(String qid, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            String profileFilename = FileUtil.createRandomFilenameOf24Chars() + getFileExtensionWithDot(multipartFile.getOriginalFilename());
            fileService.addProfileImage(qid, profileFilename, bufferedImage);
        }
    }

    public static List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest multipartHttpRequest) {
        return getMultipartFiles(multipartHttpRequest, "file");
    }

    public static List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest multipartHttpRequest, String fileListedUnderName) {
        final List<MultipartFile> files = multipartHttpRequest.getFiles(fileListedUnderName);

        if (files.isEmpty()) {
            LOG.error("Empty or no image uploaded");
            throw new RuntimeException("Empty or no image uploaded");
        }
        return files;
    }
}
