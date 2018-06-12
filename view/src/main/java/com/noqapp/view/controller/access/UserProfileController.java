package com.noqapp.view.controller.access;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.flow.RegisterUser;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.domain.ProfessionalProfileEntity;
import com.noqapp.service.ProfessionalProfileService;
import com.noqapp.service.AccountService;
import com.noqapp.service.FileService;
import com.noqapp.view.form.ProfessionalProfileForm;
import com.noqapp.view.form.UserProfileForm;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.noqapp.common.utils.FileUtil.getFileExtensionWithDot;

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
    private ProfessionalProfileService professionalProfileService;
    private FileService fileService;

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
            ProfessionalProfileService professionalProfileService,
            FileService fileService
    ) {
        this.nextPage = nextPage;
        this.awsEndPoint = awsEndPoint;
        this.awsBucket = awsBucket;

        this.apiHealthService = apiHealthService;
        this.accountService = accountService;
        this.professionalProfileService = professionalProfileService;
        this.fileService = fileService;
    }

    @GetMapping
    public String landing(
            @ModelAttribute("userProfileForm")
            UserProfileForm userProfileForm,

            @ModelAttribute("professionalProfileForm")
            ProfessionalProfileForm professionalProfileForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());

        userProfileForm
                .setProfileImage(StringUtils.isBlank(userProfile.getProfileImage())
                        ? "/static2/internal/img/profile-image-192x192.png"
                        : awsEndPoint + awsBucket + "/profile/" + userProfile.getProfileImage())
                .setGender(userProfile.getGender())
                .setEmail(new ScrubbedInput(userProfile.getEmail()))
                .setLastName(new ScrubbedInput(userProfile.getLastName()))
                .setFirstName(new ScrubbedInput(userProfile.getFirstName()))
                .setBirthday(new ScrubbedInput(userProfile.getBirthday()))
                .setAddress(new ScrubbedInput(userProfile.getAddress()))
                .setPhone(new ScrubbedInput(userProfile.getPhone()))
                .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
                .setEmailValidated(userAccount.isAccountValidated())
                .setPhoneValidated(userAccount.isPhoneValidated());

        ProfessionalProfileEntity professionalProfile = professionalProfileService.findByQid(queueUser.getQueueUserId());
        if (null != professionalProfile) {
            professionalProfileForm
                    .setProfessionalProfile(true)
                    .setPracticeStart(professionalProfile.getPracticeStart())
                    .setEducation(professionalProfile.getEducation())
                    .setLicenses(professionalProfile.getLicenses())
                    .setAwards(professionalProfile.getAwards());
        }

        apiHealthService.insert(
                "/",
                "landing",
                UserProfileController.class.getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        return nextPage;
    }

    @PostMapping(value = "/updateProfile")
    public String updateProfile(
            @ModelAttribute("userProfileForm")
            UserProfileForm userProfileForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());

        //TODO(hth) to support change of address, this will need to be changed to flow
        RegisterUser registerUser = new RegisterUser()
                .setEmail(new ScrubbedInput(userProfile.getEmail()))
                .setAddress(new ScrubbedInput(userProfile.getAddress()))
                .setCountryShortName(new ScrubbedInput(userProfile.getCountryShortName()))
                .setPhone(new ScrubbedInput(userProfile.getPhoneRaw()))
                .setTimeZone(new ScrubbedInput(userProfile.getTimeZone()))
                .setBirthday(userProfileForm.getBirthday())
                .setAddressOrigin(userProfile.getAddressOrigin())
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
        return "redirect:/access/userProfile.htm";
    }

    @PostMapping(value = "/updateProfessionalProfile")
    public String updateProfessionalProfile(
            @ModelAttribute("professionalProfileForm")
            ProfessionalProfileForm professionalProfileForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());


        apiHealthService.insert(
                "/updateProfessionalProfile",
                "updateProfessionalProfile",
                UserProfileController.class.getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        return "redirect:/access/userProfile.htm";
    }

    /**
     * For uploading profile image.
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping (
            method = RequestMethod.POST,
            value = "/upload")
    public String upload(HttpServletRequest httpServletRequest) {
        Instant start = Instant.now();
        LOG.info("uploading image");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = getMultipartFiles(multipartHttpRequest);

            if (!files.isEmpty()) {
                MultipartFile multipartFile = files.iterator().next();

                try {
                    processProfileImage(queueUser.getQueueUserId(), multipartFile);
                    return "redirect:" + nextPage + ".htm";
                } catch (Exception e) {
                    LOG.error("document upload failed reason={} qid={}", e.getLocalizedMessage(), queueUser.getQueueUserId(), e);
                    apiHealthService.insert(
                            "/upload",
                            "upload",
                            LandingController.class.getName(),
                            Duration.between(start, Instant.now()),
                            HealthStatusEnum.F);
                }

                return "redirect:" + nextPage + ".htm";
            }
        }
        return "redirect:" + nextPage + ".htm";
    }

    private void processProfileImage(String qid, MultipartFile multipartFile) throws IOException {
        BufferedImage bufferedImage = fileService.bufferedImage(multipartFile.getInputStream());
        String mimeType = FileUtil.detectMimeType(multipartFile.getInputStream());
        if (mimeType.equalsIgnoreCase(multipartFile.getContentType())) {
            String profileFilename = FileUtil.createRandomFilenameOf24Chars() + getFileExtensionWithDot(multipartFile.getOriginalFilename());
            fileService.addProfileImage(qid, profileFilename, bufferedImage);
        }
    }

    private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest multipartHttpRequest) {
        final List<MultipartFile> files = multipartHttpRequest.getFiles("file");

        if (files.isEmpty()) {
            LOG.error("Empty or no image uploaded");
            throw new RuntimeException("Empty or no image uploaded");
        }
        return files;
    }
}
