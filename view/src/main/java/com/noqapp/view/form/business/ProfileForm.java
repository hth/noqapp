package com.noqapp.view.form.business;

/**
 * Contains all profile related info here.
 *
 * hitender
 * 12/22/17 9:45 AM
 */
public class ProfileForm {

    private String mail;
    private String firstName;
    private String lastName;
    private String phone;

    private boolean submitState;
    private boolean accountValidated;

    public String getMail() {
        return mail;
    }

    public ProfileForm setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ProfileForm setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ProfileForm setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ProfileForm setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public boolean isSubmitState() {
        return submitState;
    }

    public ProfileForm setSubmitState(boolean submitState) {
        this.submitState = submitState;
        return this;
    }

    public boolean isAccountValidated() {
        return accountValidated;
    }

    public ProfileForm setAccountValidated(boolean accountValidated) {
        this.accountValidated = accountValidated;
        return this;
    }
}
