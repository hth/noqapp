package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.ExternalAccessEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * hitender
 * 2/4/18 7:59 PM
 */
public class ExternalAccessForm {

    private ScrubbedInput action;
    private ScrubbedInput id;

    public ScrubbedInput getAction() {
        return action;
    }

    public ExternalAccessForm setAction(ScrubbedInput action) {
        this.action = action;
        return this;
    }

    public ScrubbedInput getId() {
        return id;
    }

    public ExternalAccessForm setId(ScrubbedInput id) {
        this.id = id;
        return this;
    }

    private List<ExternalAccessEntity> externalAccesses = new ArrayList<>();

    public List<ExternalAccessEntity> getExternalAccesses() {
        return externalAccesses;
    }

    public ExternalAccessForm setExternalAccesses(List<ExternalAccessEntity> externalAccesses) {
        this.externalAccesses = externalAccesses;
        return this;
    }

    public String decodeId() {
        return new String(Base64.getDecoder().decode(id.getText()), StandardCharsets.ISO_8859_1);
    }
}
