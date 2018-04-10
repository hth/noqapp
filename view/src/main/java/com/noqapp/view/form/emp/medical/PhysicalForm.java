package com.noqapp.view.form.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.medical.domain.PhysicalEntity;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * hitender
 * 4/7/18 7:19 PM
 */
public class PhysicalForm {
    //When editing
    private ScrubbedInput id;

    private ScrubbedInput name;
    private ScrubbedInput[] normalRange = new ScrubbedInput[2];
    private ScrubbedInput description;

    private List<PhysicalEntity> physicals;

    public ScrubbedInput getId() {
        return id;
    }

    public PhysicalForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    public ScrubbedInput getName() {
        return name;
    }

    public PhysicalForm setName(ScrubbedInput name) {
        this.name = name;
        return this;
    }

    public ScrubbedInput[] getNormalRange() {
        return normalRange;
    }

    public PhysicalForm setNormalRange(ScrubbedInput[] normalRange) {
        this.normalRange = normalRange;
        return this;
    }

    public ScrubbedInput getDescription() {
        return description;
    }

    public PhysicalForm setDescription(ScrubbedInput description) {
        this.description = description;
        return this;
    }

    public List<PhysicalEntity> getPhysicals() {
        return physicals;
    }

    public PhysicalForm setPhysicals(List<PhysicalEntity> physicals) {
        this.physicals = physicals;
        return this;
    }

    @Transient
    public String[] getNormalRangeAsString() {
        String[] range = new String[normalRange.length];
        int i = 0;
        for(ScrubbedInput s : normalRange) {
            range[i] = s.getText();
            i ++;
        }

        return range;
    }
}
