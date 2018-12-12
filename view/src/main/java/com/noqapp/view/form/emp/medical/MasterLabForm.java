package com.noqapp.view.form.emp.medical;

import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;
import com.noqapp.view.form.FileUploadForm;

import java.util.List;

/**
 * hitender
 * 2018-12-11 18:12
 */
public class MasterLabForm extends FileUploadForm {

    private HealthCareServiceEnum healthCareService;

    private List<HealthCareServiceEnum> healthCareServices = HealthCareServiceEnum.asList();

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public MasterLabForm setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }

    public List<HealthCareServiceEnum> getHealthCareServices() {
        return healthCareServices;
    }

    public MasterLabForm setHealthCareServices(List<HealthCareServiceEnum> healthCareServices) {
        this.healthCareServices = healthCareServices;
        return this;
    }
}
