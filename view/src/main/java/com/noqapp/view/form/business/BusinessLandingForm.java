package com.noqapp.view.form.business;

import com.noqapp.domain.BizStoreEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/7/16 11:52 PM
 */
public class BusinessLandingForm {

    private String bizName;
    private List<BizStoreEntity> bizStores;

    public String getBizName() {
        return bizName;
    }

    public BusinessLandingForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public List<BizStoreEntity> getBizStores() {
        return bizStores;
    }

    public BusinessLandingForm setBizStores(List<BizStoreEntity> bizStores) {
        this.bizStores = bizStores;
        return this;
    }
}
