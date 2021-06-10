package com.noqapp.domain.site;


import com.noqapp.domain.types.UserLevelEnum;

import org.apache.commons.text.WordUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * User: hitender
 * Date: 11/18/16 9:55 AM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public final class QueueUser extends User {

    private String queueUserId;
    private UserLevelEnum userLevel;
    private boolean accountValidated;
    private String countryShortName;
    private String userShortName;
    private String emailWithoutDomain;

    public QueueUser(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
    }

    public QueueUser(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String queueUserId,
        UserLevelEnum userLevel,
        boolean active,
        boolean accountValidated,
        String countryShortName,
        String userShortName
    ) {
        super(username, password, active, true, true, true, authorities);
        this.queueUserId = queueUserId;
        this.userLevel = userLevel;
        this.accountValidated = accountValidated;
        this.countryShortName = countryShortName;
        this.userShortName = WordUtils.initials(userShortName);
        this.emailWithoutDomain = username.split("@")[0];
    }

    public QueueUser(
        String username,
        String password,
        boolean enabled,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public QueueUser(
        String username,
        String password,
        boolean enabled,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean accountNonLocked,
        Collection<? extends GrantedAuthority> authorities,
        String queueUserId,
        UserLevelEnum userLevel
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.queueUserId = queueUserId;
        this.userLevel = userLevel;
    }

    /**
     * Gets NoQueue user id.
     *
     * @return queueUserId
     */
    public String getQueueUserId() {
        return queueUserId;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public String getUserShortName() {
        return userShortName;
    }

    public String getEmailWithoutDomain() {
        return emailWithoutDomain;
    }

    /** Checks if the email ends with these domain. Since these domain have no email, they are limited from access. */
    public boolean hasNonOperationalEmailDomain() {
        switch (getUsername().split("@")[1]) {
            case "mail.noqapp.com":
            case "m.noqapp.com":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", QueueUser.class.getSimpleName() + "[", "]")
            .add("queueUserId=\"" + queueUserId + "\"")
            .add("userLevel=" + userLevel)
            .add("accountValidated=" + accountValidated)
            .add("countryShortName=\"" + countryShortName + "\"")
            .add("userShortName=\"" + userShortName + "\"")
            .toString();
    }
}
