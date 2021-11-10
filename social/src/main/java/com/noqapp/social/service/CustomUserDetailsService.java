package com.noqapp.social.service;

import com.noqapp.common.utils.IntRandomNumberGenerator;
import com.noqapp.domain.UserAccountEntity;
import com.noqapp.domain.UserProfileEntity;
import com.noqapp.domain.site.QueueUser;
import com.noqapp.domain.types.RoleEnum;
import com.noqapp.service.AccountService;
import com.noqapp.service.UserProfilePreferenceService;
import com.noqapp.social.exception.AccountNotActiveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: hitender
 * Date: 3/29/14 12:33 AM
 */
@SuppressWarnings({
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

    private ScheduledExecutorService executorService;

    @Autowired
    public CustomUserDetailsService(
        UserProfilePreferenceService userProfilePreferenceService,
        AccountService accountService
    ) {
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.accountService = accountService;

        this.executorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * @param mail - lower case string
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        LOG.info("login attempted mail={}", mail);

        /* Always check user login with lower letter email case. */
        UserProfileEntity userProfile = userProfilePreferenceService.findOneByMail(mail);
        if (null == userProfile) {
            LOG.warn("Not found user with mail={}", mail);
            throw new UsernameNotFoundException("Error in retrieving user");
        } else {
            UserAccountEntity userAccount = accountService.findByQueueUserId(userProfile.getQueueUserId());
            LOG.info("qid={} accountValidated={}", userAccount.getQueueUserId(), userAccount.isAccountValidated());

            try {
                doesUserHasInActiveReason(userAccount);
            } catch (AccountNotActiveException e) {
                return new QueueUser(
                    userProfile.getEmail(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getQueueUserId(),
                    userProfile.getLevel(),
                    false,
                    userAccount.isAccountValidated(),
                    userProfile.getCountryShortName(),
                    userAccount.getDisplayName()
                );
            }
            if (userAccount.isActive()) {
                return new QueueUser(
                    userProfile.getEmail(),
                    userAccount.getUserAuthentication().getPassword(),
                    getAuthorities(userAccount.getRoles()),
                    userProfile.getQueueUserId(),
                    userProfile.getLevel(),
                    userAccount.isActive(),
                    userAccount.isAccountValidated(),
                    userProfile.getCountryShortName(),
                    userAccount.getDisplayName()
                );
            } else {
                /* Throw exception when its NOT a social signup. */
                LOG.error("Reached condition for invalid account qid={}", userAccount.getQueueUserId());
                throw new AccountNotActiveException("Account is blocked. Contact support.");
            }
        }
    }

    /**
     * Check if account has been marked inactive for a reason.
     *
     * @param userAccount
     * @return
     */
    public void doesUserHasInActiveReason(UserAccountEntity userAccount) {
        try {
            if (null != userAccount.getAccountInactiveReason()) {
                switch (userAccount.getAccountInactiveReason()) {
                    case ANV:
                    case BOC:
                    case BUP:
                        LOG.warn("Account Not Active {} qid={}", userAccount.getAccountInactiveReason(), userAccount.getQueueUserId());
                        throw new AccountNotActiveException("Account is blocked for " + userAccount.getAccountInactiveReason().getDescription() + ". Contact support.");
                    case ADP:
                        LOG.warn("Account Not Active {} qid={}", userAccount.getAccountInactiveReason(), userAccount.getQueueUserId());
                        throw new AccountNotActiveException(userAccount.getAccountInactiveReason().getDescription() + ". Contact support.");
                    case LIM:
                        IntRandomNumberGenerator intRandomNumberGenerator = IntRandomNumberGenerator.newInstanceInclusiveOfMaxRange(2, 6);
                        int minutes = intRandomNumberGenerator.nextInt();
                        LOG.error("Account Active access limited {} qid={} {}", userAccount.getAccountInactiveReason(), userAccount.getQueueUserId(), minutes);
                        executorService.schedule(() -> accountService.updateAuthenticationKey(userAccount.getUserAuthentication().getId()), minutes, TimeUnit.MINUTES);
                    default:
                        LOG.error("Reached condition for invalid account qid={} {}", userAccount.getQueueUserId(), userAccount.getAccountInactiveReason());
                        throw new AccountNotActiveException("Account is blocked. Contact support.");
                }
            }
        } catch (Exception e) {
            LOG.error("Failed on checking account inactive qid={} reason={}", userAccount.getQueueUserId(), e.getLocalizedMessage(), e);
        }
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
