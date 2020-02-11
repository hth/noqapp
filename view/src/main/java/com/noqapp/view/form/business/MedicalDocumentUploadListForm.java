package com.noqapp.view.form.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 2/11/20 2:59 PM
 */
public class MedicalDocumentUploadListForm {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDocumentUploadListForm.class);

    private String businessName;
    private List<MedicalDocumentUploadForm> medicalDocumentUploadForms = new ArrayList<>();

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
}
