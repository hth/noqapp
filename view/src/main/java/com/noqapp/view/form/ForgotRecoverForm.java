package com.noqapp.view.form;

import com.noqapp.utils.ScrubbedInput;

/**
 * User: hitender
 * Date: 5/3/17 1:06 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class ForgotRecoverForm {

    private ScrubbedInput mail;
    private String captcha;
    private ScrubbedInput origin;

    private ForgotRecoverForm() {
    }

    public static ForgotRecoverForm newInstance() {
        return new ForgotRecoverForm();
    }

    public ScrubbedInput getMail() {
        return mail;
    }

    public void setMail(ScrubbedInput mail) {
        this.mail = mail;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public ScrubbedInput getOrigin() {
        return origin;
    }

    public ForgotRecoverForm setOrigin(ScrubbedInput origin) {
        this.origin = origin;
        return this;
    }
}