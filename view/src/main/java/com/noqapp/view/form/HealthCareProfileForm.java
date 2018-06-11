package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.helper.NameDatePair;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * hitender
 * 6/11/18 3:24 PM
 */
public class HealthCareProfileForm {

    private ScrubbedInput webProfileId;
    private Date practiceStart;

    private List<NameDatePair> education = new LinkedList<>();
    private List<NameDatePair> licenses = new LinkedList<>();
    private List<NameDatePair> awards = new LinkedList<>();

    public ScrubbedInput getWebProfileId() {
        return webProfileId;
    }

    public HealthCareProfileForm setWebProfileId(ScrubbedInput webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    public Date getPracticeStart() {
        return practiceStart;
    }

    public HealthCareProfileForm setPracticeStart(Date practiceStart) {
        this.practiceStart = practiceStart;
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

    public List<NameDatePair> getAwards() {
        return awards;
    }

    public HealthCareProfileForm setAwards(List<NameDatePair> awards) {
        this.awards = awards;
        return this;
    }
}
