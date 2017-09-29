package com.noqapp.view.form;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.TokenQueueEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreManagerForm {

    private String bizName;
    private List<BizStoreEntity> bizStores = new ArrayList<>();
    private Map<String, TokenQueueEntity> tokenQueues = new HashMap<>();

    public String getBizName() {
        return bizName;
    }

    public StoreManagerForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public List<BizStoreEntity> getBizStores() {
        return bizStores;
    }

    public StoreManagerForm addBizStore(BizStoreEntity bizStore) {
        this.bizStores.add(bizStore);
        return this;
    }

    public Map<String, TokenQueueEntity> getTokenQueues() {
        return tokenQueues;
    }

    public StoreManagerForm addTokenQueue(String codeQR, TokenQueueEntity tokenQueue) {
        this.tokenQueues.put(codeQR, tokenQueue);
        return this;
    }
}
