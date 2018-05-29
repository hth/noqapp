package com.noqapp.view.controller.access;

import com.noqapp.common.utils.FileUtil;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.UserLevelEnum;
import com.noqapp.health.domain.types.HealthStatusEnum;
import com.noqapp.health.service.ApiHealthService;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.FileService;
import com.noqapp.service.QueueService;
import com.noqapp.view.form.LandingForm;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
 * User: hitender
 * Date: 12/6/16 8:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class LandingController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    public static final String SUCCESS = "success";

    /**
     * Refers to landing.jsp.
     */
    private String nextPage;
    private String migrateToBusinessRegistrationFlowActions;

    private BusinessUserService businessUserService;
    private QueueService queueService;
    private ApiHealthService apiHealthService;
    private AccountService accountService;
    private FileService fileService;

    @Autowired
    public LandingController(
            @Value ("${nextPage:/access/landing}")
            String nextPage,

            @Value ("${migrateToBusinessRegistrationFlowActions:redirect:/migrate/business/registration.htm}")
            String migrateToBusinessRegistrationFlowActions,

            BusinessUserService businessUserService,
            QueueService queueService,
            ApiHealthService apiHealthService,
            AccountService accountService,
            FileService fileService
    ) {
        this.nextPage = nextPage;
        this.migrateToBusinessRegistrationFlowActions = migrateToBusinessRegistrationFlowActions;

        this.businessUserService = businessUserService;
        this.queueService = queueService;
        this.apiHealthService = apiHealthService;
        this.accountService = accountService;
        this.fileService = fileService;
    }

    @GetMapping(value = "/landing")
    public String landing(
            @ModelAttribute("landingForm")
            LandingForm landingForm
    ) {
        Instant start = Instant.now();
        LOG.info("Landed on next page");
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BusinessUserEntity businessUser = businessUserService.loadBusinessUser();
        if (null != businessUser) {
            landingForm.setBusinessUserRegistrationStatus(businessUser.getBusinessUserRegistrationStatus())
                    .setBusinessAccountSignedUp(businessUser.getUpdated());
        }

        if (queueUser.getUserLevel() != UserLevelEnum.M_ADMIN) {
            landingForm.setCurrentQueues(queueService.findAllQueuedByQid(queueUser.getQueueUserId()))
                    .setHistoricalQueues(queueService.findAllHistoricalQueue(queueUser.getQueueUserId()));

            landingForm.setMinorUserProfiles(accountService.findMinorProfiles(queueUser.getQueueUserId()));
        }

        LOG.info("Current size={} and Historical size={}",
                landingForm.getCurrentQueues().size(),
                landingForm.getHistoricalQueues().size());

        apiHealthService.insert(
                "/landing",
                "landing",
                LandingController.class.getName(),
                Duration.between(start, Instant.now()),
                HealthStatusEnum.G);
        return nextPage;
    }

    @GetMapping(value = "/landing/business/migrate")
    public String businessMigrate() {
        LOG.info("Requested business registration {}", migrateToBusinessRegistrationFlowActions);
        return migrateToBusinessRegistrationFlowActions;
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
            String filename = FileUtil.createRandomFilenameOf16Chars() + getFileExtensionWithDot(multipartFile.getOriginalFilename());
            fileService.addProfileImage(qid, filename, bufferedImage);
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
