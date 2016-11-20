package com.token.domain.site;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.token.domain.types.ProviderEnum;
import com.token.domain.types.UserLevelEnum;

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
public final class TokenUser extends User {

    /** rid is receiptUserId */
    private String rid;
    private ProviderEnum pid;
    private UserLevelEnum userLevel;
    private boolean accountValidated;
    private String countryShortName;

    public TokenUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
    }

    public TokenUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String rid,
            ProviderEnum pid,
            UserLevelEnum userLevel,
            boolean active,
            boolean accountValidated,
            String countryShortName
    ) {
        super(username, password, active, true, true, true, authorities);
        this.rid = rid;
        this.pid = pid;
        this.userLevel = userLevel;
        this.accountValidated = accountValidated;
        this.countryShortName = countryShortName;
    }

    public TokenUser(
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

    public TokenUser(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            String rid,
            ProviderEnum pid,
            UserLevelEnum userLevel
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.rid = rid;
        this.pid = pid;
        this.userLevel = userLevel;
    }

    /**
     * Gets receiptofi user id.
     * @return receiptUserId
     */
    public String getRid() {
        return rid;
    }

    public UserLevelEnum getUserLevel() {
        return userLevel;
    }

    public ProviderEnum getPid() {
        return pid;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    @Override
    public String toString() {
        return "ReceiptUser{" +
                "rid='" + rid + '\'' +
                ", pid=" + pid +
                ", userLevel=" + userLevel +
                '}';
    }
}