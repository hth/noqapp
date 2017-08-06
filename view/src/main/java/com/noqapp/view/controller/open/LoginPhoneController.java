package com.noqapp.view.controller.open;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.service.AccountService;
import com.noqapp.service.FirebaseAuthenticateService;
import com.noqapp.view.form.UserLoginPhoneForm;

/**
 * User: hitender
 * Date: 8/5/17 6:09 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/open/phone")
public class LoginPhoneController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginPhoneController.class);

    private FirebaseAuthenticateService firebaseAuthenticateService;
    private AccountService accountService;
    private LoginController loginController;

    @Autowired
    public LoginPhoneController(
            FirebaseAuthenticateService firebaseAuthenticateService,
            AccountService accountService,
            LoginController loginController
    ) {
        this.accountService = accountService;
        this.firebaseAuthenticateService = firebaseAuthenticateService;
        this.loginController = loginController;
    }

    @RequestMapping (
            method = RequestMethod.POST,
            value = "/login",
            headers = "Accept=application/json",
            produces = "application/json")
    public String phoneLogin(
            @ModelAttribute ("userLoginPhoneForm")
            UserLoginPhoneForm userLoginPhoneForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        UserProfileEntity userProfile = firebaseAuthenticateService.getUserWhenLoggedViaPhone(userLoginPhoneForm.getUid());
        if (null == userProfile) {
            LOG.warn("Failed to find user");
            throw new UsernameNotFoundException("User Not found");
        }
        UserAccountEntity userAccount = accountService.findByReceiptUserId(userProfile.getReceiptUserId());
        String redirect = "redirect:" + loginController.determineTargetUrlAfterLogin(userAccount, userProfile);
        LOG.info("{}", redirect);
        return redirect;
    }
}
