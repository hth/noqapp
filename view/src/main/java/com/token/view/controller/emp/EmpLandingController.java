package com.token.view.controller.emp;

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

import com.token.domain.BusinessUserEntity;
import com.token.domain.UserAccountEntity;
import com.token.domain.UserProfileEntity;
import com.token.domain.site.TokenUser;
import com.token.domain.types.BusinessUserRegistrationStatusEnum;
import com.token.domain.types.UserLevelEnum;
import com.token.service.AccountService;
import com.token.service.BusinessUserService;
import com.token.utils.ScrubbedInput;
import com.token.view.form.emp.BusinessAwaitingApprovalForm;
import com.token.view.form.emp.EmpLandingForm;

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

    @Autowired
    public EmpLandingController(
            @Value ("${empLanding:/emp/landing}")
            String empLanding,

            @Value ("${businessAwaitingApproval:/emp/businessAwaitingApproval}")
            String businessAwaitingApproval,

            BusinessUserService businessUserService,
            AccountService accountService) {
        this.empLanding = empLanding;
        this.businessAwaitingApproval = businessAwaitingApproval;

        this.businessUserService = businessUserService;
        this.accountService = accountService;
    }

    @RequestMapping (method = RequestMethod.GET)
    public String empLanding(
            @ModelAttribute ("empLandingForm")
            EmpLandingForm empLandingForm
    ) {
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Employee landed rid={}", tokenUser.getRid());

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
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Business user={} loaded by rid={}", businessUserId.getText(), tokenUser.getRid());

        BusinessUserEntity businessUser = businessUserService.fingById(businessUserId.getText());
        businessAwaitingApprovalForm
                .setBusinessUser(businessUser)
                .setUserProfile(accountService.findProfileByReceiptUserId(businessUser.getReceiptUserId()));

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
        TokenUser tokenUser = (TokenUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Approved Business user={} loaded by rid={}",
                businessAwaitingApprovalForm.getBusinessUser().getId(),
                tokenUser.getRid());

        BusinessUserEntity businessUser = businessUserService.fingById(businessAwaitingApprovalForm.getBusinessUser().getId());
        businessUser
                .setValidateByRid(tokenUser.getRid())
                .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.V);
        businessUserService.save(businessUser);

        UserProfileEntity userProfile = accountService.findProfileByReceiptUserId(businessUser.getReceiptUserId());
        userProfile.setLevel(UserLevelEnum.BUSINESS);
        accountService.save(userProfile);

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                userProfile.getLevel()
        );
        accountService.save(userAccount);

        return empLanding;
    }
}
