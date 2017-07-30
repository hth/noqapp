package com.noqapp.social.service;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.TokenUser;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.UserProfilePreferenceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 3/29/14 12:33 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private AccountService accountService;

    @Value ("${CustomUserDetailsService.account.not.validated.message}")
    private String accountNotValidatedMessage;

    @Value ("${CustomUserDetailsService.account.signup.incomplete.message}")
    private String accountSignupIncompleteMessage;

    /**
     * @param email - lower case string
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.info("login attempted user={}", email);

        /* Always check user login with lower letter email case. */
        UserProfileEntity userProfile = userProfilePreferenceService.findByEmail(email);
        if (null == userProfile) {
            LOG.warn("Not found user={}", email);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = accountService.findByReceiptUserId(userProfile.getReceiptUserId());
            LOG.info("user={} accountValidated={}", userAccount.getReceiptUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActive(userAccount);
            if (!condition) {
                /* Throw exception when its NOT a social signup. */
                throw new RuntimeException("Registration is turned off. We will notify you on your registered email " +
                        (StringUtils.isNotBlank(userProfile.getEmail()) ? "<b>" + userProfile.getEmail() + "</b>" : "") +
                        " when we start accepting new users.");
            }

            return new TokenUser(
                    userProfile.getEmail(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getReceiptUserId(),
                    userProfile.getLevel(),
                    condition,
                    userAccount.isAccountValidated(),
                    userProfile.getCountryShortName()
            );
        }
    }

    /**
     * If registration is turned on then check if the account is validated and not beyond set number of days
     * And, if registration is turned off then check is userAccount is active.
     *
     * @param userAccount
     * @return
     */
    public boolean isUserActive(UserAccountEntity userAccount) {
        if (null != userAccount.getAccountInactiveReason()) {
            switch (userAccount.getAccountInactiveReason()) {
                case ANV:
                    LOG.info("Account is inactive for reason={}", userAccount.getAccountInactiveReason());
                    throw new RuntimeException(accountNotValidatedMessage);
                default:
                    LOG.error("Reached condition for invalid account rid={}", userAccount.getReceiptUserId());
                    return false;
            }
        }

        return true;
    }

    /**
     * Retrieves the correct ROLE type depending on the access level, where access level is an Integer.
     * Basically, this interprets the access value whether it's for a regular user or admin.
     *
     * @param roles
     * @return collection of granted authorities
     */
    public Collection<? extends GrantedAuthority> getAuthorities(Set<RoleEnum> roles) {
        List<GrantedAuthority> authList = new ArrayList<>(RoleEnum.values().length);
        authList.addAll(roles.stream().map(roleEnum -> new SimpleGrantedAuthority(roleEnum.name())).collect(Collectors.toList()));
        return authList;
    }
}
