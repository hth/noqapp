package com.noqapp.view.form.emp.medical;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.medical.domain.PathologyEntity;

import java.util.List;

/**
 * hitender
 * 4/7/18 7:19 PM
 */
public class PathologyForm {
    //When editing
    private ScrubbedInput id;
    private ScrubbedInput name;
    private ScrubbedInput category;
    private ScrubbedInput description;

    private List<PathologyEntity> pathologies;

    public ScrubbedInput getId() {
        return id;
    }

    public PathologyForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    public ScrubbedInput getName() {
        return name;
    }

    public PathologyForm setName(ScrubbedInput name) {
        this.name = name;
        return this;
    }

    public ScrubbedInput getCategory() {
        return category;
    }

    public PathologyForm setCategory(ScrubbedInput category) {
        this.category = category;
        return this;
    }

    public ScrubbedInput getDescription() {
        return description;
    }

    public PathologyForm setDescription(ScrubbedInput description) {
        this.description = description;
        return this;
    }

    public List<PathologyEntity> getPathologies() {
        return pathologies;
    }

    public PathologyForm setPathologies(List<PathologyEntity> pathologies) {
        this.pathologies = pathologies;
        return this;
    }
}
