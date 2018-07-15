package com.noqapp.social.service;

import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.UserProfilePreferenceService;

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

    private UserProfilePreferenceService userProfilePreferenceService;
    private AccountService accountService;

    private String accountNotValidatedMessage;

    @Autowired
    public CustomUserDetailsService(
            @Value ("${CustomUserDetailsService.account.not.validated.message}")
            String accountNotValidatedMessage,

            UserProfilePreferenceService userProfilePreferenceService,
            AccountService accountService
    ) {
        this.accountNotValidatedMessage = accountNotValidatedMessage;

        this.userProfilePreferenceService = userProfilePreferenceService;
        this.accountService = accountService;
    }

    /**
     * @param email - lower case string
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.info("login attempted email={}", email);

        /* Always check user login with lower letter email case. */
        UserProfileEntity userProfile = userProfilePreferenceService.findOneByMail(email);
        if (null == userProfile) {
            LOG.warn("Not found user with email={}", email);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = accountService.findByQueueUserId(userProfile.getQueueUserId());
            LOG.info("qid={} accountValidated={}", userAccount.getQueueUserId(), userAccount.isAccountValidated());

            boolean condition = isUserActive(userAccount);
            if (!condition) {
                /* Throw exception when its NOT a social signup. */
                throw new RuntimeException("Registration is turned off. We will notify you on your registered email " +
                        (StringUtils.isNotBlank(userProfile.getEmail()) ? "<b>" + userProfile.getEmail() + "</b>" : "") +
                        " when we start accepting new users.");
            }

            return new QueueUser(
                    userProfile.getEmail(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getQueueUserId(),
                    userProfile.getLevel(),
                    condition,
                    userAccount.isAccountValidated(),
                    userProfile.getCountryShortName(),
                    userAccount.getDisplayName()
            );
        }
    }

    /**
     * Check if account has been marked inactive for a reason.
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
                    LOG.error("Reached condition for invalid account qid={}", userAccount.getQueueUserId());
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
