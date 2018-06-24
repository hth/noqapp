package com.noqapp.view.form;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.TokenQueueEntity;
import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StoreManagerForm {

    private String bizName;
    private Map<BusinessTypeEnum, BusinessTypeEnum> businessTypeMap = new HashMap<>();
    private List<BizStoreEntity> bizStores = new ArrayList<>();
    private Map<String, TokenQueueEntity> tokenQueues = new HashMap<>();

    public String getBizName() {
        return bizName;
    }

    public StoreManagerForm setBizName(String bizName) {
        this.bizName = bizName;
        return this;
    }

    public Map<BusinessTypeEnum, BusinessTypeEnum> getBusinessTypeMap() {
        return businessTypeMap;
    }

    /**
     * Managers not allowed to add store. Specially Manager with business DO not allowed to add store.
     * This condition of allowing managers to modify store is not supported or asked for yet. It can be completely
     * be removed.
     *
     * @param businessTypes
     * @return
     */
    public StoreManagerForm populateBusinessTypeMaps(List<BusinessTypeEnum> businessTypes) {
        businessTypeMap = businessTypes.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
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
