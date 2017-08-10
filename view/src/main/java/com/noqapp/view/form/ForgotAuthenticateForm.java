package com.noqapp.view.form;

/**
 * User: hitender
 * Date: 5/3/17 1:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ForgotAuthenticateForm {

    private String password;
    private String passwordSecond;
    private String queueUserId;
    private String authenticationKey;

    private ForgotAuthenticateForm() {
    }

    public static ForgotAuthenticateForm newInstance() {
        return new ForgotAuthenticateForm();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSecond() {
        return passwordSecond;
    }

    public void setPasswordSecond(String passwordSecond) {
        this.passwordSecond = passwordSecond;
    }

    public String getQueueUserId() {
        return queueUserId;
    }

    public ForgotAuthenticateForm setQueueUserId(String queueUserId) {
        this.queueUserId = queueUserId;
        return this;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public ForgotAuthenticateForm setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
        return this;
    }

    public boolean isEqual() {
        return password.equals(this.passwordSecond);
    }
}