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

    /* Unique Queue User ID throughout the system. This will never change. */
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

    @Field("OC")
    private int otpCount;

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

    public UserAccountEntity setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public UserAccountEntity setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserAccountEntity setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserAccountEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserAccountEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
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

    public UserAccountEntity setAccountValidated(boolean accountValidated) {
        if (!accountValidated && this.accountValidated) {
            /*
             * Update accountValidatedBeginDate with new date when account has been validated previously or else
             * keep the date same as this can lead to continuous increase in account validation timeout period.
             */
            accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
        }

        this.accountValidated = accountValidated;
        return this;
    }

    public Date getAccountValidatedBeginDate() {
        return accountValidatedBeginDate;
    }

    public UserAccountEntity setAccountValidatedBeginDate() {
        this.accountValidatedBeginDate = DateUtil.midnight(DateTime.now().plusDays(1).toDate());
        return this;
    }

    public boolean isPhoneValidated() {
        return phoneValidated;
    }

    public UserAccountEntity setPhoneValidated(boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
        return this;
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

    public UserAccountEntity setAccountInactiveReason(AccountInactiveReasonEnum accountInactiveReason) {
        this.accountInactiveReason = accountInactiveReason;
        return this;
    }

    public int getOtpCount() {
        return otpCount;
    }

    public UserAccountEntity setOtpCount(int otpCount) {
        this.otpCount = otpCount;
        return this;
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

