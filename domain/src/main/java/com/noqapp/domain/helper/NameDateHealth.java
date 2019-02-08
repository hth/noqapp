package com.noqapp.domain.helper;

import com.noqapp.domain.types.catgeory.HealthCareServiceEnum;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * hitender
 * 2019-02-08 11:51
 */
public class NameDateHealth extends NameDatePair {

    @Field("HS")
    private HealthCareServiceEnum healthCareService;

    public HealthCareServiceEnum getHealthCareService() {
        return healthCareService;
    }

    public NameDateHealth setHealthCareService(HealthCareServiceEnum healthCareService) {
        this.healthCareService = healthCareService;
        return this;
    }
}
