package com.noqapp.view.form;

import com.noqapp.common.utils.Formatter;
import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.MailTypeEnum;

import org.springframework.data.annotation.Transient;

import org.junit.jupiter.api.Assertions;

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

    /* These codes are for mail OTP verification. Used when registering new agent. */
    private String code1;
    private String code2;
    private String code3;
    private String code4;
    private String code5;
    private String code6;

    /* After mail has been sent when user requested password recover. */
    private MailTypeEnum mailSendState;
    private String phoneCountryCode;

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

    public String getCode1() {
        return code1;
    }

    public MerchantRegistrationForm setCode1(String code1) {
        this.code1 = code1;
        return this;
    }

    public String getCode2() {
        return code2;
    }

    public MerchantRegistrationForm setCode2(String code2) {
        this.code2 = code2;
        return this;
    }

    public String getCode3() {
        return code3;
    }

    public MerchantRegistrationForm setCode3(String code3) {
        this.code3 = code3;
        return this;
    }

    public String getCode4() {
        return code4;
    }

    public MerchantRegistrationForm setCode4(String code4) {
        this.code4 = code4;
        return this;
    }

    public String getCode5() {
        return code5;
    }

    public MerchantRegistrationForm setCode5(String code5) {
        this.code5 = code5;
        return this;
    }

    public String getCode6() {
        return code6;
    }

    public MerchantRegistrationForm setCode6(String code6) {
        this.code6 = code6;
        return this;
    }

    public MailTypeEnum getMailSendState() {
        return mailSendState;
    }

    public MerchantRegistrationForm setMailSendState(MailTypeEnum mailSendState) {
        this.mailSendState = mailSendState;
        return this;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    @Transient
    public String findCountryShortFromPhone() {
        Assertions.assertNotNull(phone, "Phone should be not null and contain +");
        return Formatter.getCountryShortNameFromCountryCode(Formatter.findCountryCode(phone));
    }

    @Transient
    public String getCode() {
        return code1 + code2 + code3 + code4 + code5 + code6;
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
