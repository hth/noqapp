package com.noqapp.view.form.business;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.json.JsonQueuePersonList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hitender
 * 2/11/20 2:56 PM
 */
public class MedicalDocumentUploadForm {
    private static final Logger LOG = LoggerFactory.getLogger(MedicalDocumentUploadForm.class);

    private JsonQueuePersonList jsonQueuePersonList;
    private BizStoreEntity bizStore;

    public JsonQueuePersonList getJsonQueuePersonList() {
        return jsonQueuePersonList;
    }

    public MedicalDocumentUploadForm setJsonQueuePersonList(JsonQueuePersonList jsonQueuePersonList) {
        this.jsonQueuePersonList = jsonQueuePersonList;
        return this;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public MedicalDocumentUploadForm setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
        return this;
    }
}
