package com.noqapp.view.form;

import com.noqapp.domain.helper.NameDatePair;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 6/11/18 3:24 PM
 */
public class HealthCareProfileForm {
    private boolean healthCareProfile;
    private Date practiceStart;

    private List<NameDatePair> awards = new LinkedList<>();
    private List<NameDatePair> education = new LinkedList<>();
    private List<NameDatePair> licenses = new LinkedList<>();

    public boolean isHealthCareProfile() {
        return healthCareProfile;
    }

    public HealthCareProfileForm setHealthCareProfile(boolean healthCareProfile) {
        this.healthCareProfile = healthCareProfile;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public HealthCareProfileForm setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public HealthCareProfileForm setAwards(List<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public List<NameDatePair> getEducation() {
        return education;
    }

    public HealthCareProfileForm setEducation(List<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<NameDatePair> getLicenses() {
        return licenses;
    }

    public HealthCareProfileForm setLicenses(List<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }
}
