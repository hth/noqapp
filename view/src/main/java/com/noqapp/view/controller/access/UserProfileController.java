package com.noqapp.view.controller.access;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.FileService;
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
            FileService fileService
    ) {
        this.nextPage = nextPage;
        this.awsEndPoint = awsEndPoint;
        this.awsBucket = awsBucket;

        this.apiHealthService = apiHealthService;
        this.accountService = accountService;
        this.fileService = fileService;
    }

    @GetMapping
    public String landing(
            @ModelAttribute("userProfileForm")
            UserProfileForm userProfileForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileEntity userProfile = accountService.findProfileByQueueUserId(queueUser.getQueueUserId());
        UserAccountEntity userAccount = accountService.findByQueueUserId(queueUser.getQueueUserId());

        userProfileForm
                .setProfileImage(StringUtils.isBlank(userProfile.getProfileImage()) ? "" : awsEndPoint + awsBucket + "/profile/" + userProfile.getProfileImage())
                .setGender(userProfile.getGender())
                .setEmail(userProfile.getEmail())
                .setLastName(userProfile.getLastName())
                .setFirstName(userProfile.getFirstName())
                .setBirthday(userProfile.getBirthday())
                .setAddress(userProfile.getAddress())
                .setPhone(userProfile.getPhone())
                .setTimeZone(userProfile.getTimeZone())
                .setEmailValidated(userAccount.isAccountValidated())
                .setPhoneValidated(userAccount.isPhoneValidated());

        apiHealthService.insert(
                "/",
                "landing",
                UserProfileController.class.getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        return nextPage;
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
            LOG.error("Empty or no document uploaded");
            throw new RuntimeException("Empty or no document uploaded");
        }
        return files;
    }
}
