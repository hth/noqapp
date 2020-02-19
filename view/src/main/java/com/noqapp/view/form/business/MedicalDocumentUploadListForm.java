package com.noqapp.view.form.business;

import com.noqapp.domain.json.JsonQueuedPerson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hitender
 * 2/11/20 2:59 PM
 */
public class MedicalDocumentUploadListForm {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDocumentUploadListForm.class);

    private String businessName;
    private List<MedicalDocumentUploadForm> medicalDocumentUploadForms = new ArrayList<>();

    /** For historical upload. */
    private Map<String, List<JsonQueuedPerson>> jsonQueuedPersonMap = new HashMap<>();

    public String getBusinessName() {
        return businessName;
    }

    public MedicalDocumentUploadListForm setBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public List<MedicalDocumentUploadForm> getMedicalDocumentUploadForms() {
        return medicalDocumentUploadForms;
    }

    public MedicalDocumentUploadListForm setMedicalDocumentUploadForms(List<MedicalDocumentUploadForm> medicalDocumentUploadForms) {
        this.medicalDocumentUploadForms = medicalDocumentUploadForms;
        return this;
    }

    public MedicalDocumentUploadListForm addMedicalDocumentUploadForms(MedicalDocumentUploadForm medicalDocumentUploadForm) {
        this.medicalDocumentUploadForms.add(medicalDocumentUploadForm);
        return this;
    }

    public Map<String, List<JsonQueuedPerson>> getJsonQueuedPersonMap() {
        return jsonQueuedPersonMap;
    }

    public MedicalDocumentUploadListForm setJsonQueuedPersonMap(Map<String, List<JsonQueuedPerson>> jsonQueuedPersonMap) {
        this.jsonQueuedPersonMap = jsonQueuedPersonMap;
        return this;
    }
}
