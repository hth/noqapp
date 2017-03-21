package com.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.token.domain.site.TokenUser;

/**
 * User: hitender
 * Date: 11/19/16 6:12 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
@Scope (BeanDefinition.SCOPE_SINGLETON)
public class RegistrationService {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationService.class);

    private String loginController;

    @Autowired
    public RegistrationService(
            @Value ("${indexController:/open/login.htm}")
            String loginController
    ) {
        this.loginController = loginController;
    }

    public boolean validateIfRegistrationIsAllowed(ModelMap map, Authentication authentication) {
        if (!((UserDetails) authentication.getPrincipal()).isEnabled()) {
            TokenUser receiptUser = (TokenUser) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken(
                            String.valueOf(System.currentTimeMillis()),
                            "anonymousUser",
                            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
                    )
            );
            map.addAttribute("deniedSignup", true);
            map.addAttribute("user", receiptUser.getUsername());
            map.addAttribute("pid", receiptUser.getPid());
            return true;
        }
        return false;
    }

    public String getLoginController() {
        return loginController;
    }
}
