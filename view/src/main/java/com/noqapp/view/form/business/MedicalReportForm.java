package com.noqapp.view.form.business;

import com.noqapp.common.utils.ScrubbedInput;
import com.noqapp.domain.types.medical.LabCategoryEnum;
import com.noqapp.view.form.FileUploadForm;

import java.util.List;

/**
 * hitender
 * 2019-02-24 18:28
 */
public class MedicalReportForm extends FileUploadForm {

    private ScrubbedInput storeId;
    private ScrubbedInput transactionId;
    private LabCategoryEnum labCategory;
    private ScrubbedInput codeQR;

    private String recordReferenceId;
    private List<String> images;
    private ScrubbedInput filename;

    public ScrubbedInput getStoreId() {
        return storeId;
    }

    public MedicalReportForm setStoreId(ScrubbedInput storeId) {
        this.storeId = storeId;
        return this;
    }

    public ScrubbedInput getTransactionId() {
        return transactionId;
    }

    public MedicalReportForm setTransactionId(ScrubbedInput transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public LabCategoryEnum getLabCategory() {
        return labCategory;
    }

    public MedicalReportForm setLabCategory(LabCategoryEnum labCategory) {
        this.labCategory = labCategory;
        return this;
    }

    public ScrubbedInput getCodeQR() {
        return codeQR;
    }

    public MedicalReportForm setCodeQR(ScrubbedInput codeQR) {
        this.codeQR = codeQR;
        return this;
    }

    public String getRecordReferenceId() {
        return recordReferenceId;
    }

    public MedicalReportForm setRecordReferenceId(String recordReferenceId) {
        this.recordReferenceId = recordReferenceId;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public MedicalReportForm setImages(List<String> images) {
        this.images = images;
        return this;
    }

    public ScrubbedInput getFilename() {
        return filename;
    }

    public MedicalReportForm setFilename(ScrubbedInput filename) {
        this.filename = filename;
        return this;
    }
}
