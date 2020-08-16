package com.noqapp.view.controller.open;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.AccountService;
import com.noqapp.service.FirebaseService;
import com.noqapp.social.exception.AccountNotActiveException;
import com.noqapp.view.form.UserLoginPhoneForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 8/5/17 6:09 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
@Controller
@RequestMapping(value = "/open/phone")
public class LoginPhoneController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginPhoneController.class);

    private FirebaseService firebaseService;
    private AccountService accountService;
    private LoginController loginController;

    @Autowired
    public LoginPhoneController(
        FirebaseService firebaseService,
        AccountService accountService,
        LoginController loginController
    ) {
        this.accountService = accountService;
        this.firebaseService = firebaseService;
        this.loginController = loginController;
    }

    @PostMapping(
        value = "/login",
        headers = "Accept=application/json",
        produces = "application/json")
    @ResponseBody
    public String phoneLogin(
        @ModelAttribute("userLoginPhoneForm")
        UserLoginPhoneForm userLoginPhoneForm,

        BindingResult result,
        RedirectAttributes redirectAttrs,
        HttpServletResponse httpServletResponse
    ) {
        UserProfileEntity userProfile = firebaseService.getUserWhenLoggedViaPhone(userLoginPhoneForm.getUid());
        if (null == userProfile) {
            LOG.warn("Failed to find user uid={} phone={}", userLoginPhoneForm.getUid(), userLoginPhoneForm.getPhone());
            return String.format("{ \"next\" : \"%s\" }", "/open/login.htm?loginFailure=p--#");
        }
        UserAccountEntity userAccount = accountService.findByQueueUserId(userProfile.getQueueUserId());
        if (!userAccount.isPhoneValidated()) {
            userAccount.setPhoneValidated(true).setOtpCount(1);
            accountService.save(userAccount);
        } else {
            accountService.increaseOTPCount(userProfile.getQueueUserId());
        }
        String redirect;
        try {
            redirect = loginController.determineTargetUrlAfterLogin(userAccount, userProfile);
        } catch (AccountNotActiveException e) {
            LOG.warn("Failed user InActive uid={} phone={} {}", userLoginPhoneForm.getUid(), userLoginPhoneForm.getPhone(), userAccount.getAccountInactiveReason());
            return String.format("{ \"next\" : \"%s\" }", "/open/login.htm?loginFailure=i--#");
        }
        LOG.info("Redirecting user to link={}", redirect);
        return String.format("{ \"next\" : \"%s\" }", redirect);
    }
}
