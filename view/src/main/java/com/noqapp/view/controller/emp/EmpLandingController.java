package com.noqapp.view.controller.emp;

import com.noqapp.domain.UserProfileEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.noqapp.domain.BusinessUserEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.service.AccountService;
import com.noqapp.service.BusinessUserService;
import com.noqapp.service.emp.EmpLandingService;
import com.noqapp.utils.ScrubbedInput;
import com.noqapp.view.form.emp.BusinessAwaitingApprovalForm;
import com.noqapp.view.form.emp.EmpLandingForm;

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

    private String empLanding;
    private String businessAwaitingApproval;
    private BusinessUserService businessUserService;
    private AccountService accountService;
    private EmpLandingService empLandingService;

    @Autowired
    public EmpLandingController(
            @Value ("${empLanding:/emp/landing}")
            String empLanding,

            @Value ("${businessAwaitingApproval:/emp/businessAwaitingApproval}")
            String businessAwaitingApproval,

            BusinessUserService businessUserService,
            AccountService accountService,
            EmpLandingService empLandingService
    ) {
        this.empLanding = empLanding;
        this.businessAwaitingApproval = businessAwaitingApproval;

        this.businessUserService = businessUserService;
        this.accountService = accountService;
        this.empLandingService = empLandingService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String empLanding(
            @ModelAttribute ("empLandingForm")
            EmpLandingForm empLandingForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed qid={}", queueUser.getQueueUserId());

        empLandingForm
                .setAwaitingApprovalCount(businessUserService.awaitingApprovalCount())
                .setBusinessUsers(businessUserService.awaitingApprovals());

        return empLanding;
    }

    @RequestMapping (value = "{businessUserId}", method = RequestMethod.GET)
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
                .setUserProfile(accountService.findProfileByReceiptUserId(businessUser.getQueueUserId()));

        return businessAwaitingApproval;
    }

    @RequestMapping (
            value = "/approval",
            method = RequestMethod.POST,
            params = "business-user-approve")
    public String approval(
            @ModelAttribute ("businessAwaitingApprovalForm")
            BusinessAwaitingApprovalForm businessAwaitingApprovalForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Approved Business user={} loaded by qid={}",
                businessAwaitingApprovalForm.getBusinessUser().getId(),
                queueUser.getQueueUserId());

        empLandingService.approveBusiness(
                businessAwaitingApprovalForm.getBusinessUser().getId(),
                queueUser.getQueueUserId());

        return "redirect:" + "/emp/landing.htm";
    }

    @RequestMapping (
            value = "/approval",
            method = RequestMethod.POST,
            params = "business-user-decline")
    public String decline(
            @ModelAttribute ("businessAwaitingApprovalForm")
            BusinessAwaitingApprovalForm businessAwaitingApprovalForm
    ) {
        QueueUser queueUser = (QueueUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Decline Business user={} loaded by qid={}",
                businessAwaitingApprovalForm.getBusinessUser().getId(),
                queueUser.getQueueUserId());

        empLandingService.declineBusiness(
                businessAwaitingApprovalForm.getBusinessUser().getId(),
                queueUser.getQueueUserId());

        return "redirect:" + "/emp/landing.htm";
    }
}
