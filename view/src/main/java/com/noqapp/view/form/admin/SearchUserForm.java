package com.noqapp.view.form.admin;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.AccountInactiveReasonEnum;
import com.noqapp.domain.types.ActionTypeEnum;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * hitender
 * 2018-12-07 13:47
 */
public class SearchUserForm {

    private ScrubbedInput qid;
    private ScrubbedInput phone;
    private boolean dependent;
    private ScrubbedInput guardianPhone;
    private ScrubbedInput displayName;
    private AccountInactiveReasonEnum accountInactiveReason;
    private ActionTypeEnum status;

    private Map<String, String> accountInactiveReasons;
    private boolean noUserFound;

    public ScrubbedInput getQid() {
        return qid;
    }

    public SearchUserForm setQid(ScrubbedInput qid) {
        this.qid = qid;
        return this;
    }

    public ScrubbedInput getPhone() {
        return phone;
    }

    public SearchUserForm setPhone(ScrubbedInput phone) {
        this.phone = phone;
        return this;
    }

    public boolean isDependent() {
        return dependent;
    }

    public ScrubbedInput getGuardianPhone() {
        return guardianPhone;
    }

    public SearchUserForm setGuardianPhone(ScrubbedInput guardianPhone) {
        this.guardianPhone = guardianPhone;
        if (StringUtils.isNotBlank(guardianPhone.getText())) {
            this.dependent = true;
        }
        return this;
    }

    public ScrubbedInput getDisplayName() {
        return displayName;
    }

    public SearchUserForm setDisplayName(ScrubbedInput displayName) {
        this.displayName = displayName;
        return this;
    }

    public AccountInactiveReasonEnum getAccountInactiveReason() {
        return accountInactiveReason;
    }

    public SearchUserForm setAccountInactiveReason(AccountInactiveReasonEnum accountInactiveReason) {
        this.accountInactiveReason = accountInactiveReason;
        return this;
    }

    public ActionTypeEnum getStatus() {
        return status;
    }

    public SearchUserForm setStatus(boolean status) {
        this.status = status ? ActionTypeEnum.ACTIVE : ActionTypeEnum.INACTIVE;
        return this;
    }

    public Map<String, String> getAccountInactiveReasons() {
        return accountInactiveReasons;
    }

    public SearchUserForm setAccountInactiveReasons(Map<String, String> accountInactiveReasons) {
        this.accountInactiveReasons = accountInactiveReasons;
        return this;
    }

    public boolean isNoUserFound() {
        return noUserFound;
    }

    public SearchUserForm setNoUserFound(boolean noUserFound) {
        this.noUserFound = noUserFound;
        return this;
    }
}
