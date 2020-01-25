package com.noqapp.view.form;

import com.noqapp.domain.helper.NameDatePair;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: hitender
 * Date: 6/24/18 8:29 PM
 */
public class ProfessionalProfileEditForm implements Serializable {
    private boolean professionalProfile;

    private String qid;
    private String action;
    private String name;
    private String monthYear;
    private Set<NameDatePair> nameDatePairs = new LinkedHashSet<>();

    public String getQid() {
        return qid;
    }

    public ProfessionalProfileEditForm setQid(String qid) {
        this.qid = qid;
        return this;
    }

    public boolean isProfessionalProfile() {
        return professionalProfile;
    }

    public ProfessionalProfileEditForm setProfessionalProfile(boolean professionalProfile) {
        this.professionalProfile = professionalProfile;
        return this;
    }

    public String getAction() {
        return action;
    }

    public ProfessionalProfileEditForm setAction(String action) {
        this.action = action;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProfessionalProfileEditForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public ProfessionalProfileEditForm setMonthYear(String monthYear) {
        this.monthYear = monthYear;
        return this;
    }

    public Set<NameDatePair> getNameDatePairs() {
        return nameDatePairs;
    }

    public ProfessionalProfileEditForm setNameDatePairs(Set<NameDatePair> nameDatePairs) {
        this.nameDatePairs = nameDatePairs;
        return this;
    }

    public boolean isNotValid() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(monthYear);
    }
}
