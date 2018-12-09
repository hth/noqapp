package com.noqapp.view.form.business;

import com.noqapp.domain.types.DataProtectionEnum;

import java.util.Map;

/**
 * hitender
 * 2018-12-09 16:15
 */
public class DataProtectionForm {
    private DataProtectionEnum dataProtectionForSupervisor;
    private DataProtectionEnum dataProtectionForManager;

    private Map<String, String> dataProtections;

    public DataProtectionEnum getDataProtectionForSupervisor() {
        return dataProtectionForSupervisor;
    }

    public DataProtectionForm setDataProtectionForSupervisor(DataProtectionEnum dataProtectionForSupervisor) {
        this.dataProtectionForSupervisor = dataProtectionForSupervisor;
        return this;
    }

    public DataProtectionEnum getDataProtectionForManager() {
        return dataProtectionForManager;
    }

    public DataProtectionForm setDataProtectionForManager(DataProtectionEnum dataProtectionForManager) {
        this.dataProtectionForManager = dataProtectionForManager;
        return this;
    }

    public Map<String, String> getDataProtections() {
        return dataProtections;
    }

    public DataProtectionForm setDataProtections(Map<String, String> dataProtections) {
        this.dataProtections = dataProtections;
        return this;
    }
}
