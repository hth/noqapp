package com.noqapp.view.form.business;

import com.noqapp.domain.types.DataVisibilityEnum;

import java.util.Map;

/**
 * hitender
 * 2018-12-09 16:15
 */
public class DataVisibilityForm {
    private DataVisibilityEnum dataVisibilityForSupervisor;
    private DataVisibilityEnum dataVisibilityForManager;

    private Map<String, String> dataVisibilities;

    public DataVisibilityEnum getDataVisibilityForSupervisor() {
        return dataVisibilityForSupervisor;
    }

    public DataVisibilityForm setDataVisibilityForSupervisor(DataVisibilityEnum dataVisibilityForSupervisor) {
        this.dataVisibilityForSupervisor = dataVisibilityForSupervisor;
        return this;
    }

    public DataVisibilityEnum getDataVisibilityForManager() {
        return dataVisibilityForManager;
    }

    public DataVisibilityForm setDataVisibilityForManager(DataVisibilityEnum dataVisibilityForManager) {
        this.dataVisibilityForManager = dataVisibilityForManager;
        return this;
    }

    public Map<String, String> getDataVisibilities() {
        return dataVisibilities;
    }

    public DataVisibilityForm setDataVisibilities(Map<String, String> dataVisibilities) {
        this.dataVisibilities = dataVisibilities;
        return this;
    }
}
