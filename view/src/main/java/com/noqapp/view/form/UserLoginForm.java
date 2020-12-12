package com.noqapp.view.form;

import org.apache.commons.lang3.StringUtils;

/**
 * User: hitender
 * Date: 11/19/16 7:13 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class UserLoginForm {

    private String emailId;
    private String password;

    public UserLoginForm() {
    }

    public String getEmailId() {
        return StringUtils.lowerCase(emailId);
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
