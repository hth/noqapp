package com.noqapp.view.controller.emp;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.PublishArticleEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.ValidateStatusEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.AdvertisementService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.PublishArticleService;
import com.noqapp.service.emp.EmpLandingService;
import com.noqapp.view.form.PublishArticleForm;
import com.noqapp.view.form.emp.BusinessAwaitingApprovalForm;
import com.noqapp.view.form.emp.EmpLandingForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 12/11/16 8:18 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/emp/landing")
public class EmpLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(EmpLandingController.class);

    private String bucketName;
    private String empLanding;
    private String businessAwaitingApproval;

    private BusinessUserService businessUserService;
    private AccountService accountService;
    private EmpLandingService empLandingService;
    private PublishArticleService publishArticleService;
    private AdvertisementService advertisementService;

    @Autowired
    public EmpLandingController(
        @Value("${aws.s3.bucketName}")
        String bucketName,

        @Value ("${empLanding:/emp/landing}")
        String empLanding,

        @Value ("${businessAwaitingApproval:/emp/businessAwaitingApproval}")
        String businessAwaitingApproval,

        BusinessUserService businessUserService,
        AccountService accountService,
        EmpLandingService empLandingService,
        PublishArticleService publishArticleService,
        AdvertisementService advertisementService
    ) {
        this.bucketName = bucketName;
        this.empLanding = empLanding;
        this.businessAwaitingApproval = businessAwaitingApproval;

        this.businessUserService = businessUserService;
        this.accountService = accountService;
        this.empLandingService = empLandingService;
        this.publishArticleService = publishArticleService;
        this.advertisementService = advertisementService;
    }

    @GetMapping
    public String empLanding(
        @ModelAttribute ("empLandingForm")
        EmpLandingForm empLandingForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed qid={}", queueUser.getQueueUserId());
        empLandingForm
            .setAwaitingApprovalCount(businessUserService.awaitingBusinessApprovalCount())
            .setBusinessUsers(businessUserService.awaitingBusinessApprovals())
            .setPublishArticles(publishArticleService.findPendingApprovals())
            .setAwaitingAdvertisementApprovals(advertisementService.findApprovalPendingAdvertisements());
        return empLanding;
    }

    @GetMapping(value = "{businessUserId}")
    public String getAwaitingBusinessApprovals(
        @PathVariable ("businessUserId")
        ScrubbedInput businessUserId,

        @ModelAttribute ("businessAwaitingApprovalForm")
        BusinessAwaitingApprovalForm businessAwaitingApprovalForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Business user={} loaded by qid={}", businessUserId.getText(), queueUser.getQueueUserId());

        BusinessUserEntity businessUser = businessUserService.findById(businessUserId.getText());

        UserProfileEntity inviteeUserProfile;
        if (StringUtils.isNotBlank(businessUser.getBizName().getInviteeCode())) {
            inviteeUserProfile = accountService.findProfileByInviteCode(businessUser.getBizName().getInviteeCode());
            businessAwaitingApprovalForm.setInviteeUserProfile(inviteeUserProfile);
        }

        businessAwaitingApprovalForm
            .setBusinessUser(businessUser)
            .setUserProfile(accountService.findProfileByQueueUserId(businessUser.getQueueUserId()));

        return businessAwaitingApproval;
    }

    @PostMapping (value = "/approval", params = "business-user-approve")
    public String approval(
        @ModelAttribute ("businessAwaitingApprovalForm")
        BusinessAwaitingApprovalForm businessAwaitingApprovalForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Approved Business user={} loaded by qid={}", businessAwaitingApprovalForm.getBusinessUser().getId(), queueUser.getQueueUserId());

        empLandingService.approveBusiness(businessAwaitingApprovalForm.getBusinessUser().getId(), queueUser.getQueueUserId());
        return "redirect:" + "/emp/landing.htm";
    }

    @PostMapping(value = "/approval", params = "business-user-decline")
    public String decline(
        @ModelAttribute ("businessAwaitingApprovalForm")
        BusinessAwaitingApprovalForm businessAwaitingApprovalForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Decline Business user={} loaded by qid={}", businessAwaitingApprovalForm.getBusinessUser().getId(), queueUser.getQueueUserId());

        empLandingService.declineBusiness(businessAwaitingApprovalForm.getBusinessUser().getId(), queueUser.getQueueUserId());
        return "redirect:" + "/emp/landing.htm";
    }

    @GetMapping(value = "/publishArticle/{publishId}/preview", produces = "text/html;charset=UTF-8")
    public String newArticle(
        @PathVariable("publishId")
        ScrubbedInput publishId,

        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        Model model,
        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to preview article qid={}", queueUser.getQueueUserId());
        PublishArticleEntity publishArticle = publishArticleService.findOnePendingReview(publishId.getText());
        if (null == publishArticle) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }

        publishArticleForm
            .setBannerImage(publishArticle.getBannerImage())
            .setTitle(new ScrubbedInput(publishArticle.getTitle()))
            .setDescription(publishArticle.getDescription())
            .setPublishId(new ScrubbedInput(publishArticle.getId()));
        model.addAttribute("bucketName", bucketName);
        return "/emp/publishArticle/preview";
    }

    @PostMapping(value = "/publishArticle/preview", produces = "text/html;charset=UTF-8")
    public String newArticle(
        @ModelAttribute("publishArticleForm")
        PublishArticleForm publishArticleForm,

        HttpServletResponse response
    ) throws IOException {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed to preview article qid={}", queueUser.getQueueUserId());
        PublishArticleEntity publishArticle = publishArticleService.findOnePendingReview(publishArticleForm.getPublishId().getText());
        if (null == publishArticle) {
            LOG.warn("Could not find qid={} having access as business user", queueUser.getQueueUserId());
            response.sendError(SC_UNAUTHORIZED, "Not authorized");
            return null;
        }

        switch (publishArticleForm.getValidateStatus()) {
            case A:
                if (null == publishArticle.getPublishDate()) {
                    publishArticle.setPublishDate(new Date());
                }
                publishArticle.setValidateStatus(ValidateStatusEnum.A);
                break;
            case R:
                publishArticle.setValidateStatus(ValidateStatusEnum.R);
                break;
            default:
                LOG.error("Reached unsupported condition={}", publishArticleForm.getValidateStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + publishArticleForm.getValidateStatus());
        }
        publishArticle.setValidateByQid(queueUser.getQueueUserId());
        publishArticleService.save(publishArticle);
        return "redirect:" + "/emp/landing.htm";
    }
}
