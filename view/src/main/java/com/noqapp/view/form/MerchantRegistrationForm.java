package com.noqapp.view.form;

import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.MailTypeEnum;

import org.junit.jupiter.api.Assertions;

import java.beans.Transient;
import java.io.Serializable;

/**
 * User: hitender
 * Date: 11/25/16 8:56 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class MerchantRegistrationForm implements Serializable {

    private String phone;
    private ScrubbedInput firstName;
    private ScrubbedInput lastName;
    private ScrubbedInput mail;
    private ScrubbedInput birthday;
    private ScrubbedInput gender;
    private ScrubbedInput password;
    private boolean notAdult;
    private boolean accountExists;
    private boolean acceptsAgreement;

    private String captcha;

    /* After mail has been sent when user requested password recover. */
    private MailTypeEnum mailSendState;

    private MerchantRegistrationForm() {
    }

    public static MerchantRegistrationForm newInstance() {
        return new MerchantRegistrationForm();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ScrubbedInput getFirstName() {
        return firstName;
    }

    public MerchantRegistrationForm setFirstName(ScrubbedInput firstName) {
        this.firstName = firstName;
        return this;
    }

    public ScrubbedInput getLastName() {
        return lastName;
    }

    public MerchantRegistrationForm setLastName(ScrubbedInput lastName) {
        this.lastName = lastName;
        return this;
    }

    public ScrubbedInput getMail() {
        return mail;
    }

    public MerchantRegistrationForm setMail(ScrubbedInput mail) {
        this.mail = mail;
        return this;
    }

    public ScrubbedInput getBirthday() {
        return birthday;
    }

    public MerchantRegistrationForm setBirthday(ScrubbedInput birthday) {
        this.birthday = birthday;
        return this;
    }

    public ScrubbedInput getGender() {
        return gender;
    }

    public MerchantRegistrationForm setGender(ScrubbedInput gender) {
        this.gender = gender;
        return this;
    }

    public ScrubbedInput getPassword() {
        return password;
    }

    public MerchantRegistrationForm setPassword(ScrubbedInput password) {
        this.password = password;
        return this;
    }

    public boolean isNotAdult() {
        return notAdult;
    }

    public MerchantRegistrationForm setNotAdult(boolean notAdult) {
        this.notAdult = notAdult;
        return this;
    }

    public boolean isAccountExists() {
        return accountExists;
    }

    public void setAccountExists(boolean accountExists) {
        this.accountExists = accountExists;
    }

    public boolean isAcceptsAgreement() {
        return acceptsAgreement;
    }

    public void setAcceptsAgreement(boolean acceptsAgreement) {
        this.acceptsAgreement = acceptsAgreement;
    }

    public String getCaptcha() {
        return captcha;
    }

    public MerchantRegistrationForm setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public MailTypeEnum getMailSendState() {
        return mailSendState;
    }

    public MerchantRegistrationForm setMailSendState(MailTypeEnum mailSendState) {
        this.mailSendState = mailSendState;
        return this;
    }

    @Transient
    public String findCountryShortFromPhone() {
        Assertions.assertNotNull(phone, "Phone should be not null and contain +");
        return Formatter.getCountryShortNameFromCountryCode(Formatter.findCountryCode(phone));
    }

    @Override
    public String toString() {
        return "MerchantRegistrationForm [" +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "mail=" + mail + ", " +
                "password=" + password + "]";
    }
}
