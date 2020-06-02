package com.noqapp.domain;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.domain.types.RoleEnum;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 11/18/16 6:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "USER_ACCOUNT")
@CompoundIndexes ({
        @CompoundIndex (name = "user_account_role_idx", def = "{'UID': 1, 'RE': 1}", unique = true),
        @CompoundIndex (name = "user_account_qid_idx", def = "{'QID': 1}", unique = true),
        @CompoundIndex (name = "user_account_uid_idx", def = "{'UID': 1}", unique = true)
})
public class UserAccountEntity extends BaseEntity {

    public static final String BLANK_SPACE = " ";

    /* Unique Queue User Id throughout the system. This will never change. */
    @NotNull
    @Field ("QID")
    private String queueUserId;

    /**
     * This is set by third party and cannot be relied on.
     * It could be either matching provider's Id or email.
     */
    @NotNull
    @Field ("UID")
    private String userId;

    @Field ("DN")
    private String displayName;

    @Field ("FN")
    private String firstName;

    @Field ("LN")
    private String lastName;

    @Field ("RE")
    private Set<RoleEnum> roles;

    @Field ("AV")
    private boolean accountValidated;

    @Field ("AVD")
    private Date accountValidatedBeginDate;

    @Field ("PV")
    private boolean phoneValidated;

    @Field ("AIR")
    private AccountInactiveReasonEnum accountInactiveReason;
    
    @DBRef
    @Field ("USER_AUTHENTICATION")
    private UserAuthenticationEntity userAuthentication;

    private UserAccountEntity() {
        super();
        roles = new LinkedHashSet<>();
        roles.add(RoleEnum.ROLE_CLIENT);
    }

    private UserAccountEntity(
            String queueUserId,
            String userId,
            String firstName,
            String lastName
    ) {
        this();
        this.queueUserId = queueUserId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = getName();
    }

    public static UserAccountEntity newInstance(
            String queueUserId,
            String userId,
            String firstName,
            String lastName
    ) {
        return new UserAccountEntity(queueUserId, userId, firstName, lastName);
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public void setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEnum> roles) {
        this.roles = roles;
    }

    public void addRole(RoleEnum role) {
        this.roles.add(role);
    }

    public UserAuthenticationEntity getUserAuthentication() {
        return userAuthentication;
    }

    public void setUserAuthentication(UserAuthenticationEntity userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public void setAccountValidated(boolean accountValidated) {
        if (!accountValidated && this.accountValidated) {
            /*
             * Update accountValidatedBeginDate with new date when account has been validated previously or else
             * keep the date same as this can lead to continuous increase in account validation timeout period.
             */
            accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
        }

        this.accountValidated = accountValidated;
    }

    public Date getAccountValidatedBeginDate() {
        return accountValidatedBeginDate;
    }

    public void setAccountValidatedBeginDate() {
        this.accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
    }

    public boolean isPhoneValidated() {
        return phoneValidated;
    }

    public void setPhoneValidated(boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
    }

    public String getName() {
        if (StringUtils.isNotBlank(firstName)) {
            if (StringUtils.isNotBlank(lastName)) {
                return StringUtils.trim(firstName + BLANK_SPACE + lastName);
            } else {
                return firstName;
            }
        }
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        return userId;
    }

    public AccountInactiveReasonEnum getAccountInactiveReason() {
        return accountInactiveReason;
    }

    public void setAccountInactiveReason(AccountInactiveReasonEnum accountInactiveReason) {
        this.accountInactiveReason = accountInactiveReason;
    }

    @Override
    public String toString() {
        return "UserAccountEntity{" +
            "queueUserId='" + queueUserId + '\'' +
            ", userId='" + userId + '\'' +
            ", displayName='" + displayName + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", roles=" + roles +
            ", accountValidated=" + accountValidated +
            ", accountValidatedBeginDate=" + accountValidatedBeginDate +
            ", phoneValidated=" + phoneValidated +
            ", accountInactiveReason=" + accountInactiveReason +
            ", userAuthentication=" + userAuthentication +
            '}';
    }
}

