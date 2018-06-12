package com.noqapp.view.form;

import com.noqapp.domain.helper.NameDatePair;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * hitender
 * 6/11/18 3:24 PM
 */
public class ProfessionalProfileForm {
    private boolean professionalProfile;
    private Date practiceStart;

    private List<NameDatePair> awards = new LinkedList<>();
    private List<NameDatePair> education = new LinkedList<>();
    private List<NameDatePair> licenses = new LinkedList<>();

    public boolean isProfessionalProfile() {
        return professionalProfile;
    }

    public ProfessionalProfileForm setProfessionalProfile(boolean professionalProfile) {
        this.professionalProfile = professionalProfile;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public ProfessionalProfileForm setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public ProfessionalProfileForm setAwards(List<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public List<NameDatePair> getEducation() {
        return education;
    }

    public ProfessionalProfileForm setEducation(List<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public List<NameDatePair> getLicenses() {
        return licenses;
    }

    public ProfessionalProfileForm setLicenses(List<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }
}
