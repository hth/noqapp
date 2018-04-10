package com.noqapp.view.form.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.medical.domain.RadiologyEntity;

import java.util.List;

/**
 * hitender
 * 4/7/18 7:19 PM
 */
public class RadiologyForm {
    //When editing
    private ScrubbedInput id;

    private ScrubbedInput name;
    private ScrubbedInput category;
    
    private List<RadiologyEntity> radiologies;

    public ScrubbedInput getId() {
        return id;
    }

    public RadiologyForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    public ScrubbedInput getName() {
        return name;
    }

    public RadiologyForm setName(ScrubbedInput name) {
        this.name = name;
        return this;
    }

    public ScrubbedInput getCategory() {
        return category;
    }

    public RadiologyForm setCategory(ScrubbedInput category) {
        this.category = category;
        return this;
    }

    public List<RadiologyEntity> getRadiologies() {
        return radiologies;
    }

    public RadiologyForm setRadiologies(List<RadiologyEntity> radiologies) {
        this.radiologies = radiologies;
        return this;
    }
}
