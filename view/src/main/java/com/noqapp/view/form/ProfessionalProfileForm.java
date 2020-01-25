package com.noqapp.view.form;

import com.noqapp.domain.helper.NameDatePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 6/11/18 3:24 PM
 */
public class ProfessionalProfileForm implements Serializable {
    private String qid;
    private boolean professionalProfile;
    private String practiceStart;
    private String aboutMe;

    private List<NameDatePair> awards = new ArrayList<>();
    private List<NameDatePair> education = new ArrayList<>();
    private List<NameDatePair> licenses = new ArrayList<>();

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
