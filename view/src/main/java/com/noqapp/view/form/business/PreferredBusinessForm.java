package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.PreferredBusinessEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 8/13/18 5:43 PM
 */
public class PreferredBusinessForm {

    private ScrubbedInput businessNameToAdd;
    private String recordId;
    List<PreferredBusinessEntity> preferredBusinesses = new ArrayList<>();

    public ScrubbedInput getBusinessNameToAdd() {
        return businessNameToAdd;
    }

    public PreferredBusinessForm setBusinessNameToAdd(ScrubbedInput businessNameToAdd) {
        this.businessNameToAdd = businessNameToAdd;
        return this;
    }

    public String getRecordId() {
        return recordId;
    }

    public PreferredBusinessForm setRecordId(String recordId) {
        this.recordId = recordId;
        return this;
    }

    public List<PreferredBusinessEntity> getPreferredBusinesses() {
        return preferredBusinesses;
    }

    public PreferredBusinessForm setPreferredBusinesses(List<PreferredBusinessEntity> preferredBusinesses) {
        this.preferredBusinesses = preferredBusinesses;
        return this;
    }

    public PreferredBusinessForm addPreferredBusiness(PreferredBusinessEntity preferredBusiness) {
        this.preferredBusinesses.add(preferredBusiness);
        return this;
    }
}
