package com.noqapp.view.form;

import org.apache.commons.lang3.StringUtils;

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
public final class MerchantRegistrationForm {

    private String phone;
    private String firstName;
    private String lastName;
    private String mail;
    private String birthday;
    private String gender;
    private String countryShortName;
    private String timeZone;
    private String password;
    private boolean accountExists;
    private boolean acceptsAgreement;

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

    /**
     * During registration make sure all the email ids are lowered case.
     *
     * @return
     */
    public String getMail() {
        return StringUtils.lowerCase(mail);
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public void setCountryShortName(String countryShortName) {
        this.countryShortName = countryShortName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "MerchantRegistrationForm [" +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ", " +
                "mail=" + mail + ", " +
                "password=" + password + "]";
    }
}
