package com.noqapp.view.form.admin;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.AccountInactiveReasonEnum;

import java.util.Map;

/**
 * hitender
 * 2018-12-07 13:47
 */
public class SearchUserForm {

    private ScrubbedInput qid;
    private ScrubbedInput displayName;
    private AccountInactiveReasonEnum accountInactiveReason;
    private String status;

    Map<String, String> accountInactiveReasons;

    public ScrubbedInput getQid() {
        return qid;
    }

    public SearchUserForm setQid(ScrubbedInput qid) {
        this.qid = qid;
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

    public String getStatus() {
        return status;
    }

    public SearchUserForm setStatus(boolean status) {
        this.status = status ? "ACTIVE" : "IN-ACTIVE";
        return this;
    }

    public Map<String, String> getAccountInactiveReasons() {
        return accountInactiveReasons;
    }

    public SearchUserForm setAccountInactiveReasons(Map<String, String> accountInactiveReasons) {
        this.accountInactiveReasons = accountInactiveReasons;
        return this;
    }
}
