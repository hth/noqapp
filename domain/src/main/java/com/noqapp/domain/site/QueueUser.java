package com.noqapp.domain.site;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.noqapp.domain.types.UserLevelEnum;

import java.util.Collection;

/**
 * User: hitender
 * Date: 11/18/16 9:55 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class QueueUser extends User {

    /** queueUserId is receiptUserId */
    private String queueUserId;
    private UserLevelEnum userLevel;
    private boolean accountValidated;
    private String countryShortName;

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
            String countryShortName
    ) {
        super(username, password, active, true, true, true, authorities);
        this.queueUserId = queueUserId;
        this.userLevel = userLevel;
        this.accountValidated = accountValidated;
        this.countryShortName = countryShortName;
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
     * Gets receiptofi user id.
     * @return receiptUserId
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

    @Override
    public String toString() {
        return "QueueUser{" +
                "queueUserId='" + queueUserId + '\'' +
                ", userLevel=" + userLevel +
                '}';
    }
}