package com.noqapp.view.form;

import com.noqapp.domain.helper.NameDatePair;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * hitender
 * 6/11/18 3:24 PM
 */
public class ProfessionalProfileForm implements Serializable {
    private String qid;
    private boolean professionalProfile;
    private String practiceStart;
    private String aboutMe;

    private Set<NameDatePair> awards = new HashSet<>();
    private Set<NameDatePair> education = new HashSet<>();
    private Set<NameDatePair> licenses = new HashSet<>();

    public String getQid() {
        return qid;
    }

    public ProfessionalProfileForm setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public boolean isProfessionalProfile() {
        return professionalProfile;
    }

    public ProfessionalProfileForm setProfessionalProfile(boolean professionalProfile) {
        this.professionalProfile = professionalProfile;
        return this;
    }

    public String getPracticeStart() {
        return practiceStart;
    }

    public ProfessionalProfileForm setPracticeStart(String practiceStart) {
        this.practiceStart = practiceStart;
        return this;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public ProfessionalProfileForm setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
        return this;
    }

    public Set<NameDatePair> getAwards() {
        return awards;
    }

    public ProfessionalProfileForm setAwards(Set<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }

    public Set<NameDatePair> getEducation() {
        return education;
    }

    public ProfessionalProfileForm setEducation(Set<NameDatePair> education) {
        this.education = education;
        return this;
    }

    public Set<NameDatePair> getLicenses() {
        return licenses;
    }

    public ProfessionalProfileForm setLicenses(Set<NameDatePair> licenses) {
        this.licenses = licenses;
        return this;
    }
}
